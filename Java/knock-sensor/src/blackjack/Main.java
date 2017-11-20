package blackjack;

import blackjack.io.Port;

public class Main {

    public static void main(String[] args) {
        try {
            Port port = new Port();
            port.open("COM1");

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
