package blackjack.engine;

import behave.execution.Executor;
import behave.models.DecoratorNode;
import behave.models.Node;
import blackjack.io.*;
import blackjack.tree.GameNode;
import blackjack.utils.Config;

import java.util.Random;

/**
 * Bunch of static methods for creating the game
 */
public class BlackJack {
    public static final int DEFAULT_DECK_COUNT = 1;

    private static Executor m_executor = new Executor(); // TODO think if we need custom executor

    private static Random m_random = new Random();

    public static GameNode createConsoleGame() {
        InputManager input = new ConsoleInput();
        GameNode game = new GameNode(input, DEFAULT_DECK_COUNT);
        game.addListener(new ConsoleOutput());
        return game;
    }

    public static GameNode createRobotWithUIGame() {
        //InputManager input = new ConsoleInput();
        SensorInput input = new SensorInput();
        try {
            input.initialize(Config.readFromFile("sensors.properties").port);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        GameNode game = new GameNode(input, DEFAULT_DECK_COUNT);
        /*try {
            UnityOutput output = new UnityOutput();
            output.init(Config.readFromFile("ui.properties"));
            game.addListener(output);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        RobotOutput robot = new RobotOutput();
        game.addListener(robot);*/
        RobotUIProxy listener = new RobotUIProxy(true, true, false);
        game.addListener(listener);
        return game;
    }


    public static void playGame(boolean console) {

        GameNode game = console ? createConsoleGame() : createRobotWithUIGame();
        Node root = new DecoratorNode.InfiniteRepeaterNode();
        root.addChild(game);
        m_executor.initialize(root, game.getContext());
        m_executor.start(100, 0);
    }

    //TODO think about this..
    public static void quitGame() {
        m_executor.stop();
    }

}
