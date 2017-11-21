package blackjack;

import blackjack.io.Port;
import blackjack.io.PortReaderThread;
import blackjack.utils.Config;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            String filename = args[0];
            System.out.println("Reading config from " + filename);

            Config config = Config.readFromFile(filename);

            PortReaderThread t = new PortReaderThread();
            t.initialize(config);
            t.start();

            System.out.println("Press Enter to quit...");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }

            t.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
