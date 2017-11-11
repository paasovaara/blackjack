package blackjack.engine;

import behave.execution.Executor;
import behave.models.Node;
import blackjack.tree.GameNode;

public class BlackJack {
    public static final int DEFAULT_DECK_COUNT = 1;

    private static Executor m_executor = new Executor(); // TODO think if we need custom executor

    public static GameNode createConsoleGame() {
        InputManager input = new ConsoleInput();
        return new GameNode(input, DEFAULT_DECK_COUNT);
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
