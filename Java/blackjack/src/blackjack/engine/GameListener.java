package blackjack.engine;

import blackjack.models.*;

import java.util.List;

public interface GameListener {
    void showMessage(String msg, GameContext context); //Debug, not a proper interface

    void gameStarted(List<Bet> playerBets, GameContext context);
    void giveAdvice(int playerId, Simulator.Statistics hitOdds, Simulator.Statistics stayOdds, Hand hand, GameContext context);
    void shuffle(Deck deck);
    void turnChanged(int playerId, GameContext context);
    void revealDealerCard(Card card, Hand hand, GameContext context);
    void dealCard(int playerId, Card card, Hand hand, GameContext context);
    void hitMe(int playerId, Card card, Hand hand, GameContext context);
    void stay(int playerId, Hand hand, GameContext context);
    void busted(int playerId, Hand hand, GameContext context);
    void gameEnded(GameResult results, GameContext context);
}
