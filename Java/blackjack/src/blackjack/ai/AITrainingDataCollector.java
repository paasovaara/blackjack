package blackjack.ai;

import behave.tools.Log;
import blackjack.engine.GameContext;
import blackjack.engine.GameListener;
import blackjack.engine.InputManager;
import blackjack.engine.Simulator;
import blackjack.models.*;

import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Current implementation has two problems:
 * 1) we never stay so we cannot know if that's a good option. Now this model urges to hit most of the time
 * 2) if minPipCount is low then this model will always hit, no matter if it already had 21.
 */
public class AITrainingDataCollector implements InputManager, GameListener {

    final LinkedList<TrainingSample> m_samples = new LinkedList<>();
    TrainingSample m_lastSample = null;

    final static int AI_PLAYER_ID = 0;

    FileWriter m_writer;

    public void initialize(String filename) throws Exception {
        m_writer = new FileWriter(filename, false);
    }


    @Override
    public List<Bet> getBets() {
        List<Bet> list = new LinkedList<>();
        list.add(new Bet(AI_PLAYER_ID, 1));
        return list;
    }

    @Override
    public PlayerAction getInput(int playerId, GameContext gameState, Set<PlayerAction> options, boolean longTimeout) {
        if (playerId != AI_PLAYER_ID) {
            Log.error("getInput for player Id " + playerId);
            throw new RuntimeException("AI Training received input request with invalid player id");
        }
        if (m_lastSample != null) {
            //Last sample has been a correctAction since we got one more round
            saveSample(m_lastSample, true);
        }
        m_lastSample = new TrainingSample();

        String key = GameContext.playerHandKey(playerId);
        Hand hand = (Hand)gameState.getVariable(key);
        m_lastSample.bestPips = hand.getBestPipCount();
        m_lastSample.minPips = hand.getMinPipCount();

        Hand dealerHand =(Hand)gameState.getVariable(GameContext.KEY_DEALER_HAND);
        m_lastSample.dealerPips = dealerHand.getBestPipCount();

        // Always hit so we know was it worth it or not
        return PlayerAction.Hit;
    }

    private void saveSample(TrainingSample sample, boolean hitSuccess) {
        sample.correctAction = hitSuccess ? 1 : 0;
        m_samples.add(sample);
        try {
            m_writer.append(sample.toString());
            m_writer.append("\n");
            m_writer.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void gameStarted(List<Bet> playerBets, GameContext context) {
        m_lastSample = null;
    }

    @Override
    public void gameEnded(GameResult results, GameContext context) {
        String key = GameContext.playerHandKey(AI_PLAYER_ID);
        GameResult.Result r = results.getResults().get(key);
        if (r == GameResult.Result.Busted) {
            //Was bad call to hit
            saveSample(m_lastSample, false);
        }
        else if (r == GameResult.Result.Won || r == GameResult.Result.Push) {
            //Push was caused also by a correct move, since we stop at push only if 21 reached.
            Hand hand = (Hand)context.getVariable(key);
            // let's ignore blackjacks, since no action is required then
            if (!hand.isBlackJack() && m_lastSample != null) {
                saveSample(m_lastSample, true);
            }
        }
    }

    /** Not used **/

    @Override
    public void showMessage(String msg, GameContext context) {}
    @Override
    public void waitingForBets() {}
    @Override
    public void giveAdvice(int playerId, Simulator.Statistics hitOdds, Simulator.Statistics stayOdds, Hand hand, GameContext context) {}
    @Override
    public void turnChanged(int playerId, GameContext context) {}
    @Override
    public void tellInstructions() {}
    @Override
    public void shuffle(Deck deck) {}
    @Override
    public void revealDealerCard(Card card, Hand hand, GameContext context) {}
    @Override
    public void blackjack(int playerId, GameContext context) {}
    @Override
    public void dealCard(int playerId, Card card, Hand hand, GameContext context) {}
    @Override
    public void hitMe(int playerId, Card card, Hand hand, GameContext context) {}
    @Override
    public void stay(int playerId, Hand hand, GameContext context) {}
    @Override
    public void busted(int playerId, Hand hand, GameContext context) {}

}
