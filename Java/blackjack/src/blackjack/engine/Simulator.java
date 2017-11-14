package blackjack.engine;

import blackjack.models.*;

import java.util.HashMap;

public class Simulator {
    public static class Statistics {
        public int busted;
        public int iterations;
        public HashMap<Integer, Integer> countPerPips = new HashMap<Integer, Integer>();

        public float bustedRatio() {
            return (float)busted / (float) iterations;
        }

        public float between(int min, int max) {
            int sum = 0;
            for(int n = min; n <= max; n++) {
                sum += countPerPips.getOrDefault(n, 0);
            }
            return (float)sum/(float)iterations;
        }

        @Override
        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append("Simulations: ");
            buf.append(iterations);
            buf.append("\n");
            buf.append("Busted-ratio: ");
            buf.append(bustedRatio());
            buf.append("\n");
            buf.append("21: ");
            buf.append(between(21, 21));
            buf.append("\n");
            buf.append("17-21: ");
            buf.append(between(17, 21));
            buf.append("\n");

            return buf.toString();
        }
    }

    public static Statistics simulateHit(final Hand hand, final Deck deck) {
        Statistics s = new Statistics();
        final int iterations = 100000;
        s.iterations = iterations;

        for(int n = 0; n < iterations; n++) {
            Deck copyDeck = new Deck(deck);

            copyDeck.shuffle(false);
            Card c = copyDeck.getNextCard();

            Hand copyHand = new Hand(hand);
            copyHand.addCard(c);

            if (copyHand.isBusted()) {
                s.busted += 1;
            }
            int key = copyHand.getBestPipCount();
            int count = s.countPerPips.getOrDefault(key, 0);
            s.countPerPips.put(key, count+1);
        }
        return s;
    }

    //TODO add function where there's two hands, other being the dealers.

    public static void main(String[] args) {
        Hand h = new Hand();
        h.addCard(new Card(Suite.Clubs, Rank.Six));
        h.addCard(new Card(Suite.Clubs, Rank.Five));
        Deck d = new Deck(1);
        Statistics s = Simulator.simulateHit(h, d);
        System.out.println(s.toString());
    }

}
