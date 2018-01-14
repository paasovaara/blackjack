package blackjack.ai;

import behave.tools.Log;
import blackjack.engine.Simulator;
import blackjack.models.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.LinkedList;

/**
 * Different approach than with class AITrainingDataCollector.
 * Here we go through all the hands and determine the correct action by simulating (playing the game many times).
 *
 * Problem with this model is that when chances are low on both actions (player has 4, dealer ace) the model might
 * assume to stay, even though the correct action always would be to hit, since can only improve his chances and cannot get busted.
 * => instead of playing till the end, we should check if next position is better by hitting?
 */
public class AITrainingDataGenerator {

    public static void generateAndSave(String filename) throws Exception {
        LinkedList<TrainingSample> samples = generateSamples();
        Log.info("Generated " + samples.size() + " samples.");

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

        for(int n = Rank.MIN_ID; n <= Rank.MAX_ID; n++) {
            //assume one deck
            final Deck deck = new Deck(1);
            //Suite doesn't play any role here so we can just always pretend to use the same
            Card firstCard = deck.pick(Suite.Hearts, Rank.fromId(n));
            for (int m = Rank.MIN_ID; m <= Rank.MAX_ID; m++) {
                final Deck deckCopy = new Deck(deck);

                Card secondCard = deckCopy.pick(Suite.Diamonds, Rank.fromId(m));
                for (int k = Rank.MIN_ID; k <= Rank.MAX_ID; k++) {
                    final Deck secondCopy = new Deck(deckCopy);

                    Card dealerCard = secondCopy.pick(Suite.Spades, Rank.fromId(k));

                    Hand playerHand = createHand(firstCard, secondCard);
                    Hand dealerHand = createHand(dealerCard);

                    Simulator.Statistics statsHit = Simulator.simulateHit(playerHand, dealerHand, secondCopy);
                    Simulator.Statistics statsStay = Simulator.simulateStay(playerHand, dealerHand, secondCopy);
                    int preferredAction;
                    if (statsHit.expectedROI() >= statsStay.expectedROI()) {
                        preferredAction = 1;
                    }
                    else {
                        preferredAction = 0;
                    }
                    TrainingSample sample = new TrainingSample(
                        playerHand.getBestPipCount(), playerHand.getMinPipCount(), dealerHand.getBestPipCount(), preferredAction
                    );
                    Log.info("Correct action for hand " + playerHand.getBestPipCount() + " when dealer has " + dealerHand.getBestPipCount() + " is to " + preferredAction);
                    samples.add(sample);
                }
            }
        }

        return samples;
    }

    private static Hand createHand(Card ... cards) {
        Hand hand = new Hand();
        for(Card c: cards) {
            hand.addCard(c);
        }
        return hand;
    }

}
