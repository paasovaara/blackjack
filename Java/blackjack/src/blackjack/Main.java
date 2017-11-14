package blackjack;

import blackjack.engine.BlackJack;

public class Main {

    public static void main(String[] args) {
	// write your code here
        boolean prod = (args.length > 0 && args[0].equals("prod"));
        BlackJack.playGame(!prod);
    }
}
