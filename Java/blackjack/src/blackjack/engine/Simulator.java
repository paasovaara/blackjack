package blackjack.engine;

import behave.tools.Log;
import blackjack.models.*;
import jdk.nashorn.internal.codegen.CompilerConstants;

import java.util.*;
import java.util.concurrent.*;

public class Simulator {
    public static class Statistics {
        public int busted;
        public int iterations;
        public int won;
        public int push;
        public HashMap<Integer, Integer> countPerPips = new HashMap<Integer, Integer>();

        public void add(Statistics other) {
            busted += other.busted;
            iterations += other.iterations;
            won += other.won;
            push += other.push;
            //TODO add countsPerPips here also.
        }

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
    private static Statistics simulateActionMultiThreaded(PlayerAction action, final Hand hand, final Hand dealerHand, final Deck deck, final int iterations) {
        Statistics cumulative = new Statistics();

        Runtime runtime = Runtime.getRuntime();
        int cpuCount = runtime.availableProcessors();
        int threadCount = 2;//cpuCount - 1;
        threadCount = threadCount > 0 ? threadCount : 1;

        int iterationsPerThread = iterations / threadCount;
        List<Callable<Statistics>> callables = new LinkedList<>();
        for (int n = 0; n < threadCount; n++) {
            callables.add(() -> simulateAction(action, hand, dealerHand, new Deck(deck), iterationsPerThread));
        }

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        try {
            List<Future<Statistics>> futures = new LinkedList<>();
            for (Callable<Statistics> c : callables) {
                futures.add(executor.submit(c));
            }

            /*List<Future<Statistics>> futures = executor.invokeAll(callables);*/
            for (Future<Statistics> f: futures) {
                cumulative.add(f.get());
            }
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        catch (ExecutionException ex2) {
            ex2.printStackTrace();
        }
        executor.shutdown();

        return cumulative;
    }

    //TODO simulate game till the end.
    private static Statistics simulateAction(PlayerAction action, final Hand hand, final Hand dealerHand, final Deck deck, final int iterations) {
        long startTime = System.currentTimeMillis();

        Statistics s = new Statistics();

        s.iterations = iterations;

        for(int n = 0; n < iterations; n++) {
            Hand copyHand = new Hand(hand);
            Deck copyDeck = new Deck(deck);
            copyDeck.shuffle(false);

            if (action == PlayerAction.Hit) {
                Card c = copyDeck.getNextCard();
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
                    Card c2 = copyDeck.getNextCard();
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

        Log.info("Simulation elapsed in " + (System.currentTimeMillis() - startTime) + " ms for iterations: " + iterations);

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
        /*
        Statistics stay = Simulator.simulateStay(h, dealer, d);
        System.out.println("= STAY ================");
        System.out.println(stay.toString());

        Statistics hit = Simulator.simulateHit(h, dealer, d);
        System.out.println("= HIT ================");
        System.out.println(hit.toString());
*/
        System.out.println("= Starting ================");
        int iterations = 500000;
        Statistics singleThread = Simulator.simulateAction(PlayerAction.Hit, h, dealer, d, iterations);
        System.out.println("= HIT Single thread ================");
        System.out.println(singleThread.toString());

        Statistics multiThread = Simulator.simulateActionMultiThreaded(PlayerAction.Hit, h, dealer, d, iterations);
        System.out.println("= HIT Single multiThread ================");
        System.out.println(multiThread.toString());

    }

}
