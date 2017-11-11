package blackjack.tree;

import behave.execution.ExecutionContext;
import behave.models.CompositeNode;
import behave.models.LeafNode;
import behave.models.Types;
import behave.tools.Log;
import blackjack.engine.GameListener;
import blackjack.engine.InputManager;
import blackjack.models.Card;
import blackjack.models.Deck;
import blackjack.engine.GameContext;
import blackjack.models.Hand;
import sun.awt.image.ImageWatched;

import java.util.LinkedList;

/**
 * Plays a single game of blackjack.
 */
public class GameNode extends CompositeNode.SequenceNode {
    InputManager m_input;
    Deck m_deck;
    GameContext m_context;
    private int m_deckCount = 1;

    private LinkedList<GameListener> m_listeners = new LinkedList<>();

    public GameNode(InputManager input, int howManyDecks) {
        m_input = input;
        m_deckCount = howManyDecks;
        reset();
        createTree();
    }

    public void addListener(GameListener l) {
        m_listeners.add(l);
    }

    private void notifyListeners(String msg) {
        //debug
        for (GameListener l: m_listeners) {
            l.showMessage(">" + msg, m_context);
        }
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
        //TODO remove all children and call createTree.

        //We could also put the InputManager inside the context?
    }

    private void createTree() {
        addChild(new InitGameVariablesNode());
        addChild(new DealInitCardsNode());
    }

    //////////////////////////////////////////////////////////////////
    // Nodes which form the finite state machine as a behaviour tree.
    //////////////////////////////////////////////////////////////////

    private class InitGameVariablesNode extends LeafNode {
        @Override
        public Types.Status tick(ExecutionContext context) {
            int playerCount = m_input.getPlayerCount();
            m_context.setVariable(GameContext.KEY_PLAYER_COUNT, playerCount);
            notifyListeners("Player count determined as " + playerCount);
            return Types.Status.Success;
        }
    }

    private class DealInitCardsNode extends SequenceNode {
        @Override public void initialize(ExecutionContext context) {
            Hand dealerHand = new Hand();
            addChild(new DealInitHandNode(dealerHand, true));
            context.setVariable(GameContext.KEY_DEALER_HAND, dealerHand);

            int playerCount = (int)m_context.getVariable(GameContext.KEY_PLAYER_COUNT); // Some type safety would be nice..
            for (int n = 0; n < playerCount; n++) {
                Hand h = new Hand();
                m_context.setVariable(GameContext.KEY_PLAYER_HAND_PREFIX + n, h); //Not nice, TODO better
                addChild(new DealInitHandNode(h, false));
            }

            super.initialize(context);

        }

    }

    private class DealInitHandNode extends SequenceNode {
        public DealInitHandNode(Hand hand, boolean dealer) {
            addChild(new DealSingleCardNode(hand, false));
            addChild(new DealSingleCardNode(hand, dealer));
        }
    }



    private class DealSingleCardNode extends LeafNode {
        private Hand m_hand;
        private boolean m_asHidden;
        public DealSingleCardNode(Hand hand, boolean asHidden) {
            m_hand = hand;
            m_asHidden = asHidden;
        }

        @Override
        public Types.Status tick(ExecutionContext context) {
            Card c = m_deck.getNextCard();
            c.setHidden(m_asHidden);
            m_hand.addCard(c);
            notifyListeners("Dealt card " + c);
            return Types.Status.Success;
        }
    }

}
