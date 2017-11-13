package blackjack.engine;

import blackjack.models.Card;
import blackjack.models.Hand;

public class ConsoleOutput implements GameListener {
    @Override
    public void showMessage(String msg, GameContext context) {
        System.out.println(msg);
    }

    @Override
    public void hitMe(int playerId, Card card, Hand hand, GameContext context) {
        System.out.println("Dealt player " + playerId + " card " + card);
        System.out.println("Current hand " + hand);
    }

    @Override
    public void stay(int playerId, Hand hand, GameContext context) {
        System.out.println("Staying, Current hand " + hand);

    }

    @Override
    public void busted(int playerId, Hand hand, GameContext context) {
        System.out.println("BUSTED, Current hand " + hand);
    }

}
