package lv.bootcamp.shelter;

import lombok.extern.slf4j.Slf4j;
import lv.bootcamp.shelter.service.CsvImportService;
import lv.bootcamp.shelter.service.ProcessedFilesRegistry;
import lv.bootcamp.shelter.service.data.ImportResult;
import lv.bootcamp.shelter.service.ReportExportService;
import lv.bootcamp.shelter.service.ShelterAnalyticsService;
import lv.bootcamp.shelter.service.data.ShelterReportData;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lv.bootcamp.shelter.service.JsonExportService;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Stream;

import java.nio.file.Path;
@Slf4j
public class ShelterCsvApplication {

    public static void main(String[] args) {

        Path inputFolder = Path.of("src", "main", "resources", "data");
        Path registryPath = Path.of("output", "processed-files.txt");

        ProcessedFilesRegistry registry = new ProcessedFilesRegistry(registryPath);
        CsvImportService importService = new CsvImportService();
        ShelterAnalyticsService analyticsService = new ShelterAnalyticsService();
        ReportExportService reportExportService = new ReportExportService();
        JsonExportService jsonExportService = new JsonExportService();

        // find all .csv files in the input folder (and its sub folders)
        List<Path> csvFiles;
        try (Stream<Path> paths = Files.walk(inputFolder)) {
            csvFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".csv"))
                    .sorted()
                    .toList();
        } catch (IOException e) {
            log.error("Could not scan folder {}", inputFolder, e);
            return;
        }
        log.info("Found {} csv file(s) in {}", csvFiles.size(), inputFolder);

        for (Path csvFile : csvFiles) {
            String inputFileName = csvFile.getFileName().toString();

            if (registry.isProcessed(inputFileName)) {
                log.info("File {} was already processed, skipping", inputFileName);
                continue;
            }

            // the csv name is part of the report name, so two files in the same second do not collide
            String baseName = inputFileName.substring(0, inputFileName.length() - ".csv".length());
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            Path outputPath = Path.of("output", "upload-report-" + baseName + "-" + timestamp + ".txt");
            Path jsonPath = Path.of("output", "upload-report-" + baseName + "-" + timestamp + ".json");
            ImportResult importResult = importService.importAnimals(csvFile);
            ShelterReportData reportData = analyticsService.buildReportData(importResult);
            reportExportService.writeReport(outputPath, reportData);
            jsonExportService.writeJson(jsonPath, reportData);
            registry.markProcessed(inputFileName);
        }
    }
}

