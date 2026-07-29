package TestCases;

import Base.Base;
import Base.Utilities;
import Base.ZephyrUploader;
import DemoWeb.Actions.DemoActions;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

public class DemoTestCase {
    private WebDriver driver;
    private DemoActions ac;
    // Control whether to create Word evidence reports for each test. Can be overridden per-test.
    protected boolean createEvidence = true;

    @BeforeMethod
    public void setUp() {
        Utilities.clearStepLog();
        // Read default from properties (can be overridden in test methods)
        String evidenceProp = Utilities.getProperties("evidence.enabled");
        if (evidenceProp != null && !evidenceProp.isBlank()) {
            try {
                createEvidence = Boolean.parseBoolean(evidenceProp.trim());
            } catch (Exception ignored) {
            }
        }
        // Propagate to Utilities so screenshot capture respects this flag
        Utilities.setEvidenceEnabled(createEvidence);
        driver = Base.startDriver("chrome");
        ac = new DemoActions();
        driver.manage().deleteAllCookies();
        Base.navigate();
        Utilities.captureStepScreenshot("Open application", "The application page should be visible", "The application page is loaded");
    }

    @Ignore("Def: 1120")
    @Test(description = "Login with valid credentials")
    public void CP001() {
        Utilities.captureStepScreenshot("Fill login data", "The login form should be completed", "The login form is populated");
        ac.fillLoginData();
        Utilities.captureStepScreenshot("Submit login", "The login action should be executed", "The login action is triggered");
        ac.click_SingInBtn();
    }

    @Test(description = "Login with invalid credentials")
    public void CP002() {
        Utilities.captureStepScreenshot("Submit incorrect credentials", "An error message should appear", "The invalid login flow starts");
        ac.fillIncorrectLoginData();
        Utilities.captureStepScreenshot("Submit login", "The login action should be executed", "The login action is triggered");
        ac.click_SingInBtn();
        String errTxt = ac.getLoginError();
        Utilities.captureStepScreenshot("Validate error message", "The error message should contain 'Incorrect'", "The captured error text is validated");
        Assert.assertTrue(errTxt.contains("Incorrect"),"Validation Failed");
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

        // Upload test results to Zephyr Scale if enabled in properties
        try {
            ZephyrUploader.uploadSurefireReports();
        } catch (Exception e) {
            System.err.println("Zephyr upload failed: " + e.getMessage());
        }

        if (driver != null) {
            driver.quit();
        }
    }
}
