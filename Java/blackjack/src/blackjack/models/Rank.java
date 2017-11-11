package blackjack.models;

public enum Rank {
    Ace(1), King(13), Queen(12), Jack(11), Ten(10), Nine(9), Eight(8), Seven(7), Six(6), Five(5), Four(4), Three(2), Two(2);

    public static int MIN_ID = 1;
    public static int MAX_ID = 13;

    private int m_id;

    Rank(int id) {
        m_id = id;
    }

    public int pips() {
        if (this== Ace) {
            return 11; //Ace is a problem, how to do it? Deal at game logic?
        }
        else if (m_id >= 10) {
            return 10;
        }
        else {
            return m_id;
        }
    }

    //For serializing when communicating over network
    public int getId() {
        return m_id;
    }

    //For deserialization
    public static Rank fromId(int id) {
        switch (id) {
            case 1: return Ace;
            case 2: return Two;
            case 3: return Three;
            case 4: return Four;
            case 5: return Five;
            case 6: return Six;
            case 7: return Seven;
            case 8: return Eight;
            case 9: return Nine;
            case 10: return Ten;
            case 11: return Jack;
            case 12: return Queen;
            case 13: return King;
            default: throw new RuntimeException("Invalid Rank id! " + id);
        }
    }

    public String toString() {
        return Integer.toString(m_id);
    }
}
