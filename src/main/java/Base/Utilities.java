package Base;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Utilities extends Base{

    public static String getProperties(String key) {
        Properties prop = new Properties();
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/data.properties");
            prop.load(fis);
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo de propiedades: " + e.getMessage());
        }
        return prop.getProperty(key);
    }
}
