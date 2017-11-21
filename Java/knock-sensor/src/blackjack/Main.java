package blackjack;

import blackjack.io.Port;
import blackjack.io.PortReaderThread;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            String comport = args[0];
            System.out.println(comport);

            /*
            Port port = new Port();
            port.open(comport);*/
            PortReaderThread t = new PortReaderThread();
            t.initialize(comport);
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
