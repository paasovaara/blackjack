package blackjack.models;

import java.util.LinkedList;

public class Hand {

    LinkedList<Card> m_cards = new LinkedList<>();

    public void addCard(Card c) {
        m_cards.add(c);
    }

    public boolean isBlackJack() {
        if (m_cards.size() == 2) {
            Rank first = m_cards.get(0).m_rank;
            Rank second = m_cards.get(1).m_rank;
            if (first == Rank.Ace && second.pips() == 10) {
                return true;
            }
            else if (second == Rank.Ace && first.pips() == 10) {
                return true;
            }
        }
        return false;
    }

}
