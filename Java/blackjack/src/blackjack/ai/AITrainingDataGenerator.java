package blackjack.ai;

import behave.tools.Log;
import blackjack.engine.Simulator;
import blackjack.models.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

/**
 * Different approach than with class AITrainingDataCollector.
 * Here we go through all the hands and determine the correct action by simulating (playing the game many times).
 *
 * Currently we run only one round of simulation. Having more depth in simulations we would probably get better
 * data on close-call situations. TODO
 */
public class AITrainingDataGenerator {
    /**
     * Problem with this model might be that when chances are low on both actions (player has 4, dealer ace) the model might
     * assume to stay, even though the correct action always would be to hit, since can only improve his chances and cannot get busted.
     * => let's always hit if player cannot go over, benefits the player always.
     */
    private static boolean m_alwaysHitOnLowCards = true;

    // Lists for simulating the changes in deck weight:
    private static final ArrayList<Rank> s_smallsToRemove = new ArrayList<>();
    private static final ArrayList<Rank> s_bigsToRemove = new ArrayList<>();
    static {
        //Let's remove two of each, however so that they are not ordered. has impact with smalls but not much with bigs (only aces).
        for (int n = 0; n < 2; n++) {
            for (int rank = 2; rank < 7; rank++) {
                s_smallsToRemove.add(Rank.fromId(rank));
            }
        }
        for (int n = 0; n < 2; n++) {
            s_bigsToRemove.add(Rank.Ace);
            for (int rank = 10; rank < 14; rank++) {
                s_bigsToRemove.add(Rank.fromId(rank));
            }
        }
        Log.info("smalls to remove: " + s_smallsToRemove.size() + " and bigs: " + s_bigsToRemove.size());
    }

    public static void generateAndSave(String filename) throws Exception {
        generateAndSave(filename, null, 0.3f);
    }

    public static void generateAndSave(String filename, String testSetFilename, float testSetRatio) throws Exception {
        LinkedList<TrainingSample> samples = generateSamples();
        int sampleCount = samples.size();
        Log.info("Generated " + sampleCount + " samples.");
        Collections.shuffle(samples);

        if (testSetFilename != null) {
            int testSetSize = Math.round(testSetRatio * (float)sampleCount);
            LinkedList<TrainingSample> testSet = new LinkedList<>();

            ListIterator<TrainingSample> itr  = samples.listIterator(sampleCount - testSetSize);
            while(itr.hasNext()) {
                TrainingSample s = itr.next();
                itr.remove();
                testSet.add(s);
            }

            Log.info("Split into training set of size " + samples.size() + " and test set of size " + testSet.size());
            saveSamplesToFile(testSet, testSetFilename);
        }
        saveSamplesToFile(samples, filename);
    }

    private static void saveSamplesToFile(List<TrainingSample> samples, String filename) throws Exception {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false))) {
            for(TrainingSample sample: samples) {
                writer.append(sample.toString());
                writer.append("\n");
            }
            writer.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static LinkedList<TrainingSample> generateSamples() {
        final LinkedList<TrainingSample> samples = new LinkedList<>();
        //assume one deck. Always use the same count as with the actual game (train separate models if needed)
        final int deckCount = 1;

        for(int n = Rank.MIN_ID; n <= Rank.MAX_ID; n++) {
            final Deck deck = new Deck(deckCount);
            CardCounter counter = new CardCounter();
            //Suite doesn't play any role here so we can just always pretend to use the same
            Card firstCard = pickCard(deck, Suite.Hearts, Rank.fromId(n), counter);
            for (int m = Rank.MIN_ID; m <= Rank.MAX_ID; m++) {
                final Deck deckCopy = new Deck(deck);

                Card secondCard = pickCard(deckCopy, Suite.Diamonds, Rank.fromId(m), counter);
                for (int k = Rank.MIN_ID; k <= Rank.MAX_ID; k++) {
                    final Deck secondCopy = new Deck(deckCopy);
                    Card dealerCard = pickCard(secondCopy, Suite.Spades, Rank.fromId(k), counter);

                    //Put variance to deck weight by removing first small numbers one by one from deck and then doing the same with large ones.
                    List<TrainingSample> samplesWithBetterDeck = generateSamplesWithDeckVariance(secondCopy, firstCard, secondCard, dealerCard, counter, s_smallsToRemove);
                    samples.addAll(samplesWithBetterDeck);
                    List<TrainingSample> samplesWithWorseDeck = generateSamplesWithDeckVariance(secondCopy, firstCard, secondCard, dealerCard, counter, s_bigsToRemove);
                    samples.addAll(samplesWithWorseDeck);
                }
            }
        }

        return samples;
    }

    private static List<TrainingSample> generateSamplesWithDeckVariance(Deck deckToUse, Card firstCard, Card secondCard, Card dealerCard, CardCounter counter, ArrayList<Rank> allCardsToRemove) {
        List<TrainingSample> samples = new LinkedList<>();
        for (int removeCards = 0; removeCards < allCardsToRemove.size(); removeCards++) {
            final CardCounter myCounter = new CardCounter(counter);
            final Deck thirdCopy = new Deck(deckToUse);

            List<Rank> cardsToRemove = allCardsToRemove.subList(0, removeCards);
            for (Rank removeMe: cardsToRemove) {
                //Log.info("Removing card " + removeMe + ". counter before : " + myCounter.getCount() + " and deck size " + thirdCopy.cardsLeft());
                pickCard(thirdCopy, null, removeMe, myCounter);
                //Log.info("Removed card " + removeMe + ". counter after : " + myCounter.getCount() + " and deck size " + thirdCopy.cardsLeft());
            }
            int deckWeight = myCounter.getCount();

            TrainingSample sample = createSample(thirdCopy, firstCard, secondCard, dealerCard, deckWeight);
            samples.add(sample);

        }
        return samples;
    }


    private static TrainingSample createSample(Deck deckToUse, Card firstCard, Card secondCard, Card dealerCard, int deckWeight) {

        Hand dealerHand = createHand(dealerCard);

        int preferredAction;
        Hand playerHand = createHand(firstCard, secondCard);
        if (m_alwaysHitOnLowCards && playerHand.getBestPipCount() <= 11) {
            //Always hit if there's no possibility of going over. Will benefit the player always.
            preferredAction = 1;
        }
        else {
            Simulator.Statistics statsHit = Simulator.simulateHit(playerHand, dealerHand, deckToUse);
            Simulator.Statistics statsStay = Simulator.simulateStay(playerHand, dealerHand, deckToUse);
            if (statsHit.expectedROI() >= statsStay.expectedROI()) {
                preferredAction = 1;
            }
            else {
                preferredAction = 0;
            }
        }
        TrainingSample sample = new TrainingSample(
            playerHand.getBestPipCount(), playerHand.getMinPipCount(), dealerHand.getBestPipCount(), deckWeight, preferredAction
        );
        Log.info("Correct action for hand " + playerHand.getBestPipCount() + " when dealer has " + dealerHand.getBestPipCount() + " is to " + preferredAction);
        return sample;
    }

    private static Card pickCard(Deck deck, Suite suite, Rank rank, CardCounter counter) {
        Card c = deck.pick(suite, rank);
        if (c == null) {
            Log.warning("Warning, picked card is null. Rank " + rank + " from suite " + suite);
        }
        else {
            counter.addToCount(c);
        }
        return c;
    }

    private static Hand createHand(Card ... cards) {
        Hand hand = new Hand();
        for(Card c: cards) {
            hand.addCard(c);
        }
        return hand;
    }

}
