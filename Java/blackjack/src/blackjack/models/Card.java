package blackjack.models;

public class Card {
    Suite m_suite;
    int m_value; // TODO create enum

    public Card(Suite suite, int value) {
        m_suite = suite;
        m_value = value;
    }

    public String toString() {
        return m_value + " of " + m_suite.toString();
    }

}
