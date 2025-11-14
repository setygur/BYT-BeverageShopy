package persistence.fileioTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import persistence.SerializeException;
import persistence.fileio.FileOut;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileOutTests {
    @TempDir
    Path tempDir;

    @Test
    void writesJson_createsDirectory_andReturnsPath() throws Exception {
        Path targetDir = tempDir.resolve("exports/sub");
        String json = "{\n  \"hello\": \"world\"\n}";

        Path written = FileOut.writeJson(targetDir, "data", json);
        assertTrue(Files.exists(written), "File should be created");
        assertEquals(targetDir.resolve("data.json"), written, "Filename should be baseName + .json");
        String content = Files.readString(written, StandardCharsets.UTF_8);
        assertEquals(json, content, "Written content should match input JSON");
    }

    @Test
    void stripsDotJsonExtension_whenProvided() throws Exception {
        Path targetDir = tempDir.resolve("out");
        String json = "{\"a\":1}";
        Path written = FileOut.writeJson(targetDir, "report.json", json);
        assertEquals(targetDir.resolve("report.json"), written);
        assertTrue(Files.exists(written));
    }

    @Test
    void usesIncrementedNames_whenFileExists() throws Exception {
        Path targetDir = tempDir.resolve("dupes");
        Files.createDirectories(targetDir);

        String json1 = "{\"n\":1}";
        String json2 = "{\"n\":2}";
        String json3 = "{\"n\":3}";

        Path first = FileOut.writeJson(targetDir, "data", json1);
        assertEquals(targetDir.resolve("data.json"), first);

        Path second = FileOut.writeJson(targetDir, "data", json2);
        assertEquals(targetDir.resolve("data (1).json"), second);

        Path third = FileOut.writeJson(targetDir, "data", json3);
        assertEquals(targetDir.resolve("data (2).json"), third);

        assertEquals(json1, Files.readString(first, StandardCharsets.UTF_8));
        assertEquals(json2, Files.readString(second, StandardCharsets.UTF_8));
        assertEquals(json3, Files.readString(third, StandardCharsets.UTF_8));
    }

    @Test
    void throwsIllegalArgument_whenBaseNameBlank() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> FileOut.writeJson(tempDir, "   ", "{}"));
        assertTrue(ex.getMessage().toLowerCase().contains("base name"));
    }

    @Test
    void throwsIllegalArgument_whenJsonNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> FileOut.writeJson(tempDir, "data", null));
        assertTrue(ex.getMessage().toLowerCase().contains("json"));
    }

    @Test
    void wrapsCreateDirectoriesFailure_intoSerializeException() throws Exception {
        Path fileWhereDirShouldBe = tempDir.resolve("notADirectory");
        Files.writeString(fileWhereDirShouldBe, "I am a file", StandardCharsets.UTF_8);

        SerializeException ex = assertThrows(SerializeException.class,
                () -> FileOut.writeJson(fileWhereDirShouldBe, "data", "{}"));
        assertNotNull(ex.getMessage());
    }
}
