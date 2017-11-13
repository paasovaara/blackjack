package blackjack.engine;

import blackjack.models.Card;
import blackjack.models.Hand;

public interface GameListener {
    void showMessage(String msg, GameContext context); //Debug, not a proper interface
    void turnChanged(int playerId, GameContext context);
    void dealCard(int playerId, Card card, Hand hand, GameContext context);
    void hitMe(int playerId, Card card, Hand hand, GameContext context);
    void stay(int playerId, Hand hand, GameContext context);
    void busted(int playerId, Hand hand, GameContext context);
}
