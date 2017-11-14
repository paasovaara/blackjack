package blackjack.engine;

import behave.execution.Executor;
import behave.models.DecoratorNode;
import behave.models.Node;
import blackjack.io.ConsoleInput;
import blackjack.io.ConsoleOutput;
import blackjack.io.UnityOutput;
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
        //game.addListener(new ConsoleOutput());
        try {
            UnityOutput output = new UnityOutput();
            output.init(Config.readFromFile("ui.properties"));
            game.addListener(output);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return game;
    }

    public static void playGame() {
        GameNode game = createConsoleGame();
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
