package blackjack.ai;

import blackjack.engine.GameContext;
import blackjack.engine.GameListener;
import blackjack.engine.Simulator;
import blackjack.models.*;

import java.util.List;

public class CardCounter implements GameListener {
    private int m_count = 0;

    //This is just for debug assertion
    private int m_cardsDealt = 0;

    // It's beneficial for the player to get rid of low cards and leave high cards to the deck
    private static final int HIGH_CARD_VALUE = -1;
    private static final int LOW_CARD_VALUE = 1;
    private static final int MIDDLE_CARD_VALUE = 0;

    private static int getValue(Card c) {
        int pips = c.getRank().pips();
        if (pips >= 10) {
            return HIGH_CARD_VALUE;
        }
        else if (pips < 7) {
            return LOW_CARD_VALUE;
        }
        else {
            return MIDDLE_CARD_VALUE;
        }
    }

    public int getCount() {
        return m_count;
    }

    private void addToCount(Card c) {
        m_count += getValue(c);
        m_cardsDealt++;
    }

    @Override
    public void shuffle(Deck deck) {
        m_count = 0;
        m_cardsDealt = 0;
    }

    @Override
    public void revealDealerCard(Card card, Hand hand, GameContext context) {
        addToCount(card);
    }

    @Override
    public void dealCard(int playerId, Card card, Hand hand, GameContext context) {
        if (!card.isHidden()) {
            addToCount(card);
        }
    }

    @Override
    public void hitMe(int playerId, Card card, Hand hand, GameContext context) {
        addToCount(card);
    }

    @Override
    public void gameEnded(GameResult results, GameContext context) {
        //Some debug assertions here
        Deck deck = (Deck)context.getVariable(GameContext.KEY_DECK);
        int dealt = deck.deckSize() - deck.cardsLeft();
        if (dealt != m_cardsDealt) {
            throw new RuntimeException("Card counter has skipped some cards, dealt " + dealt + " when counter was " + m_cardsDealt);
        }
    }

    @Override
    public void showMessage(String msg, GameContext context) {}

    @Override
    public void waitingForBets() {}

    @Override
    public void gameStarted(List<Bet> playerBets, GameContext context) {}

    @Override
    public void giveAdvice(int playerId, Simulator.Statistics hitOdds, Simulator.Statistics stayOdds, Hand hand, GameContext context) {}

    @Override
    public void tellInstructions() {}

    @Override
    public void turnChanged(int playerId, GameContext context) {}

    @Override
    public void blackjack(int playerId, GameContext context) {}

    @Override
    public void stay(int playerId, Hand hand, GameContext context) {}

    @Override
    public void busted(int playerId, Hand hand, GameContext context) {}

}
