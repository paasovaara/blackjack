package blackjack.models;

import behave.tools.Log;

import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Class to represent card deck. This class is not thread safe.
 */
public class Deck {

    private Stack<Card> m_cardsRemaining;
    private Stack<Card> m_cardsDealt;

    public Deck(int howMany) {
        createDeck(howMany);
    }

    public Deck() {
        this(1);
    }

    private void createDeck(int howMany) {
        m_cardsRemaining = new Stack<>();
        m_cardsDealt = new Stack<>();

        for(int m = 0; m < howMany; m++) {
            for(int n = Rank.MIN_ID; n <= Rank.MAX_ID; n++) {
                Rank rank = Rank.fromId(n);
                m_cardsRemaining.push(new Card(Suite.Hearts, rank));
                m_cardsRemaining.push(new Card(Suite.Diamonds, rank));
                m_cardsRemaining.push(new Card(Suite.Clubs, rank));
                m_cardsRemaining.push(new Card(Suite.Spades, rank));
            }
        }
        Log.info("Deck size: " + m_cardsRemaining.size());
    }

    public void shuffle() {
        m_cardsRemaining.addAll(m_cardsDealt);
        m_cardsDealt.clear();
        Collections.shuffle(m_cardsRemaining);
    }

    public int cardsLeft() {
        return m_cardsRemaining.size();
    }

    public int deckSize() {
        return m_cardsRemaining.size() + m_cardsDealt.size();
    }

    public float deckRemaining() {
        return (float)cardsLeft() / (float)(deckSize());
    }

    public Card getNextCard() {
        Card c = null;
        try {
            c = m_cardsRemaining.pop();
            if (c != null) {
                m_cardsDealt.push(c);
            }
        }
        catch (EmptyStackException empty) {
            Log.info("Card deck empty");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public String toString() {

        return "Deck with " + cardsLeft() + " cards left (out of " + deckSize() + ")";
    }
}
