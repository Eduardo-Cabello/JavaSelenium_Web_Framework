package DemoWeb.Actions;


import Base.Base;
import Base.Utilities;
import DemoWeb.Locators.DemoLocators;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public class DemoActions extends Base {
    protected DemoLocators dl;

    public DemoActions(){
        dl= new DemoLocators();
        AjaxElementLocatorFactory factory = new AjaxElementLocatorFactory(driver , implicitWait);
        PageFactory.initElements(factory,dl);
    }

    public void fillLoginData(){
        String user = Utilities.getProperties("username");
        String pass = Utilities.getProperties("password");
        String role = Utilities.getProperties("role");

        type(dl.txtusername,user);
        type(dl.txtpassword,pass);
        select(dl.lstform,role);
        click(dl.chekterms);
    }

    public void click_SingInBtn(){click(dl.btnsignin);}


}
