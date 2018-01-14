package blackjack.models;

import behave.tools.Log;

import java.util.*;

/**
 * Class to represent card deck. This class is not thread safe.
 */
public class Deck {

    private LinkedList<Card> m_cardsRemaining;
    private LinkedList<Card> m_cardsDealt;

    public Deck(int howMany) {
        createDeck(howMany);
    }

    public Deck() {
        this(1);
    }

    public Deck(Deck copy) {
        m_cardsDealt = new LinkedList<>();
        //Should we deep-copy the cards also?
        m_cardsDealt.addAll(copy.m_cardsDealt);
        m_cardsRemaining = new LinkedList<>();
        m_cardsRemaining.addAll(copy.m_cardsRemaining);
    }

    private void createDeck(int howMany) {
        m_cardsRemaining = new LinkedList<>();
        m_cardsDealt = new LinkedList<>();

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
        shuffle(true);
    }

    public void shuffle(boolean mergeDecks) {
        if (mergeDecks) {
            m_cardsRemaining.addAll(m_cardsDealt);
            m_cardsDealt.clear();
        }
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

    public Card pick(Suite suite, Rank rank) {
        ListIterator<Card> itr = m_cardsRemaining.listIterator();
        Card card = null;
        while(itr.hasNext()) {
            Card next = itr.next();
            if (next.getRank() == rank && next.getSuite() == suite) {
                card = next;
                itr.remove();
                m_cardsDealt.push(card);
                break;
            }
        }
        return card;
    }

    public String toString() {

        return "Deck with " + cardsLeft() + " cards left (out of " + deckSize() + ")";
    }
}
