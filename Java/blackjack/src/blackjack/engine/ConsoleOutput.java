package blackjack.engine;

import blackjack.models.Card;
import blackjack.models.Deck;
import blackjack.models.Hand;

public class ConsoleOutput implements GameListener {
    @Override
    public void showMessage(String msg, GameContext context) {
        System.out.println(msg);
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

}
