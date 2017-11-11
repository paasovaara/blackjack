package blackjack.engine;

import behave.execution.Executor;
import behave.models.Node;
import blackjack.models.Card;
import blackjack.tree.GameNode;

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

    public static void playGame() {
        GameNode game = createConsoleGame();
        m_executor.initialize(game, game.getContext());
        m_executor.start(100, 0);
    }

    //TODO think about this..
    public static void quitGame() {
        m_executor.stop();
    }

}
