package blackjack.models;

public enum Suite {
    Hearts(0), Diamonds(1), Clubs(2), Spades(3);

    private int m_asInt;

    Suite(int asInt) {
        m_asInt = asInt;
    }

    public int asInt() {
        return m_asInt;
    }

    public static Suite fromId(int id) {
        switch (id) {
            case 0: return Hearts;
            case 1: return Diamonds;
            case 2: return Clubs;
            case 3: return Spades;
            default: throw new RuntimeException("Invalid Suite id! " + id);
        }
    }

    public String toString() {
        switch (this) {
            case Hearts: return "Hearts";
            case Diamonds: return "Diamonds";
            case Clubs: return "Clubs";
            case Spades: return "Spades";
        }
        return "";
    }
}
