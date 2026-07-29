package DemoWeb.Actions;


import Base.Base;
import Base.ConfigManager;
import DemoWeb.Pages.LoginPage;

public class DemoActions extends Base {
    private final LoginPage loginPage;

    public DemoActions(){
        loginPage = new LoginPage();
    }

    public void fillLoginData(){
        String user = ConfigManager.getString("username", "");
        String pass = ConfigManager.getString("password", "");
        String role = ConfigManager.getString("role", "");

        loginPage.loginWithCredentials(user, pass, role);
    }

    public void fillIncorrectLoginData(){
        String pass = ConfigManager.getString("password", "");
        String role = ConfigManager.getString("role", "");

        loginPage.loginWithCredentials("asddc", pass, role);
    }

    public void click_SingInBtn(){loginPage.submit();}

    public String getLoginError(){
      String attribute = loginPage.getErrorText();
      System.out.println(attribute);
      return attribute;
    }

}
