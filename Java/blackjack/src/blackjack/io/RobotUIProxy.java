package blackjack.io;

import blackjack.engine.GameContext;
import blackjack.engine.GameListener;
import blackjack.engine.Simulator;
import blackjack.models.*;
import blackjack.utils.Config;

import java.util.List;

public class RobotUIProxy implements GameListener {

    static class Delays {
        public long shuffle = 0;
        public long turnChanged = 0;
        public long revealDealerCard = 0;
        public long dealCard = 0;
        public long hitMe = 0;
        public long stay = 0;
        public long giveAdvice = 0;
        public long busted = 0;
        public long start = 0;
        public long results = 0;

    }

    private static final Delays UI_DELAYS = new Delays();
    private static final Delays ROBOT_DELAYS = new Delays();
    private static final Delays NO_DELAYS = new Delays();
    static {
        // Total delay will be robot + UI, where UI will start where robot will end
        // We could do this inside the behavior tree also, but this is simpler for debugging/development.
        //TODO map correct values here
        UI_DELAYS.shuffle = 1000;
        UI_DELAYS.turnChanged = 500;
        UI_DELAYS.revealDealerCard = 500;
        UI_DELAYS.dealCard = 1500;
        UI_DELAYS.hitMe = 500;
        UI_DELAYS.stay = 0;
        UI_DELAYS.giveAdvice = 0;
        UI_DELAYS.start = 500;
        UI_DELAYS.results = 1000;

        ROBOT_DELAYS.shuffle = 0;
        ROBOT_DELAYS.turnChanged = 500;
        ROBOT_DELAYS.revealDealerCard = 500;
        ROBOT_DELAYS.dealCard = 3500;
        ROBOT_DELAYS.hitMe = 1500;
        ROBOT_DELAYS.stay = 1500;
        ROBOT_DELAYS.giveAdvice = 5000;
        ROBOT_DELAYS.start = 500;
        ROBOT_DELAYS.results = 3000;


    }

    /**
     * We could make this into a list of listeners but we need to map custom delays between each call, so just easier this way.
     * use NullOutput to get rid of null checks
     */
    private GameListener m_console = new NullOutput();
    private GameListener m_ui = new NullOutput();
    private GameListener m_robot = new NullOutput();

    private Delays m_uiDelays = NO_DELAYS;
    private Delays m_robotDelays = NO_DELAYS;

