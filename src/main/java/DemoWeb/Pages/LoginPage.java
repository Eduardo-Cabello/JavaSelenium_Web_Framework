package DemoWeb.Pages;

import Base.Base;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public class LoginPage extends Base {

    @FindBy(id = "username")
    private WebElement txtUsername;

    @FindBy(id = "password")
    private WebElement txtPassword;

    @FindBy(xpath = "//select[@class=\"form-control\"]")
    private WebElement lstRole;

    @FindBy(id = "terms")
    private WebElement chkTerms;

    @FindBy(id = "signInBtn")
    private WebElement btnSignIn;

    @FindBy(xpath = "//div[contains(@class, 'alert-danger')]")
    private WebElement signInError;

    public LoginPage() {
        AjaxElementLocatorFactory factory = new AjaxElementLocatorFactory(Base.getDriver(), Base.implicitWait);
        PageFactory.initElements(factory, this);
    }

    public void loginWithCredentials(String username, String password, String role) {
        type(txtUsername, username);
        type(txtPassword, password);
        select(lstRole, role);
        click(chkTerms);
    }

    public void submit() {
        click(btnSignIn);
    }

    public String getErrorText() {
        return getAttribute(signInError, "textContent");
    }
}
