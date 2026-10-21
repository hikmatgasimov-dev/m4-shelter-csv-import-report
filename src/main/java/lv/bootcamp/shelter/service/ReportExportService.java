package lv.bootcamp.shelter.service;

import lombok.extern.slf4j.Slf4j;
import lv.bootcamp.shelter.service.data.ShelterReportData;

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


            writer.write("----Unique species----");
            writer.newLine();
            writer.write(String.join(",",reportData.uniqueSpecies()));
            writer.newLine();
            writer.newLine();

            log.info("Report written  to {}",outputPath);

            writer.write("Total important +"+ reportData.importResult().allAnimals().size());
            writer.newLine();
            writer.write("Total skipped: "+ reportData.importResult().skippedRows());
            writer.newLine();
            writer.newLine();
        } catch (IOException e) {
            log.error("Could not write report to {}", outputPath, e);
        }

    }
}
