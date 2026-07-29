package TestCases;

import Base.Utilities;
import Base.ZephyrUploader;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

public class DemoTestCase extends BaseTest {

    @Ignore("Def: 1120")
    @Test(description = "Login with valid credentials")
    public void CP001() {
        Utilities.captureStepScreenshot("Fill login data", "The login form should be completed", "The login form is populated");
        actions.fillLoginData();
        Utilities.captureStepScreenshot("Submit login", "The login action should be executed", "The login action is triggered");
        actions.click_SingInBtn();
    }

    @Test(description = "Login with invalid credentials")
    public void CP002() {
        Utilities.captureStepScreenshot("Submit incorrect credentials", "An error message should appear", "The invalid login flow starts");
        actions.fillIncorrectLoginData();
        Utilities.captureStepScreenshot("Submit login", "The login action should be executed", "The login action is triggered");
        actions.click_SingInBtn();
        String errTxt = actions.getLoginError();
        Utilities.captureStepScreenshot("Validate error message", "The error message should contain 'Incorrect'", "The captured error text is validated");
        Assert.assertTrue(errTxt.contains("Incorrect"),"Validation Failed");
    }

    @Override
    public void tearDown(ITestResult result) {
        super.tearDown(result);

        try {
            ZephyrUploader.uploadSurefireReports();
        } catch (Exception e) {
            System.err.println("Zephyr upload failed: " + e.getMessage());
        }
    }
}
