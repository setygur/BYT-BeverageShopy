package persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonSerializerTests {

    @JsonSerializable
    static class BaseThing {
        String baseName;
        @JsonIgnore
        String secret;

        BaseThing(String baseName, String secret) {
            this.baseName = baseName;
            this.secret = secret;
        }
    }

    @JsonSerializable
    static class Thing extends BaseThing {
        @ObjectList
        public static List<Thing> registry = new ArrayList<>();
        static double baseRate = 2.5;
        String title;
        int count;
        String note;

        Thing(String baseName, String secret, String title, int count, String note) {
            super(baseName, secret);
            this.title = title;
            this.count = count;
            this.note = note;

            registry.add(this);
        }
    }

    @JsonSerializable
    static class EmptyModel {
        @ObjectList
        public static List<EmptyModel> registry = new ArrayList<>();
        static String staticInfo = "should_appear_only_if_class_is_included";
    }

    @BeforeEach
    void reset() {
        Thing.registry.clear();
        Thing.baseRate = 2.5;
        EmptyModel.registry.clear();
    }

    private static String normalize(String s) {
        return s.replaceAll("\\s+", "");
    }


    @Test
    void serializeObjects_includesClassKeyStaticAndObjects_withInheritance_andEscaping_andNulls() throws SerializeException {
        Thing t = new Thing(
                "base\"Name",
                "do_not_serialize_me",
                "Hello \\ \"World\"",
                7,
                null
        );
        String json = JsonSerializer.serializeObjects(List.of(Thing.class, EmptyModel.class));
        String flat = normalize(json);

        assertTrue(flat.startsWith(normalize("{\"models\":{")), "Root 'models' object missing");

        String classKey = "\"" + Thing.class.getName() + "\":{";
        assertTrue(flat.contains(normalize(classKey)), "Class key for Thing missing");

        assertTrue(flat.contains(normalize("\"static\":{\"baseRate\":2.5")), "Static baseRate missing or wrong");
        assertFalse(flat.contains(normalize("\"registry\":")), "Static @ObjectList must NOT be serialized");

        assertTrue(flat.contains(normalize("\"objects\":[")), "Objects array missing");

        assertTrue(flat.contains(normalize("\"title\":\"Hello \\\\ \\\"World\\\"\"")),
                "Escaped title not serialized correctly");
        assertTrue(flat.contains(normalize("\"count\":7")), "Numeric field not serialized");
        assertTrue(flat.contains(normalize("\"note\":null")), "Null field not serialized as null");
        assertTrue(flat.contains(normalize("\"baseName\":\"base\\\"Name\"")),
                "Superclass field baseName not serialized or not escaped");
        assertFalse(flat.contains(normalize("\"secret\":")), "@JsonIgnore field 'secret' must NOT appear");

        assertFalse(flat.contains(normalize("\"" + EmptyModel.class.getName() + "\":{")),
                "EmptyModel with empty registry must be skipped");
    }

    @Test
    void serializeObjects_skipsClassWhenRegistryEmpty() throws SerializeException {
        String json = JsonSerializer.serializeObjects(List.of(Thing.class, EmptyModel.class));
        String flat = normalize(json);

        assertEquals(normalize("{\"models\":{}}"), flat, "Expected empty models map when all registries are empty");
    }

    @Test
    void serializeObjects_withMultipleInstances_listsAllObjects_andKeepsStaticOnce() throws SerializeException {
        Thing t1 = new Thing("A", "ignoreA", "One", 1, "note1");
        Thing t2 = new Thing("B", "ignoreB", "Two", 2, null);

        String json = JsonSerializer.serializeObjects(List.of(Thing.class));
        String flat = normalize(json);

        assertTrue(flat.contains(normalize("\"title\":\"One\"")), "First object missing");
        assertTrue(flat.contains(normalize("\"title\":\"Two\"")), "Second object missing");

        int idx = flat.indexOf(normalize("\"static\":{"));
        assertTrue(idx >= 0, "Static section missing");
        int nextIdx = flat.indexOf(normalize("\"static\":{"), idx + 1);
        assertEquals(-1, nextIdx, "Static section should appear only once per class block");
    }

    @Test
    void serializeObjects_throwsOnInaccessibleStaticFieldRead_isWrapped() {
        @JsonSerializable
        class WithPrivateStatic {
            @ObjectList
            public static List<WithPrivateStatic> registry = new ArrayList<>();
            @SuppressWarnings("unused")
            private static int hidden = 42;

            int x = 1;
            WithPrivateStatic() { registry.add(this); }
        }

        WithPrivateStatic w = new WithPrivateStatic();
        assertDoesNotThrow(() -> JsonSerializer.serializeObjects(List.of(WithPrivateStatic.class)));
        String json = assertDoesNotThrow(() -> JsonSerializer.serializeObjects(List.of(WithPrivateStatic.class)));
        String flat = normalize(json);
        assertTrue(flat.contains(normalize("\"hidden\":42")), "private static field not serialized in 'static'");
    }

    @Test
    void serializeObjects_throwsSerializeException_whenNonSerializableObjectFound() {
        class NotSerializable {
            @ObjectList
            public static List<NotSerializable> registry = new ArrayList<>();
            int y = 5;
            NotSerializable() { registry.add(this); }
        }

        NotSerializable.registry.clear();
        new NotSerializable(); // put one instance in the list

        SerializeException ex = assertThrows(SerializeException.class,
                () -> JsonSerializer.serializeObjects(List.of(NotSerializable.class)));
        assertTrue(ex.getMessage().toLowerCase().contains("not jsonserializable"),
                "Expected SerializeException complaining about @JsonSerializable");
    }
}
