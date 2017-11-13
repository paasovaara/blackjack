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

    public Card revealHiddenCard() {
        for(Card c: m_cards) {
            if (c.isHidden()) {
                c.setHidden(false);
                return c;
            }
        }
        return null;
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

    public int getBestPipCount() {
        //TODO this fails if there's multiple aces! we should calc all permutations.
        int min = getMinPipCount();
        int max = getMaxPipCount();
        if (max > 21) {
            return min;
        }
        else {
            return max;
        }
    }

    private int calcSum(boolean aceIsOne) {
        return calcSum(aceIsOne, true);
    }

    private int calcSum(boolean aceIsOne, boolean includeHidden) {
        int sum = 0;
        for(Card c: m_cards) {
            if (!includeHidden && c.isHidden())
                continue;

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
        //buf.append("Hand => ");
        int min = calcSum(true, false);
        int max = calcSum(false, false);
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
