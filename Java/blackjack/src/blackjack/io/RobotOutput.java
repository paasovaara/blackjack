package blackjack.io;

import blackjack.engine.GameContext;
import blackjack.engine.GameListener;
import blackjack.engine.Simulator;
import blackjack.models.*;
import blackjack.utils.Config;
import blackjack.utils.EventSender;

import java.util.List;
import java.util.Map;

public class RobotOutput implements GameListener {
    EventSender m_sender = new EventSender();

    /*
    We simulate the robot as a separate thread for each movement because that's what it essentially is for us.
    We have no control weather the robot has finished it's movements in time or not, we just have to assume it does something in fixed time.
     */
    class RobotSimulation extends Thread {
        private String m_output;
        private long m_length;

        public RobotSimulation(String output, long movementLength) {
            m_output = output;
            m_length = movementLength;
        }

        public void run() {
            System.out.println("<<ROBOT START >>" + m_output);
            try {
                sleep(m_length);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("<<ROBOT DONE >>" + m_output);
        }
    }

    public void init(Config config) {
        try {
            m_sender.initialize(config.host, config.port);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showMessage(String msg, GameContext context) {
    }

    @Override
    public void tellInstructions() {
        String msg = "instructions";
        m_sender.sendMessage(msg.getBytes());
    }

    @Override
    public void waitingForBets() {
        String msg = "waiting";
        m_sender.sendMessage(msg.getBytes());
    }

    @Override
    public void gameStarted(List<Bet> playerBets, GameContext context) {
        String msg = "start";
        m_sender.sendMessage(msg.getBytes());
    }

    @Override
    public void giveAdvice(int playerId, Simulator.Statistics hitOdds, Simulator.Statistics stayOdds, Hand hand, GameContext context) {
        String msg = "advice{<p>}{<h>}{<s>}{<pip>}{<b>}";
        int hit = Math.round(hitOdds.expectedROI() * 1000);
        int stay = Math.round(stayOdds.expectedROI() * 1000);
        int busted = Math.round(hitOdds.bustedRatio() * 1000);
        msg = msg.replaceAll("<p>", Integer.toString(playerId))
                .replaceAll("<h>", Integer.toString(hit))
                .replaceAll("<s>", Integer.toString(stay))
                .replaceAll("<pip>", Integer.toString(hand.getBestPipCount()))
                .replaceAll("<b>", Integer.toString(busted));

        m_sender.sendMessage(msg.getBytes());
    }

    @Override
    public void shuffle(Deck deck) {
        String msg = "shuffle";
        m_sender.sendMessage(msg.getBytes());
        //new RobotSimulation("shuffle", 2000).start();
    }

    @Override
    public void turnChanged(int playerId, GameContext context) {
        String key = playerId == GameContext.DEALER_PLAYER_ID ? GameContext.KEY_DEALER_HAND : GameContext.playerHandKey(playerId);
        Hand hand = (Hand)context.getVariable(key);
        String msg = "turn{<p>}{<h>}";
        msg = msg.replaceAll("<p>", Integer.toString(playerId))
                .replaceAll("<h>", Integer.toString(hand.getBestPipCount()));

        m_sender.sendMessage(msg.getBytes());
    }

    @Override
    public void revealDealerCard(Card card, Hand hand, GameContext context) {
        String msg = "reveal{<s>}{<r>}{<h>}";
        msg = msg.replaceAll("<s>", Integer.toString(card.getSuite().asInt()))
                .replaceAll("<r>", Integer.toString(card.getRank().getId()))
                .replaceAll("<h>", Integer.toString(hand.getBestPipCount()));

        m_sender.sendMessage(msg.getBytes());
    }

    @Override
    public void blackjack(int playerId, GameContext context) {
        String msg = "blackjack{<p>}";
        msg = msg.replaceAll("<p>", Integer.toString(playerId));
        m_sender.sendMessage(msg.getBytes());
    }

    @Override
    public void dealCard(int playerId, Card card, Hand hand, GameContext context) {
        String msg = "deal{<p>}{<s>}{<r>}";
        msg = msg.replaceAll("<p>", Integer.toString(playerId))
                .replaceAll("<s>", Integer.toString(card.getSuite().asInt()))
                .replaceAll("<r>", Integer.toString(card.getRank().getId()));

        m_sender.sendMessage(msg.getBytes());

    }

    @Override
    public void hitMe(int playerId, Card card, Hand hand, GameContext context) {
        String msg = "hit{<p>}{<c>}{<h>}";
        msg = msg.replaceAll("<p>", Integer.toString(playerId))
                .replaceAll("<c>", Integer.toString(card.getRank().getId()))
                .replaceAll("<h>", Integer.toString(hand.getBestPipCount()));

        m_sender.sendMessage(msg.getBytes());
    }

    @Override
    public void stay(int playerId, Hand hand, GameContext context) {
        String msg = "stay{<p>}{<c>}{<h>}";
        msg = msg.replaceAll("<p>", Integer.toString(playerId))
                .replaceAll("<c>", "0")
                .replaceAll("<h>", Integer.toString(hand.getBestPipCount()));

        m_sender.sendMessage(msg.getBytes());
    }

    @Override
    public void busted(int playerId, Hand hand, GameContext context) {
        String msg = "busted{<p>}";
        msg = msg.replaceAll("<p>", Integer.toString(playerId));
        m_sender.sendMessage(msg.getBytes());
    }

    @Override
    public void gameEnded(GameResult results, GameContext context) {
        //TODO tell who was busted and who won, etc
        //The data type is just BAD.
        Map<String, GameResult.Result> map = results.getResults();
        GameResult.Result p1 = map.get(GameContext.playerHandKey(0));
        GameResult.Result p2 = map.get(GameContext.playerHandKey(1));

        String strP1 = p1 != null ? p1.toString() : "Null";
        String strP2 = p2 != null ? p2.toString() : "Null";

        String msg = "results{<p1>}{<p2>}";
        msg = msg.replaceAll("<p1>", strP1)
                .replaceAll("<p2>", strP2);

        m_sender.sendMessage(msg.getBytes());
    }
}
