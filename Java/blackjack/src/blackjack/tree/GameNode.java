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
import blackjack.models.GameResult;
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
        addChild(new DetermineWinnerNode());
    }

    //////////////////////////////////////////////////////////////////
    // Nodes which form the finite state machine as a behaviour tree.
    //////////////////////////////////////////////////////////////////

    private class InitGameVariablesNode extends LeafNode {
        @Override
        public Types.Status tick(ExecutionContext context) {
            int playerCount = m_input.getPlayerCount();
            m_context.setVariable(GameContext.KEY_PLAYER_COUNT, playerCount);

            GameResult result = new GameResult();
            m_context.setVariable(GameContext.KEY_RESULTS, result);

            notifyListeners("Player count determined as " + playerCount);
            return Types.Status.Success;
        }
    }

    private class NotifyTurnChangeNode extends LeafNode {
        String m_key;
        public NotifyTurnChangeNode(String playerKey) {
            m_key = playerKey;
        }

        @Override
        public Types.Status tick(ExecutionContext context) {
            notifyListeners("__ Player " + m_key + " turn. __");
            return Types.Status.Success;
        }
    }

    private class DealInitCardsNode extends SequenceNode {
        @Override public void initialize(ExecutionContext context) {
            Hand dealerHand = new Hand();
            addChild(new NotifyTurnChangeNode(GameContext.KEY_DEALER_HAND));
            addChild(new DealInitHandNode(dealerHand, true));
            context.setVariable(GameContext.KEY_DEALER_HAND, dealerHand);

            int playerCount = (int)m_context.getVariable(GameContext.KEY_PLAYER_COUNT); // Some type safety would be nice..
            for (int n = 0; n < playerCount; n++) {
                Hand h = new Hand();
                String key = GameContext.playerHandKey(n);
                m_context.setVariable(key, h); //Not nice, TODO better
                addChild(new NotifyTurnChangeNode(key));
                addChild(new DealInitHandNode(h, false));
            }
            //addChild(new CheckDealerHiddenCardNode());

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
    /*
    private class CheckDealerHiddenCardNode extends LeafNode {
        @Override
        public Types.Status tick(ExecutionContext context) {
            Hand hand = (Hand)context.getVariable(GameContext.KEY_DEALER_HAND);
            //In this point we could offer insurance if visible card would be 10 or 11.
            if (hand.isBlackJack()) {
                hand.revealeHiddenCards();
                notifyListeners("Dealer has BlackJack!");
            }
            return Types.Status.Success;
        }
    }*/

    // Actual game is in this node: =============================
    private class DetermineWinnerNode extends SelectorNode {
        public DetermineWinnerNode() {
            addChild(new CheckBlackJacksNode());
            addChild(new PlayAllHandsNode());
        }
    }

    private class CheckBlackJacksNode extends LeafNode {
        @Override
        public Types.Status tick(ExecutionContext context) {
            GameResult result = (GameResult)context.getVariable(GameContext.KEY_RESULTS);
            Hand hand = (Hand)context.getVariable(GameContext.KEY_DEALER_HAND);
            //In this point we could offer insurance if visible card would be 10 or 11.
            boolean dealerHasBj = false;
            if (hand.isBlackJack()) {
                hand.revealeHiddenCards();
                notifyListeners("Dealer has BlackJack!");
                result.setResult(GameContext.KEY_DEALER_HAND, GameResult.Result.Won);
                dealerHasBj = true;
            }

            int playerCount = (int)m_context.getVariable(GameContext.KEY_PLAYER_COUNT);
            int blackjackCount = 0;
            for (int n = 0; n < playerCount; n++) {
                String key = GameContext.playerHandKey(n);
                Hand playerHand = (Hand)m_context.getVariable(key);
                if (playerHand.isBlackJack()) {
                    notifyListeners("Player " + key + " has BlackJack!");
                    GameResult.Result playerRes = dealerHasBj ? GameResult.Result.Tied : GameResult.Result.Won;
                    result.setResult(key, playerRes);
                    blackjackCount++;
                }
                else if (dealerHasBj) {
                    result.setResult(key, GameResult.Result.Lost);
                }
            }

            // return Success if we want the game round to end immediately after this node
            // This is the case if dealer has BlackJack and/or all players have blackjack
            if (dealerHasBj || blackjackCount == playerCount) {
                return Types.Status.Success;
            }
            else {
                return Types.Status.Failure;
            }
        }
    }

    private class PlayAllHandsNode extends SequenceNode {
        @Override public void initialize(ExecutionContext context) {
            int playerCount = (int)m_context.getVariable(GameContext.KEY_PLAYER_COUNT); // Some type safety would be nice..
            for (int n = 0; n < playerCount; n++) {
                String key = GameContext.playerHandKey(n);
                addChild(new NotifyTurnChangeNode(key));
                addChild(new PlayPlayerHandNode(key));
            }
            addChild(new NotifyTurnChangeNode(GameContext.KEY_DEALER_HAND));
            addChild(new PlayPlayerHandNode(GameContext.KEY_DEALER_HAND));

            super.initialize(context);
        }
    }

    private class PlayPlayerHandNode extends SequenceNode { // or leaf?
        private String m_keyForHand;
        public PlayPlayerHandNode(String keyForPlayerHand) {
            m_keyForHand = keyForPlayerHand;
        }

        @Override public void initialize(ExecutionContext context) {
            Hand hand = (Hand)m_context.getVariable(m_keyForHand);


            super.initialize(context);
        }
    }

}
