import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = new Properties();

    static {
        try {
            InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties");
            if (input == null) {
                throw new RuntimeException("Unable to find config.properties file");
            }
            properties.load(input);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static int getIntValue(String name) {
        String value = properties.getProperty(name);
        if (value == null) {
            throw new IllegalArgumentException("Property not found");
        }
        try {
            return Integer.parseInt(value);
        } catch (Exception exception) {
            throw new IllegalArgumentException("The property is not an integer");
        }
    }
}