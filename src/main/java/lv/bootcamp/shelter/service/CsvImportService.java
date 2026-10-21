package lv.bootcamp.shelter.service;

import lombok.extern.slf4j.Slf4j;
import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CsvImportService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public ImportResult importAnimals(Path inputPath) {
        log.info("Starting import from {}", inputPath);

        List<Animal> allAnimals = new ArrayList<>();

        int skippedRows = 0;
        List<Integer>invalidRowNumbers=new ArrayList<>();

        List<String> lines;
        try {
            lines = Files.readAllLines(inputPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Could not read file {}", inputPath, e);
            return new ImportResult(allAnimals, 0,invalidRowNumbers);
        }

        // i = 0 is the header row, so start from 1
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineNumber = i + 1;

            //-1 keeps empty fields the endf of the row
            String[] parts = line.split(",", -1);

            if (parts.length != 5) {
                log.warn("Skipping line {}:expected 5 columns but found {} ", lineNumber, parts.length);
                skippedRows++;
                invalidRowNumbers.add(lineNumber);
                continue;
            }

            String name = parts[0].trim();
            String species = parts[1].trim();
            String ageText = parts[2].trim();
            String vaccinatedText = parts[3].trim();
            String dateText = parts[4].trim();

            if (name.isBlank() || species.isEmpty()) {
                log.warn("Skipping line {}: name or species is missing", lineNumber);
                skippedRows++;
                invalidRowNumbers.add(lineNumber);
                continue;
            }
            Integer age = null;
            if (!ageText.isEmpty()) {

                try {
                    age = Integer.parseInt(ageText);
                } catch (NumberFormatException e) {
                    log.warn("Skipping line {}: age '{}'  is  not number ", lineNumber, ageText);
                    skippedRows++;
                    invalidRowNumbers.add(lineNumber);
                    continue;
                }
                if (age <= 0) {
                    log.warn("Skipping line {}: age '{}' must be positive ", lineNumber, ageText);
                    skippedRows++;
                    invalidRowNumbers.add(lineNumber);
                    continue;
                }
            }
            if (!vaccinatedText.equals("true") && !vaccinatedText.equals("false")) {
                log.warn("Skipping line {}: vaccinated must be true or false but was '{}'", lineNumber, vaccinatedText);
                skippedRows++;
                invalidRowNumbers.add(lineNumber);
                continue;
            }
            boolean vaccinated = Boolean.parseBoolean(vaccinatedText);

            LocalDate intakeDate;


            try {
                intakeDate = LocalDate.parse(dateText, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                log.warn("Skipping line {}: intakeDate '{}' is not valid (dd.MM.yyyy)", lineNumber, dateText);
                skippedRows++;
                invalidRowNumbers.add(lineNumber);
                continue;

            }
            allAnimals.add(new Animal(name, species, age, vaccinated, intakeDate));

        }
        log.info("Import finished {} imported , {} skipped", allAnimals.size(),skippedRows);
        return  new ImportResult(allAnimals,skippedRows,invalidRowNumbers);

    }
}
