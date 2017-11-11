package blackjack.models;

import java.util.LinkedList;

public class Hand {

    LinkedList<Card> m_cards = new LinkedList<>();

    public void addCard(Card c) {
        m_cards.add(c);
    }

    public boolean isBlackJack() {
        if (m_cards.size() == 2) {
            return getMaxPipCount() == 21;
        }
        else {
            return false;
        }
    }

    public boolean hasHiddenCards() {
        for(Card c: m_cards) {
            if (c.isHidden()) {
                return true;
            }
        }
        return false;
    }

    public boolean isBusted() {
        return getMinPipCount() > 21;
    }

    public int getMinPipCount() {
        return calcSum(true);
    }

    public int getMaxPipCount() {
        return calcSum(false);
    }

    private int calcSum(boolean aceIsOne) {
        int sum = 0;
        for(Card c: m_cards) {
            Rank r = c.m_rank;
            if (r == Rank.Ace) {
                int add = aceIsOne ? 1 : 11;
                sum += add;
            }
            else {
                sum += r.pips();
            }
        }
        return sum;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("Hand => ");
        int min = getMinPipCount();
        int max = getMaxPipCount();
        if (min == max) {
            buf.append(Integer.toString(min) + " [");
        }
        else {
            buf.append(min + "/" + max + " [");
        }
        for(Card c: m_cards) {
            buf.append(c.toString()+ ", ");
        }
        buf.append("]");
        return buf.toString();
    }
}
