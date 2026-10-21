package lv.bootcamp.shelter.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ShelterReportData;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class JsonExportService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public void writeJson(Path outputPath, ShelterReportData reportData) {
        // a simple summary map: Gson can write maps, lists and numbers without extra setup
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("dateGenerated", LocalDate.now().format(DATE_FORMAT));
        summary.put("totalImported", reportData.importResult().allAnimals().size());
        summary.put("totalSkipped", reportData.importResult().skippedRows());
        summary.put("invalidRows", reportData.importResult().invalidRowNumbers());
        summary.put("uniqueSpecies", reportData.uniqueSpecies());

        Map<String, Object> perSpecies = new TreeMap<>();
        for (String species : reportData.uniqueSpecies()) {
            Map<String, Long> counts = new LinkedHashMap<>();
            counts.put("total", reportData.totalPerSpecies().getOrDefault(species, 0L));
            counts.put("vaccinated", reportData.vaccinatedPerSpecies().getOrDefault(species, 0L));
            perSpecies.put(species, counts);
        }
        summary.put("perSpecies", perSpecies);

        Map<String, Object> oldestPerSpecies = new TreeMap<>();
        for (Map.Entry<String, Animal> entry : reportData.oldestPerSpecies().entrySet()) {
            Map<String, Object> oldest = new LinkedHashMap<>();
            oldest.put("name", entry.getValue().getName());
            oldest.put("age", entry.getValue().getAge());
            oldestPerSpecies.put(entry.getKey(), oldest);
        }
        summary.put("oldestPerSpecies", oldestPerSpecies);
        summary.put("animalsNeedingVetInput", reportData.animalsNeedingVetInput());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            gson.toJson(summary, writer);
            log.info("JSON summary written to {}", outputPath);
        } catch (IOException e) {
            log.error("Could not write JSON summary to {}", outputPath, e);
        }
    }
}