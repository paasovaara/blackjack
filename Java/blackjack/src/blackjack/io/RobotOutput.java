package blackjack.io;

import blackjack.engine.GameContext;
import blackjack.engine.GameListener;
import blackjack.models.Card;
import blackjack.models.Deck;
import blackjack.models.Hand;

public class RobotOutput implements GameListener {

    /*
    We simulate the robot as a separate thread for each movement because that's what it essentially is for us.
    We have no control wether the robot has finished it's movements in time or not, we just have to assume it does something in fixed time.
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

    @Override
    public void showMessage(String msg, GameContext context) {
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
        new RobotSimulation("DEAL", 2000).start();
    }

    @Override
    public void hitMe(int playerId, Card card, Hand hand, GameContext context) {

    }

    @Override
    public void stay(int playerId, Hand hand, GameContext context) {

    }

    @Override
    public void busted(int playerId, Hand hand, GameContext context) {

    }
}
