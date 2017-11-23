package blackjack;

import blackjack.engine.BlackJack;

import java.util.HashSet;

public class Main {

    public static void main(String[] args) {
        HashSet<String> set = new HashSet<>();
        for(int n = 0; n < args.length; n++) {
            set.add(args[n]);
        }
	    boolean prod = set.contains("prod");
        boolean sensors = set.contains("sensors");
        boolean robot = set.contains("robot");
        BlackJack.playGame(!prod, sensors, robot);
    }
}
