package TestCases;

import Base.Utilities;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

public class UtilitiesReportTest {

    @Test
    public void shouldCreateWordReportWithSteps() {
        Utilities.clearStepLog();

        Utilities.captureStepScreenshot("Open application", "Login page is visible", "Login page is visible");
        Utilities.captureStepScreenshot("Enter credentials", "Credentials are entered", "Credentials are entered");

        String reportPath = "target/test-output/word-report-test.docx";
        Utilities.generateWordReport("Demo Test", reportPath);

        Assert.assertTrue(new File(reportPath).exists(), "Word report should be created");
    }
}
