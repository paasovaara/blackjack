package blackjack.models;

public class Card {
    Suite m_suite;
    Rank m_rank;
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
    public void setHidden(boolean hidden) { m_hidden = hidden; }

    public Suite getSuite() { return m_suite; }
    public Rank getRank() { return m_rank; }

    public String toString() {
        if (m_hidden) {
            return "Card hidden";
        }
        else {
            return m_rank + " of " + m_suite.toString();
        }
    }

}
