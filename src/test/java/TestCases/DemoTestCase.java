package TestCases;

import Base.Base;
import DemoWeb.Actions.DemoActions;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

public class DemoTestCase {
    private WebDriver driver;
    private DemoActions ac;

    @BeforeMethod
    public void setUp() {
        driver = Base.startDriver("chrome");
        ac = new DemoActions();
        driver.manage().deleteAllCookies();
        Base.navigate();
    }

    @Ignore("Def: 1120")
    @Test
    public void CP001() {
        ac.fillLoginData();
        ac.click_SingInBtn();
    }

    @Test
    public void CP002() {
        ac.fillIncorrectLoginData();
        ac.click_SingInBtn();
        String errTxt = ac.getLoginError();
        Assert.assertTrue(errTxt.contains("Incorrect"),"Validation Failed");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
