package Base;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Utilities extends Base {

    private static final List<StepRecord> STEP_LOG = new ArrayList<>();
    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public static String getProperties(String key) {
        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream("src/test/resources/data.properties")) {
            prop.load(fis);
        } catch (IOException e) {
            System.out.println("Unable to read properties file: " + e.getMessage());
        }
        return prop.getProperty(key);
    }

    // Clear the step log before each test execution.
    public static void clearStepLog() {
        STEP_LOG.clear();
    }

    // Capture a screenshot for each test step and store the expected vs actual result.
    public static void captureStepScreenshot(String stepName, String expectedResult, String actualResult) {
        String safeStepName = stepName == null ? "step" : stepName.replaceAll("[^a-zA-Z0-9._-]", "_");
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        Path screenshotPath = Paths.get("target", "reports", "screenshots", safeStepName + "_" + timestamp + ".png");

        try {
            Files.createDirectories(screenshotPath.getParent());
        } catch (IOException e) {
            System.out.println("Unable to create screenshot folder: " + e.getMessage());
        }

        try {
            if (driver != null && driver instanceof TakesScreenshot) {
                java.io.File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Files.copy(screenshotFile.toPath(), screenshotPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                createPlaceholderScreenshot(screenshotPath);
            }
        } catch (Exception e) {
            System.out.println("Unable to capture screenshot: " + e.getMessage());
            try {
                createPlaceholderScreenshot(screenshotPath);
            } catch (IOException ignored) {
                // Ignore fallback errors
            }
        }

        STEP_LOG.add(new StepRecord(stepName, expectedResult, actualResult, screenshotPath.toString()));
    }

    // Generate a Word document containing the execution summary and screenshots for each step.
    public static void generateWordReport(String testName, String outputPath) {
        generateWordReport(testName, outputPath, null);
    }

    public static void generateWordReport(String testName, String outputPath, String finalStatus) {
        Path output = Paths.get(outputPath);
        try {
            Files.createDirectories(output.getParent());
        } catch (IOException e) {
            System.out.println("Unable to create report folder: " + e.getMessage());
        }

        String resolvedStatus = finalStatus == null || finalStatus.isBlank()
                ? (STEP_LOG.isEmpty() ? "Unknown" : "Passed")
                : finalStatus;

        try (XWPFDocument document = new XWPFDocument(); FileOutputStream fos = new FileOutputStream(output.toFile())) {
            XWPFParagraph title = document.createParagraph();
            XWPFRun titleRun = title.createRun();
            titleRun.setBold(true);
            titleRun.setFontSize(20);
            titleRun.setText("Automation Test Report");

            XWPFParagraph summary = document.createParagraph();
            XWPFRun summaryRun = summary.createRun();
            summaryRun.setText("Test Name: " + (testName == null ? "Unknown" : testName));
            summaryRun.addBreak();
            summaryRun.setText("Execution Date: " + LocalDateTime.now());
            summaryRun.addBreak();
            summaryRun.setText("Total Steps: " + STEP_LOG.size());
            summaryRun.addBreak();
            summaryRun.setBold(true);
            summaryRun.setText("Final Status: " + resolvedStatus);

            for (StepRecord step : STEP_LOG) {
                XWPFParagraph stepParagraph = document.createParagraph();
                XWPFRun stepRun = stepParagraph.createRun();
                stepRun.setBold(true);
                stepRun.setText("Step: " + (step.stepName == null ? "Unnamed step" : step.stepName));
                stepRun.addBreak();
                stepRun.setText("Expected: " + (step.expectedResult == null ? "" : step.expectedResult));
                stepRun.addBreak();
                stepRun.setText("Actual: " + (step.actualResult == null ? "" : step.actualResult));

                if (step.screenshotPath != null && Files.exists(Paths.get(step.screenshotPath))) {
                    addImageToDocument(document, Paths.get(step.screenshotPath));
                }
            }

            document.write(fos);
            System.out.println("Word report generated at: " + output.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Unable to generate Word report", e);
        }
    }

    public static String sanitizeFileName(String value) {
        if (value == null || value.isBlank()) {
            return "test_report";
        }
        return value.replaceAll("[^a-zA-Z0-9._-]", " ").trim().replaceAll("\\s+", "_");
    }

    private static void createPlaceholderScreenshot(Path filePath) throws IOException {
        BufferedImage image = new BufferedImage(800, 450, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 800, 450);
        graphics.setColor(Color.DARK_GRAY);
        graphics.drawRect(10, 10, 780, 430);
        graphics.setColor(Color.BLUE);
        graphics.drawString("Screenshot unavailable", 280, 220);
        graphics.dispose();
        ImageIO.write(image, "png", filePath.toFile());
    }

    private static void addImageToDocument(XWPFDocument document, Path imagePath) throws IOException {
        try (InputStream inputStream = Files.newInputStream(imagePath)) {
            byte[] imageBytes = inputStream.readAllBytes();
            XWPFParagraph imageParagraph = document.createParagraph();
            XWPFRun imageRun = imageParagraph.createRun();
            try {
                imageRun.addPicture(
                        new ByteArrayInputStream(imageBytes),
                        XWPFDocument.PICTURE_TYPE_PNG,
                        imagePath.getFileName().toString(),
                        Units.toEMU(320),
                        Units.toEMU(180)
                );
            } catch (InvalidFormatException e) {
                throw new IOException("Unable to insert screenshot into the Word document", e);
            }
        }
    }

    private static class StepRecord {
        private final String stepName;
        private final String expectedResult;
        private final String actualResult;
        private final String screenshotPath;

        private StepRecord(String stepName, String expectedResult, String actualResult, String screenshotPath) {
            this.stepName = stepName;
            this.expectedResult = expectedResult;
            this.actualResult = actualResult;
            this.screenshotPath = screenshotPath;
        }
    }
}
