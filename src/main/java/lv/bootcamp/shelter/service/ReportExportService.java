package lv.bootcamp.shelter.service;

import lombok.extern.slf4j.Slf4j;
import lv.bootcamp.shelter.service.data.ShelterReportData;
import lv.bootcamp.shelter.model.Animal;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
@Slf4j
public class ReportExportService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public void writeReport(Path outputPath, ShelterReportData reportData) {
        // TODO Step 4:

        try(BufferedWriter writer= Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)){

            writer.write("=== Shelter Intake Report ===");
            writer.newLine();
            writer.write("Date generated: " + LocalDate.now().format(DATE_FORMAT));
            writer.newLine();
            writer.newLine();

            writer.write("Total imported: " + reportData.importResult().allAnimals().size());
            writer.newLine();
            writer.write("Total skipped:  " + reportData.importResult().skippedRows());
            writer.newLine();
            writer.newLine();

            writer.write("--- Unique species ---");
            writer.newLine();
            writer.write(String.join(", ", reportData.uniqueSpecies()));
            writer.newLine();
            writer.newLine();

            writer.write("--- Per-species breakdown ---");
            writer.newLine();
            for (String species:reportData.uniqueSpecies()){

                long total=reportData.totalPerSpecies().getOrDefault(species, 0L);
                long vaccinated=reportData.vaccinatedPerSpecies().getOrDefault(species, 0L);
                writer.write(String.format("%-8s%d total, %d vaccinated", species + ":", total, vaccinated));

                writer.newLine();

            }
            writer.newLine();

            writer.write("--- Oldest per species ---");
            writer.newLine();
            for (String species : reportData.uniqueSpecies()) {
                Animal oldest = reportData.oldestPerSpecies().get(species);
                if (oldest != null) {
                    writer.write(String.format("%-8s%s (age %d)", species + ":", oldest.getName(), oldest.getAge()));
                    writer.newLine();
                }
            }
            writer.newLine();

            writer.write("--- Needs Vet input to determine age ---");
            writer.newLine();
            writer.write(String.join(", ", reportData.animalsNeedingVetInput()));
            writer.newLine();
            log.info("Report written  to {}",outputPath);

        } catch (IOException e) {
            log.error("Could not write report to {}", outputPath, e);
        }

    }
}
