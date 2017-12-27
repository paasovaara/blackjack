package blackjack.io.console;

import blackjack.engine.GameContext;
import blackjack.engine.GameListener;
import blackjack.engine.Simulator;
import blackjack.models.*;

import java.util.List;

public class ConsoleOutput implements GameListener {
    @Override
    public void showMessage(String msg, GameContext context) {
        System.out.println(msg);
    }

    @Override
    public void gameStarted(List<Bet> playerBets, GameContext context) {
        StringBuffer buf = new StringBuffer();
        buf.append("Game started with following bets:\n");
        for(Bet bet: playerBets) {
            buf.append("Player " + bet.playerId + ": " + bet.betAmount + "$\n");
        }
        System.out.println(buf.toString());
    }


    @Override
    public void waitingForBets() {
        System.out.println("Please join in for a quick game of blackjack");
    }

    @Override
    public void giveAdvice(int playerId, Simulator.Statistics hitOdds, Simulator.Statistics stayOdds, Hand hand, GameContext context) {
        //We could randomize all kinds of punchlines here..
        System.out.println("Changes of winning if hit: " + hitOdds.expectedROI() + " and by staying: " + stayOdds.expectedROI());
    }

    @Override
    public void tellInstructions() {
        System.out.println("You will win if your hand is closer to 21 than the dealers or the dealer get's busted and you don't. Good luck.");
    }

    @Override
    public void shuffle(Deck deck) {
        System.out.println("~ Shuffling deck ~");
    }

    @Override
    public void turnChanged(int playerId, GameContext context) {
        if (playerId >= 0) {
            System.out.println("Player " + playerId + " turn");
        }
        else {
            System.out.println("Dealers turn");
        }
    }

    @Override
    public void revealDealerCard(Card card, Hand hand, GameContext context) {
        System.out.println("\tDealer hidden card " + card);
        System.out.println("Current dealer hand " + hand);
    }

    @Override
    public void blackjack(int playerId, GameContext context) {
        if (playerId == -1)  {
            System.out.println("Dealer has blackjack!");
        }
        else {
            System.out.println("Player "+ playerId + " has blackjack!");
        }
    }

    @Override
    public void dealCard(int playerId, Card card, Hand hand, GameContext context) {
        if (playerId >= 0) {
            System.out.println("\tDealt player " + playerId + " card " + card);
        }
        else {
            System.out.println("\tDealt dealer a card " + card);
        }
    }

    @Override
    public void hitMe(int playerId, Card card, Hand hand, GameContext context) {
        if (playerId >= 0) {
            System.out.println("\tDealt player " + playerId + " card " + card);
        }
        else {
            System.out.println("\tDealt dealer a card " + card);
        }
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

    @Override
    public void gameEnded(GameResult results, GameContext context) {
        System.out.println("GAME OVER");
        System.out.println(results);
    }

}
