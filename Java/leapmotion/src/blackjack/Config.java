package blackjack;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    //Too lazy to code getters for this one
    public String host;
    public int port;
    public String payload;

    public static Config readFromFile(String filename) throws IOException {
        Config c = null;
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream(filename);
            // load a properties file
            prop.load(input);

            c = new Config();
            c.host = prop.getProperty("targethost");
            String portStr = prop.getProperty("port");
            c.port = Integer.parseInt(portStr.trim());
            c.payload = prop.getProperty("payload");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return c;
    }

}
