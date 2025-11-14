package persistence.fileioTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import persistence.JsonCtor;
import persistence.ObjectList;
import persistence.fileio.FileIn;
import validation.ValidationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class FileInTests {

    @SuppressWarnings("unused")
    static class BaseModel {
        String base;
        BaseModel(String base) { this.base = base; }
    }

    static class DemoModel extends BaseModel {
        @ObjectList
        public static List<DemoModel> registry = new ArrayList<>();

        static double factor = 1.0;
        String name;
        int count;

        @JsonCtor
        public DemoModel(String base, String name, int count) {
            super(base);
            if (name == null || name.isBlank()) {
                throw new ValidationException("name must not be blank");
            }
            this.name = name;
            this.count = count;
            registry.add(this);
        }

        @Override public String toString() {
            return "DemoModel(" + base + "," + name + "," + count + ")";
        }
    }

    static class ListGuardModel {
        @ObjectList
        public static List<ListGuardModel> registry = new ArrayList<>();
        static String info = "orig";

        @JsonCtor
        public ListGuardModel() {
            registry.add(this);
        }
    }

    @TempDir
    Path tmp;

    @BeforeEach
    void reset() {
        DemoModel.registry.clear();
        DemoModel.factor = 1.0;
        ListGuardModel.registry.clear();
        ListGuardModel.info = "orig";
    }

    private static boolean ctorParamNamesPresent(Class<?> cls) {
        for (Constructor<?> c : cls.getDeclaredConstructors()) {
            if (c.isAnnotationPresent(JsonCtor.class)) {
                for (Parameter p : c.getParameters()) {
                    if (!p.isNamePresent()) return false;
                }
                return true;
            }
        }
        return false;
    }

    @Test
    void loadsValidObjects_andSetsStaticFields_butNotObjectList() throws Exception {
        assumeTrue(ctorParamNamesPresent(DemoModel.class),
                "Compile tests with -parameters so constructor parameter names are available");

        ListGuardModel listGuardModel = new ListGuardModel(); //add one to the registry

        String json = """
            {
              "models": {
                "%s": {
                  "static": { "factor": 3.5 },
                  "objects": [
                    { "base": "B1", "name": "Alice", "count": 2 },
                    { "base": "B2", "name": "Bob",   "count": 5 }
                  ]
                },
                "%s": {
                  "static": {
                    "info": "updated",
                    "registry": []
                  },
                  "objects": [
                    { }
                  ]
                }
              }
            }
            """.formatted(DemoModel.class.getName(), ListGuardModel.class.getName());

        Path in = tmp.resolve("in.json");
        Files.writeString(in, json, StandardCharsets.UTF_8);
        FileIn.readJson(in, List.of(DemoModel.class, ListGuardModel.class));

        assertEquals(3.5, DemoModel.factor, 1e-9);
        assertEquals(2, DemoModel.registry.size());
        assertEquals("Alice", DemoModel.registry.get(0).name);
        assertEquals("Bob",   DemoModel.registry.get(1).name);
        assertEquals("updated", ListGuardModel.info);
        assertEquals(2, ListGuardModel.registry.size(), "Static should not overwrite @ObjectList");
    }

    @Test
    void skipsUnknownClasses_andContinues() throws Exception {
        assumeTrue(ctorParamNamesPresent(DemoModel.class),
                "Compile tests with -parameters so constructor parameter names are available");

        String json = """
            {
              "models": {
                "x.y.z.MissingClass": {
                  "static": { "whatever": 1 },
                  "objects": [ { "some": "thing" } ]
                },
                "%s": {
                  "static": { "factor": 2.0 },
                  "objects": [
                    { "base": "B", "name": "Ok", "count": 3 }
                  ]
                }
              }
            }
            """.formatted(DemoModel.class.getName());

        Path in = tmp.resolve("unknown.json");
        Files.writeString(in, json, StandardCharsets.UTF_8);

        assertDoesNotThrow(() -> FileIn.readJson(in, List.of(DemoModel.class)));
        assertEquals(1, DemoModel.registry.size());
        assertEquals("Ok", DemoModel.registry.get(0).name);
        assertEquals(2.0, DemoModel.factor, 1e-9);
    }

    @Test
    void skipsObjectsThatThrowValidationException() throws Exception {
        assumeTrue(ctorParamNamesPresent(DemoModel.class),
                "Compile tests with -parameters so constructor parameter names are available");

        String json = """
            {
              "models": {
                "%s": {
                  "static": { "factor": 4.2 },
                  "objects": [
                    { "base": "B1", "name": "   ",  "count": 1 },
                    { "base": "B2", "name": "Good", "count": 2 }
                  ]
                }
              }
            }
            """.formatted(DemoModel.class.getName());

        Path in = tmp.resolve("validation.json");
        Files.writeString(in, json, StandardCharsets.UTF_8);
        FileIn.readJson(in, List.of(DemoModel.class));

        assertEquals(1, DemoModel.registry.size());
        assertEquals("Good", DemoModel.registry.get(0).name);
        assertEquals(4.2, DemoModel.factor, 1e-9);
    }

    @Test
    void missingPrimitiveParameterCausesSkip() throws Exception {
        assumeTrue(ctorParamNamesPresent(DemoModel.class),
                "Compile tests with -parameters so constructor parameter names are available");

        String json = """
            {
              "models": {
                "%s": {
                  "static": { "factor": 1.5 },
                  "objects": [
                    { "base": "B1", "name": "One" },
                    { "base": "B2", "name": "Two", "count": null }
                  ]
                }
              }
            }
            """.formatted(DemoModel.class.getName());

        Path in = tmp.resolve("primitive.json");
        Files.writeString(in, json, StandardCharsets.UTF_8);
        FileIn.readJson(in, List.of(DemoModel.class));

        assertEquals(0, DemoModel.registry.size());
        assertEquals(1.5, DemoModel.factor, 1e-9, "Static still applies even if objects skipped");
    }

    @Test
    void toleratesEmptyModelsAndNonMapRoots() throws Exception {
        String json1 = """
            { "models": { } }
            """;
        Path p1 = tmp.resolve("empty.json");
        Files.writeString(p1, json1, StandardCharsets.UTF_8);
        assertDoesNotThrow(() -> FileIn.readJson(p1, List.of(DemoModel.class)));
        assertEquals(0, DemoModel.registry.size());

        String json2 = """
            [ 1, 2, 3 ]
            """;
        Path p2 = tmp.resolve("arrayroot.json");
        Files.writeString(p2, json2, StandardCharsets.UTF_8);
        assertDoesNotThrow(() -> FileIn.readJson(p2, List.of(DemoModel.class)));
        assertEquals(0, DemoModel.registry.size());
    }
}
