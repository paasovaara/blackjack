package blackjack.engine;

import behave.execution.Executor;
import behave.models.DecoratorNode;
import behave.models.Node;
import behave.tools.Log;
import blackjack.ai.AITrainingDataCollector;
import blackjack.ai.AITrainingDataGenerator;
import blackjack.io.console.ConsoleInput;
import blackjack.io.console.ConsoleOutput;
import blackjack.io.sensors.SensorInput;
import blackjack.io.views.RobotUIProxy;
import blackjack.models.GameSettings;
import blackjack.tree.GameNode;
import blackjack.utils.Config;
import blackjack.utils.ConfigUtils;

import java.util.Properties;

/**
 * Bunch of static methods for creating the game
 */
public class BlackJack {
    private static Executor m_defaultExecutor = new Executor(); // TODO think if we need custom executor
    private static Executor m_aiExecutor = new AIGameExecutor();

    public static GameNode createConsoleGame() {
        GameSettings settings = GameSettings.DEFAULT;
        InputManager input = new ConsoleInput(settings);
        GameNode game = new GameNode(input, settings);
        game.addListener(new ConsoleOutput());
        return game;
    }

    public static GameNode createAIGame() {
        try {
            AITrainingDataCollector trainingCollector = new AITrainingDataCollector();
            trainingCollector.initialize("dataset.txt");
            GameNode game = new GameNode(trainingCollector, GameSettings.AI_DEFAULT);
            game.addListener(trainingCollector);
            return game;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void generateTrainingDataBySimulating() {
        try {
            AITrainingDataGenerator.generateAndSave("dataset-simulated.txt");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static GameNode createRobotWithUIGame(boolean sensorInput, boolean robot) {
        InputManager input;
        SensorInput sensors = null;
        GameSettings settings = GameSettings.DEFAULT;
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
            input = new ConsoleInput(settings);
        }
        GameNode game = new GameNode(input, settings);

        RobotUIProxy listener = new RobotUIProxy(true, true, robot, sensors);
        game.addListener(listener);
        return game;
    }


    public static void playGame(boolean console, boolean sensors, boolean robot) {

        GameNode game = console ? createConsoleGame() : createRobotWithUIGame(sensors, robot);
        Node root = new DecoratorNode.InfiniteRepeaterNode();
        root.addChild(game);
        m_defaultExecutor.initialize(root, game.getContext());
        m_defaultExecutor.start(100, 0);
    }

    public static void trainAiGame() {
        GameNode game = createAIGame();
        Node root = new DecoratorNode.FiniteRepeaterNode(50);
        root.addChild(game);
        m_aiExecutor.initialize(root, game.getContext());
        m_aiExecutor.start(0, 0);
    }


    //TODO think about this..
    public static void quitGame() {
        m_defaultExecutor.stop();
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