    public RobotUIProxy(boolean console, boolean ui, boolean robot) {
        if (console) {
            m_console = new ConsoleOutput();
        }
        if (ui) {
            try {
                UnityOutput unity = new UnityOutput();
                unity.init(Config.readFromFile("ui.properties"));
                m_ui = unity;
                m_uiDelays = UI_DELAYS;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (robot) {
            try {
                //RobotOutput r = new RobotOutput();
                //We can use the same class for robot output also, why reinvent the wheel..
                RobotOutput r = new RobotOutput();
                r.init(Config.readFromFile("robot.properties"));
                m_robot = r;
                m_robotDelays = ROBOT_DELAYS;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This is just terrible, we shouldn't do this. DEBUG MOCK IMPLEMENTATION.
     * @param ms
     */
    private void sleepMs( long ms) {
        try {
            Thread.currentThread().sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showMessage(String msg, GameContext context) {
        m_console.showMessage(msg, context);
        m_robot.showMessage(msg, context);
        m_ui.showMessage(msg, context);
    }

    @Override
    public void gameStarted(List<Bet> playerBets, GameContext context) {
        m_console.gameStarted(playerBets, context);
        m_robot.gameStarted(playerBets, context);
        sleepMs(m_robotDelays.start);
        m_ui.gameStarted(playerBets, context);
        sleepMs(m_uiDelays.start);
    }

    @Override
    public void giveAdvice(int playerId, Simulator.Statistics hitOdds, Simulator.Statistics stayOdds, Hand hand, GameContext context) {
        m_console.giveAdvice(playerId, hitOdds, stayOdds, hand, context);
        m_robot.giveAdvice(playerId, hitOdds, stayOdds, hand, context);
        sleepMs(m_robotDelays.giveAdvice);
        m_ui.giveAdvice(playerId, hitOdds, stayOdds, hand, context);
        sleepMs(m_uiDelays.giveAdvice);
    }

    @Override
    public void shuffle(Deck deck) {
        m_console.shuffle(deck);
        m_robot.shuffle(deck);
        sleepMs(m_robotDelays.shuffle);
        m_ui.shuffle(deck);
        sleepMs(m_uiDelays.shuffle);
    }

    @Override
    public void turnChanged(int playerId, GameContext context) {
        m_console.turnChanged(playerId, context);
        m_robot.turnChanged(playerId, context);
        sleepMs(m_robotDelays.turnChanged);
        m_ui.turnChanged(playerId, context);
        sleepMs(m_uiDelays.turnChanged);
    }

    @Override
    public void revealDealerCard(Card card, Hand hand, GameContext context) {
        m_console.revealDealerCard(card, hand, context);
        m_robot.revealDealerCard(card, hand, context);
        sleepMs(m_robotDelays.revealDealerCard);
        m_ui.revealDealerCard(card, hand, context);
        sleepMs(m_uiDelays.revealDealerCard);
    }

    @Override
    public void dealCard(int playerId, Card card, Hand hand, GameContext context) {
        m_console.dealCard(playerId, card, hand, context);
        if (hand.cardCount() == 1) {
            m_robot.dealCard(playerId, card, hand, context);
            sleepMs(m_robotDelays.dealCard);
        }
        m_ui.dealCard(playerId, card, hand, context);
        sleepMs(m_uiDelays.dealCard);
    }

    @Override
    public void hitMe(int playerId, Card card, Hand hand, GameContext context) {
        m_console.hitMe(playerId, card, hand, context);
        m_robot.hitMe(playerId, card, hand, context);
        sleepMs(m_robotDelays.hitMe);
        m_ui.hitMe(playerId, card, hand, context);
        sleepMs(m_uiDelays.hitMe);
    }

    @Override
    public void stay(int playerId, Hand hand, GameContext context) {
        m_console.stay(playerId, hand, context);
        m_robot.stay(playerId, hand, context);
        sleepMs(m_robotDelays.stay);
        m_ui.stay(playerId, hand, context);
        sleepMs(m_uiDelays.stay);
    }

    @Override
    public void busted(int playerId, Hand hand, GameContext context) {
        m_console.busted(playerId, hand, context);
        m_robot.busted(playerId, hand, context);
        sleepMs(m_robotDelays.busted);
        m_ui.busted(playerId, hand, context);
        sleepMs(m_uiDelays.busted);
    }

    @Override
    public void gameEnded(GameResult results, GameContext context) {
        m_console.gameEnded(results, context);
        m_robot.gameEnded(results, context);
        sleepMs(m_robotDelays.results);
        m_ui.gameEnded(results, context);
        sleepMs(m_uiDelays.results);
    }

    // To get rid of null checks
    class NullOutput implements GameListener {
        @Override
        public void showMessage(String msg, GameContext context) {}

        @Override
        public void gameStarted(List<Bet> playerBets, GameContext context) {}
        @Override
        public void giveAdvice(int playerId, Simulator.Statistics hitOdds, Simulator.Statistics stayOdds, Hand hand, GameContext context) {}
        @Override
        public void shuffle(Deck deck) {}
        @Override
        public void turnChanged(int playerId, GameContext context) {}
        @Override
        public void revealDealerCard(Card card, Hand hand, GameContext context) {}
        @Override
        public void dealCard(int playerId, Card card, Hand hand, GameContext context) {}
        @Override
        public void hitMe(int playerId, Card card, Hand hand, GameContext context) {}
        @Override
        public void stay(int playerId, Hand hand, GameContext context) {}
        @Override
        public void busted(int playerId, Hand hand, GameContext context) {}
        @Override
        public void gameEnded(GameResult results, GameContext context) {}
    }

}
