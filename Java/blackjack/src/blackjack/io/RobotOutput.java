package blackjack.io;

import blackjack.engine.GameContext;
import blackjack.engine.GameListener;
import blackjack.engine.Simulator;
import blackjack.models.*;
import blackjack.utils.Config;
import blackjack.utils.EventSender;

import java.util.List;

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
    public void gameStarted(List<Bet> playerBets, GameContext context) {

    }

    @Override
    public void giveAdvice(Simulator.Statistics hitOdds, Simulator.Statistics stayOdds) {

    }

    @Override
    public void shuffle(Deck deck) {
        new RobotSimulation("shuffle", 2000).start();
    }

    @Override
    public void turnChanged(int playerId, GameContext context) {

    }

    @Override
    public void revealDealerCard(Card card, Hand hand, GameContext context) {

    }

    @Override
    public void dealCard(int playerId, Card card, Hand hand, GameContext context) {
        String msg = "deal{<p>}{<s>}{<r>}";
        msg = msg.replaceAll("<p>", Integer.toString(playerId))
                .replaceAll("<s>", card.getSuite().toString())
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

    }

    @Override
    public void gameEnded(GameResult results, GameContext context) {

    }
}
