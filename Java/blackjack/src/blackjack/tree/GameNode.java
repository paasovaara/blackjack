package blackjack.tree;

import behave.execution.ExecutionContext;
import behave.models.CompositeNode;
import behave.models.LeafNode;
import behave.models.Types;
import behave.tools.Log;
import blackjack.engine.InputManager;
import blackjack.models.Deck;
import blackjack.engine.GameContext;

/**
 * Single game of blackjack. Should this extend CompositeNode?
 */
public class GameNode extends CompositeNode.SequenceNode {
    InputManager m_input;
    Deck m_deck;
    GameContext m_context;
    private int m_deckCount = 1;

    public GameNode(InputManager input, int howManyDecks) {
        m_input = input;
        m_deckCount = howManyDecks;
        reset();
        createTree();
    }

    /**
     * Context contains the state of this whole game
     */
    public GameContext getContext() {
        return m_context;
    }

    public void reset() {
        m_deck = new Deck(m_deckCount);
        m_deck.shuffle();

        m_context = new GameContext();
        m_context.setVariable(GameContext.KEY_DECK, m_deck);
    }

    private void createTree() {
        addChild(new InitGameVariablesNode());
    }

    class InitGameVariablesNode extends LeafNode {
        @Override
        public Types.Status tick(ExecutionContext context) {
            int playerCount = m_input.getPlayerCount();
            m_context.setVariable(GameContext.KEY_PLAYER_COUNT, playerCount);
            return Types.Status.Success;
        }
    }

}
