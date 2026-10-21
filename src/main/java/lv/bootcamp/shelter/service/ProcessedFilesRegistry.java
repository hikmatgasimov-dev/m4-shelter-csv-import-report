package lv.bootcamp.shelter.service;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Slf4j
public class ProcessedFilesRegistry {

    private final Path registryPath;

    public ProcessedFilesRegistry(Path registryPath) {
        this.registryPath = registryPath;
    }

    public boolean isProcessed(String fileName) {
        if (!Files.exists(registryPath)) {
            return false;
        }
        try {
            List<String> processedFiles = Files.readAllLines(registryPath, StandardCharsets.UTF_8);
            return processedFiles.contains(fileName);
        } catch (IOException e) {
            log.error("Could not read registry {}", registryPath, e);
            return false;
        }
    }

    public void markProcessed(String fileName) {
        try (BufferedWriter writer = Files.newBufferedWriter(registryPath, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(fileName);
            writer.newLine();
        } catch (IOException e) {
            log.error("Could not update registry {}", registryPath, e);
        }
    }
}