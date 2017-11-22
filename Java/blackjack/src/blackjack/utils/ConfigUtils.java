package blackjack.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtils {
    public static Properties readPropertiesFile(String filename) throws IOException {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(filename);
            // load a properties file
            prop.load(input);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }
}
