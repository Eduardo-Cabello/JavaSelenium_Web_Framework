package DemoWeb.Locators;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class DemoLocators {

    @FindBy (id = "username")
    public WebElement txtusername;

    @FindBy (id = "password")
    public WebElement txtpassword;

    @FindBy (xpath = "//select[@class=\"form-control\"]")
    public WebElement lstform;

    @FindBy (id = "terms")
    public WebElement chekterms;

    @FindBy (id = "signInBtn")
    public WebElement btnsignin;

    @FindBy(xpath = "//div[contains(@class, 'alert-danger')]")
    public WebElement SingInError;
}
