package blackjack.io;

import blackjack.engine.GameContext;
import blackjack.engine.GameListener;
import blackjack.engine.Simulator;
import blackjack.models.*;
import blackjack.utils.Config;

import java.util.List;

public class RobotUIProxy implements GameListener {

    static class Delays {
        public long waitForBets = 0;
        public long shuffle = 0;
        public long turnChanged = 0;
        public long revealDealerCard = 0;
        public long dealCard = 0;
        public long hitMe = 0;
        public long stay = 0;
        public long giveAdvice = 0;
        public long busted = 0;
        public long blackjack = 0;
        public long start = 0;
        public long results = 0;
        public long instructions = 0;

    }

    private static final Delays UI_DELAYS = new Delays();
    private static final Delays ROBOT_DELAYS = new Delays();
    private static final Delays NO_DELAYS = new Delays();
    static {
        // Total delay will be robot + UI, where UI will start where robot will end
        // We could do this inside the behavior tree also, but this is simpler for debugging/development.
        //TODO map correct values here
        UI_DELAYS.waitForBets = 0;
        UI_DELAYS.shuffle = 2000;
        UI_DELAYS.turnChanged = 0;
        UI_DELAYS.revealDealerCard = 3500;
        UI_DELAYS.dealCard = 1500;
        UI_DELAYS.hitMe = 600;
        UI_DELAYS.stay = 0;
        UI_DELAYS.giveAdvice = 0;
        UI_DELAYS.start = 500;
        UI_DELAYS.results = 1000;
        UI_DELAYS.blackjack = 0;
        UI_DELAYS.instructions = 0;
        UI_DELAYS.busted = 0;

        ROBOT_DELAYS.waitForBets = 5500;
        ROBOT_DELAYS.shuffle = 0;
        ROBOT_DELAYS.turnChanged = 3000;
        ROBOT_DELAYS.revealDealerCard = 4000;
        ROBOT_DELAYS.dealCard = 3500;
        ROBOT_DELAYS.hitMe = 500;
        ROBOT_DELAYS.stay = 2000;
        ROBOT_DELAYS.giveAdvice = 3000;
        ROBOT_DELAYS.start = 4500;
        ROBOT_DELAYS.results = 3000;
        ROBOT_DELAYS.blackjack = 1000;
        ROBOT_DELAYS.instructions = 25000;
        ROBOT_DELAYS.busted = 500;

    }

    /**
     * We could make this into a list of listeners but we need to map custom delays between each call, so just easier this way.
     * use NullOutput to get rid of null checks
     */
    private GameListener m_console = new NullOutput();
    private GameListener m_ui = new NullOutput();
    private GameListener m_robot = new NullOutput();

    private UnityOutput m_unity = null; // NOt nice but no time to do this correctly. TODO refactor

    private Delays m_uiDelays = NO_DELAYS;
    private Delays m_robotDelays = NO_DELAYS;

    public RobotUIProxy(boolean console, boolean ui, boolean robot, SensorInput sensors) {
        if (console) {
            m_console = new ConsoleOutput();
        }
        if (ui) {
            try {
                UnityOutput unity = new UnityOutput();
                unity.init(Config.readFromFile("ui.properties"));
                m_ui = unity;
                m_unity = unity;
                m_uiDelays = UI_DELAYS;
                if (sensors != null) {
                    sensors.getBetManager().addListener(unity);
                    m_unity.setUpdateBets(false);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (robot) {
            try {
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

    private void setBetUpdateActive(boolean update) {
        if (m_unity != null) {
            m_unity.setUpdateBets(update);
        }
    }
    @Override
    public void waitingForBets() {
        setBetUpdateActive(true);

        m_console.waitingForBets();
        m_robot.waitingForBets();
        sleepMs(ROBOT_DELAYS.waitForBets);
        m_ui.waitingForBets();
        sleepMs(UI_DELAYS.waitForBets);
    }

    @Override
    public void gameStarted(List<Bet> playerBets, GameContext context) {
        setBetUpdateActive(false);

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
    public void tellInstructions() {
        m_console.tellInstructions();
        m_ui.tellInstructions();
        sleepMs(m_uiDelays.instructions);
        m_robot.tellInstructions();
        sleepMs(m_robotDelays.instructions);
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
        m_ui.revealDealerCard(card, hand, context);
        sleepMs(m_uiDelays.revealDealerCard);
        m_robot.revealDealerCard(card, hand, context);
        sleepMs(m_robotDelays.revealDealerCard);

    }

    @Override
    public void blackjack(int playerId, GameContext context) {
        m_console.blackjack(playerId, context);
        m_ui.blackjack(playerId, context);
        sleepMs(m_uiDelays.blackjack);
        m_robot.blackjack(playerId, context);
        sleepMs(m_robotDelays.blackjack);
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
        m_ui.hitMe(playerId, card, hand, context);
        sleepMs(m_uiDelays.hitMe);
        m_robot.hitMe(playerId, card, hand, context);
        sleepMs(m_robotDelays.hitMe);

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
        m_ui.busted(playerId, hand, context);
        sleepMs(m_uiDelays.busted);
        m_robot.busted(playerId, hand, context);
        sleepMs(m_robotDelays.busted);

    }

    @Override
    public void gameEnded(GameResult results, GameContext context) {
        m_console.gameEnded(results, context);
        m_ui.gameEnded(results, context);
        sleepMs(m_uiDelays.results);
        m_robot.gameEnded(results, context);
        sleepMs(m_robotDelays.results);

    }

    // To get rid of null checks
    class NullOutput implements GameListener {
        @Override
        public void showMessage(String msg, GameContext context) {}

        @Override
        public void waitingForBets() {}
        @Override
        public void gameStarted(List<Bet> playerBets, GameContext context) {}
        @Override
        public void giveAdvice(int playerId, Simulator.Statistics hitOdds, Simulator.Statistics stayOdds, Hand hand, GameContext context) {}
        @Override
        public void tellInstructions() {}
        @Override
        public void shuffle(Deck deck) {}
        @Override
        public void turnChanged(int playerId, GameContext context) {}
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
        @Override
        public void gameEnded(GameResult results, GameContext context) {}
    }

}
