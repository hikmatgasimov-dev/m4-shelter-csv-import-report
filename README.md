## How to run

Requirements: Java 21 and Maven 3.8+.

```bash
mvn compile exec:java
```

You can also run `ShelterCsvApplication` from IntelliJ.

The app scans `src/main/resources/data` for every `.csv` file (including sub folders) and processes each one. For every file it writes two reports to the `output/` folder:

- `upload-report-<csv name>-<yyyy-MM-dd_HH-mm-ss>.txt` - the text report
- `upload-report-<csv name>-<yyyy-MM-dd_HH-mm-ss>.json` - a JSON summary (Gson)

Invalid rows are skipped and logged with SLF4J warnings.

## Processed files registry

The names of imported files are saved in `output/processed-files.txt`. A file that is already listed there is skipped on the next run, so running the app twice does not create duplicate reports.

To import a file again, delete `output/processed-files.txt` (or remove that file name from it) and run the app.

The files in `output/` are generated, so they are listed in `.gitignore` and are not committed.

## Sample report

Result for `intake.csv`:

```
=== Shelter Intake Report ===
Date generated: 21.10.2026

Total imported: 31
Total skipped:  6
Invalid rows: 8, 9, 10, 34, 35, 36

--- Unique species ---
Bird, Cat, Dog, Rabbit

--- Per-species breakdown ---
Bird:   6 total, 2 vaccinated
Cat:    8 total, 7 vaccinated
Dog:    11 total, 10 vaccinated
Rabbit: 6 total, 3 vaccinated

--- Oldest per species ---
Bird:   Charlie (age 4)
Cat:    Cleo (age 5)
Dog:    Shadow (age 9)
Rabbit: Gizmo (age 5)

--- Needs Vet input to determine age ---
Milo(Dog), Leo(Cat), Pepper(Bird)
```

## What was implemented

Required steps:

1. Parse and validate CSV rows (`CsvImportService`)
2. Store valid data in collections (`ShelterAnalyticsService`)
3. Calculate summary values with stream pipelines (`ShelterAnalyticsService`, `ShelterReportData`)
4. Export the report with `BufferedWriter` (`ReportExportService`)

Stretch goals:

- Report sections sorted alphabetically by species with a `Comparator` on the map entries
- `Optional` for animals with a missing age (`Animal.getAgeOptional()`)
- JSON summary export with Gson (`JsonExportService`)
- All `.csv` files in a folder processed with `Files.walk()`
- Timestamped report file names and a processed-files registry (`ProcessedFilesRegistry`)
- Invalid row numbers listed in the report