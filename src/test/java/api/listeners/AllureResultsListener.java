package api.listeners;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Простая реализация RunListener: при завершении прогона архивирует папку target/allure-results
 * в target/allure-results-<timestamp>.zip
 */
public class AllureResultsListener extends RunListener {

    private static final String RESULTS_DIR = "target/allure-results";
    private static final String TARGET_DIR = "target";

    @Override
    public void testRunFinished(Result result) throws Exception {
        Path resultsPath = Paths.get(RESULTS_DIR);
        if (Files.exists(resultsPath) && Files.isDirectory(resultsPath)) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            Path zipPath = Paths.get(TARGET_DIR, "allure-results-" + timestamp + ".zip");
            try {
                zipDirectory(resultsPath, zipPath);
                System.out.println("Allure results archived to: " + zipPath.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("Не удалось заархивировать allure-results: " + e.getMessage());
            }
        } else {
            System.out.println("Папка allure-results не найдена: " + resultsPath.toAbsolutePath());
        }
    }

    // Утилитарный метод zip
    private void zipDirectory(Path sourceDirPath, Path zipFilePath) throws IOException {
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
            Files.walk(sourceDirPath)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourceDirPath.relativize(path).toString().replace("\\", "/"));
                        try (InputStream is = Files.newInputStream(path)) {
                            zs.putNextEntry(zipEntry);
                            byte[] buffer = new byte[4096];
                            int len;
                            while ((len = is.read(buffer)) > 0) {
                                zs.write(buffer, 0, len);
                            }
                            zs.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
    }

    // Остальные методы не обязательно переопределять, оставим дефолтные
    @Override
    public void testStarted(Description description) throws Exception {
        // nop
    }
}
