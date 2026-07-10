package TestCases;

import Base.Base;
import DemoWeb.Actions.DemoActions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

public class DemoTestCase {
    WebDriver driver = Base.startDriver("chrome");
    DemoActions ac = new DemoActions();

    @BeforeClass
    public void tearUp(){
        Base.navigate();
    }

    @Test
    public void CP001() {
        ac.fillLoginData();
        ac.click_SingInBtn();
    }

    @AfterClass
    public void tearDown(){
        //Closes all tabs and windows
        driver.quit();
        // driver.close(); Closes the current tab only
    }
}
