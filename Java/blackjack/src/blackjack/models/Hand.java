package blackjack.models;

import java.util.*;

public class Hand {

    LinkedList<Card> m_cards = new LinkedList<>();

    public Hand() {}

    public Hand(Hand copy) {
        m_cards.addAll(copy.m_cards);
    }

    public void addCard(Card c) {
        m_cards.add(c);
    }

    public int cardCount() {
        return m_cards.size();
    }

    public static GameResult.Result compareHands(Hand playerHand, Hand dealerHand) {
        int dealerTicks = dealerHand.getBestPipCount();
        int playerTicks = playerHand.getBestPipCount();
        GameResult.Result result = GameResult.Result.Lost;
        if (playerHand.isBusted()) {
            result = GameResult.Result.Busted;
        }
        else if (dealerHand.isBusted() && !playerHand.isBusted()) {
            result = GameResult.Result.Won;
        }
        else if (playerHand.isBlackJack()) {
            result = dealerHand.isBlackJack() ? GameResult.Result.Push : GameResult.Result.Won;
        }
        else if (playerTicks > dealerTicks) {
            result = GameResult.Result.Won;
        }
        else if (playerTicks == dealerTicks) {
            result = GameResult.Result.Push;
        }
        else {
            result = GameResult.Result.Lost;
        }
        return result;
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

    private int getMinPipCount() {
        return calcSum(true);
    }

    private int getMaxPipCount() {
        return calcSum(false);
    }

    private LinkedList<Integer> calcAllSumsRecursively(LinkedList<Integer> sums, List<Card> remainingCards) {
        if (remainingCards.isEmpty()) {
            return sums;
        }
        else {
            Card c = remainingCards.remove(0);
            if (c.isHidden()) {
                return calcAllSumsRecursively(sums, remainingCards);
            }
            else {
                LinkedList<Integer> newSums = new LinkedList<>();
                for (int oldSum: sums) {
                    Rank r = c.m_rank;
                    if (r == Rank.Ace) {
                        newSums.add(oldSum + 1);
                        newSums.add(oldSum + 11);
                    }
                    else {
                        newSums.add(oldSum + r.pips());
                    }
                }
                return calcAllSumsRecursively(newSums, remainingCards);
            }
        }
    }

    public int getBestPipCount() {
        LinkedList<Integer> seed = new LinkedList<>();
        seed.add(0);
        LinkedList<Card> temp = new LinkedList<>(m_cards);
        LinkedList<Integer> allSumPermuations = calcAllSumsRecursively(seed, temp);

        //Try to pick the first under 22
        Collections.sort(allSumPermuations, Collections.reverseOrder());
        int best = 0;
        for (Integer perm: allSumPermuations) {
            best = perm;
            if (perm <= 21) {
                break;
            }
        }
        return best;
    }

    private int calcSum(boolean aceIsOne) {
        return calcSum(m_cards, aceIsOne);
    }

    private static int calcSum(List<Card> cards, boolean aceIsOne) {
        int sum = 0;
        for(Card c: cards) {
            //Hidden card basically doesn't exist until it's turned.
            if (c.isHidden())
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
