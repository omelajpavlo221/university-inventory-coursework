package ua.edu.nupp.kpp.client;

import javafx.concurrent.Task;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.logging.Logger;

public class ImportInventoryTask extends Task<Integer> {
    private static final Logger LOG = Logger.getLogger(ImportInventoryTask.class.getName());
    private static final String EXPECTED_HEADER = "inv_number;name;category;available_qty;criticality_score";

    private final Path file;
    private final HttpInventoryClient api;

    public ImportInventoryTask(Path file, HttpInventoryClient api) {
        this.file = file;
        this.api = api;
    }

    @Override
    protected Integer call() throws Exception {
        LOG.info("Початок фонового CSV-імпорту з файлу: " + file);
        if (!Files.exists(file)) {
            throw new IllegalArgumentException("Файл не знайдено: " + file);
        }

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("CSV-файл порожній");
        }

        String header = lines.get(0).replace("\uFEFF", "").trim();
        if (!EXPECTED_HEADER.equalsIgnoreCase(header)) {
            throw new IllegalArgumentException("Некоректний заголовок CSV. Очікується: " + EXPECTED_HEADER);
        }

        int total = Math.max(1, lines.size() - 1);
        int imported = 0;
        int skipped = 0;

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            String[] p = line.split(";");
            if (p.length != 5) {
                skipped++;
                LOG.warning("Пропущено рядок " + (i + 1) + ": неправильна кількість полів (" + p.length + ")");
                updateProgress(i, total);
                continue;
            }

            try {
                Asset asset = new Asset(
                    null,
                    p[0].trim(),
                    p[1].trim(),
                    p[2].trim(),
                    Integer.parseInt(p[3].trim()),
                    Double.parseDouble(p[4].trim())
                );
                api.addAsset(asset);
                imported++;
            } catch (Exception ex) {
                skipped++;
                LOG.warning("Помилка в рядку " + (i + 1) + ": " + ex.getMessage());
            }

            updateProgress(i, total);
            updateMessage("Імпортовано: " + imported + " із " + total + " (пропущено: " + skipped + ")");
        }

        LOG.info("Завершено CSV-імпорт. Успішно: " + imported + ", пропущено: " + skipped);
        return imported;
    }
}
