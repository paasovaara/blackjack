package blackjack.models;

public class Card {
    Suite m_suite;
    Rank m_rank; // TODO create enum
    private boolean m_hidden = false;


    public Card(Suite suite, Rank rank) {
        this(suite, rank, false);
    }

    public Card(Suite suite, Rank rank, boolean hidden) {
        m_suite = suite;
        m_rank = rank;
        m_hidden = hidden;
    }

    public boolean isHidden() { return m_hidden; }

    public String toString() {
        return m_rank + " of " + m_suite.toString();
    }

}
