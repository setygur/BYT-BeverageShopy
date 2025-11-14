package persistence.fileio;

import persistence.SerializeException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileOut {

    public static Path writeJson(Path directory, String baseName, String json) {
        if (baseName == null || baseName.isBlank()) {
            throw new IllegalArgumentException("Base name must not be blank");
        }
        if (json == null) {
            throw new IllegalArgumentException("JSON content must not be null");
        }
        try{
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new SerializeException(e.getMessage());
        }
        String cleanBase = stripJsonExtension(baseName.trim());
        Path targetPath = uniqueJsonPath(directory, cleanBase);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetPath.toFile(), false))) {
            writer.write(json);
        } catch (IOException e) {
            throw new SerializeException(e.getMessage());
        }

        return targetPath;
    }

    private static Path uniqueJsonPath(Path dir, String base) {
        Path candidate = dir.resolve(base + ".json");
        int i = 1;
        while (Files.exists(candidate)) {
            candidate = dir.resolve(base + " (" + i + ").json");
            i++;
        }
        return candidate;
    }

    private static String stripJsonExtension(String name) {
        if (name.toLowerCase().endsWith(".json")) {
            return name.substring(0, name.length() - 5);
        }
        return name;
    }
}
