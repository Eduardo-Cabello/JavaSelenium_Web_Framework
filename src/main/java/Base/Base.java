package Base;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;

public class Base {

    public static WebDriver driver;
    public static WebDriverWait wait;
    public static int implicitWait = 6;

    public static WebDriver startDriver(String nav) {
        try {
            if (nav.equalsIgnoreCase("chrome")){
                driver = new ChromeDriver();
            } else if (nav.equalsIgnoreCase("edge")){
                driver = new EdgeDriver();
            } else {
                throw new IllegalArgumentException("Incorrect browser name, options: 'chrome', 'edge'");
            }
            driver.manage().window().maximize();
        } catch (Exception e) {
            System.err.println("Error al iniciar el driver: " + e.getMessage());
        }
        return driver;
    }

    public static void navigate(){
        driver.get(Utilities.getProperties("url"));
    }

    public static void waitElementVisible(WebElement element){
        try {
            System.out.println("Waiting to element visible.."+ element.toString());
            wait.until(ExpectedConditions.visibilityOf(element));
            System.out.println("Element visible");
        } catch (Exception e){
            System.out.println("Element no visible"+element.toString());
        }
    }

    public static void click(WebElement element) {
        try {
            waitElementVisible(element);
            element.click();
        } catch (Exception e) {
            String[] options = {"Continue","Repeat","Finish"};
            int opt = JOptionPane.showOptionDialog(
                    null,
                    "Element no clickable: " + element.toString(),
                    "Error",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    options,
                    0
            );
            if (opt == 0) {
                System.out.println("Continue Test");
            }else if (opt == 1){
                element.click();
            }else {
                System.out.println("Test stopped at element: " + element.toString());
                if (driver != null) {
                    driver.quit();
                }
                System.exit(0);
            }
        }
    }

    public static void type(WebElement element, String value) {
        try {
            waitElementVisible(element);
            element.sendKeys(value);
        } catch (Exception e) {
            String[] options = {"Continue","Repeat","Finish"};
            int opt = JOptionPane.showOptionDialog(
                    null,
                    "Element no clickable: " + element.toString(),
                    "Error",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    options,
                    0
            );
            if (opt == 0) {
                System.out.println("Continue Test");
            }else if (opt == 1){
                element.sendKeys(value);
            }else {
                System.out.println("Test stopped at element: " + element.toString());
                if (driver != null) {
                    driver.quit();
                }
                System.exit(0);
            }
        }
    }

    public static void select(WebElement element, String value) {
        Select webList = new Select(element);
        try {
            waitElementVisible(element);
            webList.selectByVisibleText(value);
        } catch (Exception e) {
            String[] options = {"Continue","Repeat","Finish"};
            int opt = JOptionPane.showOptionDialog(
                    null,
                    "Element no selectable: " + element.toString(),
                    "Error",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    options,
                    0
            );
            if (opt == 0) {
                System.out.println("Continue Test");
            }else if (opt == 1){
                webList.selectByVisibleText(value);
            }else {
                System.out.println("Test stopped at element: " + element.toString());
                if (driver != null) {
                    driver.quit();
                }
                System.exit(0);
            }
        }
    }

    public static void clearType(WebElement element, String value){
        try {
            waitElementVisible(element);
            element.clear();
            wait.wait(500);
            element.sendKeys(value);
        } catch (Exception e) {
            String[] options = {"Continue","Repeat","Finish"};
            int opt = JOptionPane.showOptionDialog(
                    null,
                    "Element no findable: " + element.toString(),
                    "Error",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    options,
                    0
            );
            if (opt == 0) {
                System.out.println("Continue Test");
            }else if (opt == 1){
                element.clear();
                element.sendKeys(value);
            }else {
                System.out.println("Test stopped at element: " + element.toString());
                if (driver != null) {
                    driver.quit();
                }
                System.exit(0);
            }

        }
    }

    public static void clear(WebElement element){
        try {
            waitElementVisible(element);
            element.clear();
        }catch (Exception e){
            String[] options = {"Continue","Repeat","Finish"};
            int opt = JOptionPane.showOptionDialog(
                    null,
                    "Element no findable: " + element.toString(),
                    "Error",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    options,
                    0
            );
            if (opt == 0) {
                System.out.println("Continue Test");
            }else if (opt == 1){
                element.clear();
            }else {
                System.out.println("Test stopped at element: " + element.toString());
                if (driver != null) {
                    driver.quit();
                }
                System.exit(0);
            }
        }
    }

    public static String getText(WebElement element){
        waitElementVisible(element);
        return element.getText();
    }

    public static String getAttribute(WebElement element, String attribute){
        waitElementVisible(element);
        return element.getAttribute(attribute);
    }

    public static void switchToFrame(By locator) {
        try {
            driver.switchTo().defaultContent();
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
        } catch (TimeoutException e) {
            throw new TimeoutException("Frame identify by " + locator + " It wasn't available in time of period", e);
        } catch (WebDriverException e) {
            throw new WebDriverException("Failed to find frame " + locator, e);
        }
    }

    public static void switchOriginalFrame(){
        try {
            driver.switchTo().defaultContent();
        } catch (WebDriverException e) {
            System.err.println("Error to find principal frame: " + e.getMessage());
            throw e;
        }
    }

    public static void acceptAlertIfPresent(){
        try {
            wait.until(ExpectedConditions.alertIsPresent()).accept();
        } catch (TimeoutException e) {
            System.out.println("No alert detected in a time of period");
        }
    }
}