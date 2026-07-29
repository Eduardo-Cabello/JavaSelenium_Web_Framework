package TestCases;

import Base.Utilities;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        Utilities.captureStepScreenshot("Test failed", "The test should pass", "The test failed with an assertion or exception");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Utilities.captureStepScreenshot("Test skipped", "The test should run", "The test was skipped");
    }
}
