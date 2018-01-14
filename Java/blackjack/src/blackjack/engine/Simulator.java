package blackjack.engine;

import blackjack.models.*;

import java.util.HashMap;

public class Simulator {
    public static class Statistics {
        public int busted;
        public int iterations;
        public int won;
        public int push;
        public HashMap<Integer, Integer> countPerPips = new HashMap<Integer, Integer>();

        public float bustedRatio() {
            return (float)busted / (float) iterations;
        }

        public float winRatio() {
            return (float)won / (float) iterations;
        }

        public float pushRatio() {
            return (float)push / (float) iterations;
        }

        public float expectedROI() {
            return (float)won / (float)(iterations - push);
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
            buf.append("Win-ratio: ");
            buf.append(winRatio());
            buf.append("\n");
            buf.append("ROI: ");
            buf.append(expectedROI());
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

    public static final int DEFAULT_ITERATION_COUNT = 50000;

    public static Card simulateCard(final Deck deck) {
        Deck copyDeck = new Deck(deck);

        copyDeck.shuffle(false);
        Card c = copyDeck.getNextCard();
        return c;
    }

    public static Statistics simulateHit(final Hand hand, final Hand dealerHand, final Deck deck) {
        return simulateHit(hand, dealerHand, deck, DEFAULT_ITERATION_COUNT);
    }

    public static Statistics simulateHit(final Hand hand, final Hand dealerHand, final Deck deck, final int iterations) {
        return simulateAction(PlayerAction.Hit, hand, dealerHand, deck, iterations);
    }

    public static Statistics simulateStay(final Hand hand, final Hand dealerHand, final Deck deck) {
        return simulateStay(hand, dealerHand, deck, DEFAULT_ITERATION_COUNT);
    }

    public static Statistics simulateStay(final Hand hand, final Hand dealerHand, final Deck deck, final int iterations) {
        return simulateAction(PlayerAction.Stay, hand, dealerHand, deck, iterations);
    }
    //TODO simulate game till the end.
    private static Statistics simulateAction(PlayerAction action, final Hand hand, final Hand dealerHand, final Deck deck, final int iterations) {
        Statistics s = new Statistics();

        s.iterations = iterations;

        for(int n = 0; n < iterations; n++) {
            Hand copyHand = new Hand(hand);

            if (action == PlayerAction.Hit) {
                Card c = simulateCard(deck);
                copyHand.addCard(c);
            }

            if (copyHand.isBusted()) {
                s.busted += 1;
            }
            int key = copyHand.getBestPipCount();
            int count = s.countPerPips.getOrDefault(key, 0);
            s.countPerPips.put(key, count+1);

            if (dealerHand != null) {
                Hand dealerCopy = new Hand(dealerHand);
                while (dealerCopy.getBestPipCount() < 17) {
                    Card c2 = simulateCard(deck);
                    dealerCopy.addCard(c2);
                }
                GameResult.Result result = Hand.compareHands(copyHand, dealerCopy);
                if (result == GameResult.Result.Won) {
                    s.won += 1;
                }
                else if (result == GameResult.Result.Push) {
                    s.push += 1;
                }
            }
        }
        return s;
    }

    public static void main(String[] args) {
        Hand h = new Hand();
        h.addCard(new Card(Suite.Clubs, Rank.Two));
        h.addCard(new Card(Suite.Clubs, Rank.Two));

        Hand dealer = new Hand();
        //Deal only one initial card, to simulate a hidden card which is randomly picked
        dealer.addCard(new Card(Suite.Hearts, Rank.Ace));

        Deck d = new Deck(1);

        Statistics stay = Simulator.simulateStay(h, dealer, d);
        System.out.println("= STAY ================");
        System.out.println(stay.toString());

        Statistics hit = Simulator.simulateHit(h, dealer, d);
        System.out.println("= HIT ================");
        System.out.println(hit.toString());

    }

}
