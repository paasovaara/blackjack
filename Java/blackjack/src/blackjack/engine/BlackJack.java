package blackjack.engine;

import behave.execution.Executor;
import behave.models.DecoratorNode;
import behave.models.Node;
import behave.tools.Log;
import blackjack.io.*;
import blackjack.models.GameSettings;
import blackjack.tree.GameNode;
import blackjack.utils.Config;
import blackjack.utils.ConfigUtils;

import java.util.Properties;
import java.util.Random;

/**
 * Bunch of static methods for creating the game
 */
public class BlackJack {
    private static Executor m_executor = new Executor(); // TODO think if we need custom executor

    private static Random m_random = new Random();

    public static GameNode createConsoleGame() {
        InputManager input = new ConsoleInput();
        GameNode game = new GameNode(input, GameSettings.DEFAULT);
        game.addListener(new ConsoleOutput());
        return game;
    }

    public static GameNode createAIGame() {
        InputManager input = new AIPlayerInput();
        GameNode game = new GameNode(input, GameSettings.DEFAULT);
        game.addListener(new ConsoleOutput());
        return game;
    }


    public static GameNode createRobotWithUIGame(boolean sensorInput, boolean robot) {
        InputManager input;
        SensorInput sensors = null;
        if (sensorInput) {
            sensors = new SensorInput();
            try {
                sensors.initialize(Config.readFromFile("sensors.properties").port);
            }
            catch (Exception e) {
                Log.error("Could not read sensors.properties file: " + e.getMessage());
                e.printStackTrace();
            }
            input = sensors;
        }
        else {
            input = new ConsoleInput();
        }
        GameNode game = new GameNode(input, GameSettings.DEFAULT);

        RobotUIProxy listener = new RobotUIProxy(true, true, robot, sensors);
        game.addListener(listener);
        return game;
    }


    public static void playGame(boolean console, boolean sensors, boolean robot) {

        GameNode game = console ? createConsoleGame() : createRobotWithUIGame(sensors, robot);
        Node root = new DecoratorNode.InfiniteRepeaterNode();
        root.addChild(game);
        m_executor.initialize(root, game.getContext());
        m_executor.start(100, 0);
    }

    public static void simulateGame() {
        GameNode game = createAIGame();
        Node root = new DecoratorNode.FiniteRepeaterNode(5);
        root.addChild(game);
        m_executor.initialize(root, game.getContext());
        m_executor.start(10, 0);
    }


    //TODO think about this..
    public static void quitGame() {
        m_executor.stop();
    }

    private static int s_winnings = -1;
    public static int readTotalWinnings() {
        if (s_winnings < 0) {
            try {
                Properties props = ConfigUtils.readPropertiesFile("winnings.properties");
                String strWins = props.getProperty("winnings", "0");
                s_winnings = Integer.parseInt(strWins);
            }
            catch (Exception e){
                System.out.println("Could not read winnings from file");
                e.printStackTrace();
                s_winnings = 0;
            }

        }
        return s_winnings;
    }

    public static void saveTotalWinnings(int winnings) {
        s_winnings = winnings;
        try {
            Properties props = new Properties();
            props.setProperty("winnings", Integer.toString(s_winnings));
            ConfigUtils.writePropertiesFile("winnings.properties", props);
        }
        catch (Exception e) {
            System.out.println("Failed to wrinte winnings to file");
            e.printStackTrace();
        }
    }

}
