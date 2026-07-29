package Base;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigManager {

    private static final Properties PROPERTIES = loadProperties();

    private ConfigManager() {
    }

    public static String getString(String key, String defaultValue) {
        if (key == null || key.isBlank()) {
            return defaultValue;
        }

        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        String envKey = key.toUpperCase().replace('.', '_');
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        String propValue = PROPERTIES.getProperty(key);
        return (propValue != null && !propValue.isBlank()) ? propValue : defaultValue;
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = getString(key, Boolean.toString(defaultValue));
        try {
            return Boolean.parseBoolean(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static int getInt(String key, int defaultValue) {
        String value = getString(key, Integer.toString(defaultValue));
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        Path propsPath = Paths.get("src", "test", "resources", "data.properties");
        if (!Files.exists(propsPath)) {
            return props;
        }

        try (FileInputStream fis = new FileInputStream(propsPath.toFile())) {
            props.load(fis);
        } catch (IOException e) {
            System.out.println("Unable to read properties file: " + e.getMessage());
        }
        return props;
    }
}
