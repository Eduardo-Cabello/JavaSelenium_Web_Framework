package TestCases;

import Base.Base;
import Base.ConfigManager;
import Base.DriverFactory;
import Base.Utilities;
import DemoWeb.Actions.DemoActions;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {
    protected WebDriver driver;
    protected DemoActions actions;
    protected boolean createEvidence;

    @BeforeMethod
    public void setUp() {
        Utilities.clearStepLog();
        createEvidence = ConfigManager.getBoolean("evidence.enabled", true);
        Utilities.setEvidenceEnabled(createEvidence);

        driver = DriverFactory.createDriver();
        Base.setDriver(driver);
        actions = new DemoActions();
        driver.manage().deleteAllCookies();
        Base.navigate();
        Utilities.captureStepScreenshot("Open application", "The application page should be visible", "The application page is loaded");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        String testId = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        String testName = (description == null || description.isBlank()) ? testId : testId + " - " + description;
        String reportFileName = Utilities.sanitizeFileName(testId + " " + (description == null ? "" : description));
        String reportPath = "target/reports/" + reportFileName + ".docx";
        String finalStatus = result.getStatus() == ITestResult.SUCCESS ? "Passed" : "Failed";

        if (createEvidence) {
            Utilities.captureStepScreenshot("Finish test execution", "The test should complete", finalStatus);
            Utilities.generateWordReport(testName, reportPath, finalStatus);
        } else {
            System.out.println("Skipping final screenshot and Word evidence generation for " + testId);
        }

        if (driver != null) {
            driver.quit();
        }
    }
}
