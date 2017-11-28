package blackjack.utils;

import java.io.*;
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

    public static void writePropertiesFile(String filename, Properties props) throws IOException {
        Properties prop = new Properties();
        OutputStream output = null;
        try {
            output = new FileOutputStream(filename);
            // load a properties file
            props.store(output, null);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
