package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtil {

    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();


        try (InputStream fis = classLoader.getResourceAsStream("application.properties")) {
            PROPERTIES.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }
}
