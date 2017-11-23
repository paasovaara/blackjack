package blackjack.tree;

import behave.execution.ExecutionContext;
import behave.models.*;
import blackjack.engine.*;
import blackjack.models.*;

import java.util.*;

/**
 * Plays a single game of blackjack.
 */
public class GameNode extends CompositeNode.SequenceNode {
    InputManager m_input;
    Deck m_deck;
    GameContext m_context;
    private int m_deckCount = 1;

    private static final float SHUFFLE_DECK_RATIO = 0.5f;
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

    //TODO should this block?
    private void notifyPlayerAction(int playerId, Card latestCard, Hand hand, PlayerAction action) {
        for (GameListener l: m_listeners) {
            if (action == PlayerAction.Hit) {
                l.hitMe(playerId, latestCard, hand, m_context);
            }
            else if (action == PlayerAction.Stay) {
                l.stay(playerId, hand, m_context);
            }
        }
    }

    private void notifyDealerAction(int playerId, Card latestCard, Hand hand, DealerAction action) {
        for (GameListener l: m_listeners) {
            if (action == DealerAction.DealCard) {
                l.dealCard(playerId, latestCard, hand, m_context);
            }
            else if (action == DealerAction.ChangeTurn) {
                l.turnChanged(playerId, m_context);
            }
            else if (action == DealerAction.RevealDealerCard) {
                l.revealDealerCard(latestCard, hand, m_context);
            }
            else if (action == DealerAction.Shuffle) {
                l.shuffle(m_deck);
            }
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
        notifyDealerAction(GameContext.DEALER_PLAYER_ID, null, null, DealerAction.Shuffle);

        m_context = new GameContext();
        m_context.setVariable(GameContext.KEY_DECK, m_deck);
        //TODO remove all children and call createTree.

        //We could also put the InputManager inside the context?
    }

    private void createTree() {
        addChild(new InitGameVariablesNode());
        addChild(new DealInitCardsNode());
        addChild(new PlayTheGameNode());
    }

    //////////////////////////////////////////////////////////////////
    // Nodes which form the finite state machine as a behaviour tree.
    //////////////////////////////////////////////////////////////////

    private class InitGameVariablesNode extends LeafNode {
        @Override
        public Types.Status tick(ExecutionContext context) {
            m_context.clear();
            if (m_deck.deckRemaining() < SHUFFLE_DECK_RATIO) {
                m_deck.shuffle();
                notifyDealerAction(GameContext.DEALER_PLAYER_ID, null, null, DealerAction.Shuffle);
            }

            List<Bet> bets = m_input.getBets();
            for(Bet bet: bets) {
                String playerBetKey = GameContext.playerBetKey(bet.playerId);
                m_context.setVariable(playerBetKey, bet.betAmount);
            }

            int playerCount = bets.size();
            m_context.setVariable(GameContext.KEY_PLAYER_COUNT, playerCount);

            GameResult result = new GameResult();
            result.setBets(bets);
            m_context.setVariable(GameContext.KEY_RESULTS, result);

            if (playerCount > 0) {
                for (GameListener l: m_listeners) {
                    l.gameStarted(bets, m_context);
                }
                return Types.Status.Success;
            }
            else {
                m_listeners.forEach(GameListener::waitingForBets);
                return Types.Status.Failure;
            }
        }
    }

    private class NotifyTurnChangeNode extends LeafNode {
        int m_id;
        public NotifyTurnChangeNode(int playerId) {
            m_id = playerId;
        }

        @Override
        public Types.Status tick(ExecutionContext context) {
            notifyDealerAction(m_id, null, null, DealerAction.ChangeTurn);
            return Types.Status.Success;
        }
    }

    private class DealInitCardsNode extends SequenceNode {
        @Override public void initialize(ExecutionContext context) {
            removeChildren();

            Hand dealerHand = new Hand();
            addChild(new NotifyTurnChangeNode(GameContext.DEALER_PLAYER_ID));
            addChild(new DealInitHandNode(GameContext.DEALER_PLAYER_ID, dealerHand, true));
            context.setVariable(GameContext.KEY_DEALER_HAND, dealerHand);
            //TODO we cannot actually just count id's from zero, we'd need to check each player -> in or out
            int playerCount = (int)m_context.getVariable(GameContext.KEY_PLAYER_COUNT); // Some type safety would be nice..
            for (int n = 0; n < playerCount; n++) {
                Hand h = new Hand();
                String key = GameContext.playerHandKey(n);
                m_context.setVariable(key, h); //Not nice, TODO better
                addChild(new NotifyTurnChangeNode(n));
                addChild(new DealInitHandNode(n, h, false));
            }

            super.initialize(context);
        }
    }

    private class DealInitHandNode extends SequenceNode {
        public DealInitHandNode(int playerId, Hand hand, boolean dealer) {
            addChild(new DealSingleCardNode(playerId, hand, false));
            addChild(new DealSingleCardNode(playerId, hand, dealer));
        }
    }

    private class DealSingleCardNode extends LeafNode {
        private Hand m_hand;
        private boolean m_asHidden;
        private int m_playerId;
        public DealSingleCardNode(int playerId, Hand hand, boolean asHidden) {
            m_hand = hand;
            m_asHidden = asHidden;
            m_playerId = playerId;
        }

        @Override
        public Types.Status tick(ExecutionContext context) {
            Card c = m_deck.getNextCard();
            c.setHidden(m_asHidden);
            m_hand.addCard(c);
            notifyDealerAction(m_playerId, c, m_hand, DealerAction.DealCard);
            return Types.Status.Success;
        }
    }

    // Actual game is in this node: =============================
    private class PlayTheGameNode extends SelectorNode {
        public PlayTheGameNode() {
            addChild(new CheckBlackJacksNode());
            addChild(new PlayUntilWinnerFoundNode());
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
                hand.revealHiddenCard();
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
                for (GameListener l: m_listeners) {
                    l.gameEnded(result, m_context);
                }
                return Types.Status.Success;
            }
            else {
                return Types.Status.Failure;
            }
        }
    }

    private class PlayUntilWinnerFoundNode extends SequenceNode {
        @Override public void initialize(ExecutionContext context) {
            removeChildren();

            int playerCount = (int)m_context.getVariable(GameContext.KEY_PLAYER_COUNT); // Some type safety would be nice..
            for (int n = 0; n < playerCount; n++) {
                String key = GameContext.playerHandKey(n);
                addChild(new NotifyTurnChangeNode(n));
                addChild(new PlayPlayerHandNode(n));
            }
            addChild(new NotifyTurnChangeNode(GameContext.DEALER_PLAYER_ID));
            addChild(new RevealDealerHandNode());
            addChild(new PlayDealerHandIfNecessary());

            addChild(new DetermineWinnerNode());
            super.initialize(context);
        }
    }

    private class PlayPlayerHandNode extends SequenceNode {
        private int m_playerId;
        public PlayPlayerHandNode(int playerId) {
            m_playerId = playerId;
            Node repeater = new DecoratorNode.RepeatUntilSuccessNode();
            addChild(repeater);

            Node seq = new SequenceNode();
            repeater.addChild(seq);

            seq.addChild(new ChooseOptionNode(playerId));
            seq.addChild(new HandleActionNode());
        }

        @Override public void initialize(ExecutionContext context) {
            m_context.setVariable(GameContext.KEY_PLAYER_IN_TURN_ID, m_playerId);
            super.initialize(context);
        }
    }

    private class PlayDealerHandIfNecessary extends SelectorNode {
        public PlayDealerHandIfNecessary() {
            addChild(new CheckIfAllBustedNode());
            addChild(new PlayDealerHandNode());
        }
    }

    private class CheckIfAllBustedNode extends LeafNode {
        @Override
        public Types.Status tick(ExecutionContext context) {
            int playerCount = (int)m_context.getVariable(GameContext.KEY_PLAYER_COUNT);
            int busted = 0;
            for (int n = 0; n < playerCount; n++) {
                String key = GameContext.playerHandKey(n);
                Hand hand = (Hand) m_context.getVariable(key);
                if (hand.isBusted())
                    busted++;
            }
            //if all busted we stop execution, otherwise we continue
            return (busted == playerCount) ? Types.Status.Success : Types.Status.Failure;
        }
    }


    private class PlayDealerHandNode extends SequenceNode {
        public PlayDealerHandNode() {
            Node repeater = new DecoratorNode.RepeatUntilSuccessNode();
            addChild(repeater);

            repeater.addChild(new DealerHitUnder17Node());
        }

        @Override public void initialize(ExecutionContext context) {
            m_context.setVariable(GameContext.KEY_PLAYER_IN_TURN_ID, GameContext.DEALER_PLAYER_ID);
            super.initialize(context);
        }
    }

    private class DealerHitUnder17Node extends LeafNode {
        @Override
        public Types.Status tick(ExecutionContext context) {
            Hand hand = (Hand)m_context.getVariable(GameContext.KEY_DEALER_HAND);
            if (hand.isBusted()) {
                return Types.Status.Success;
            }
            else if (hand.getBestPipCount() >= 17) {
                notifyPlayerAction(GameContext.DEALER_PLAYER_ID, null, hand, PlayerAction.Stay);
                return Types.Status.Success;
            }
            else {
                //Hit, check result on next run
                Card c = m_deck.getNextCard();
                hand.addCard(c);

                notifyPlayerAction(GameContext.DEALER_PLAYER_ID, c, hand, PlayerAction.Hit);
                return Types.Status.Failure;
            }
        }
    }

    private class RevealDealerHandNode extends LeafNode {

        @Override
        public Types.Status tick(ExecutionContext context) {
            Hand hand = (Hand)m_context.getVariable(GameContext.KEY_DEALER_HAND);
            Card c = hand.revealHiddenCard();
            notifyDealerAction(GameContext.DEALER_PLAYER_ID, c, hand, DealerAction.RevealDealerCard);
            return Types.Status.Success;
        }
    }

    private class HandleActionNode extends LeafNode {

        @Override
        public Types.Status tick(ExecutionContext context) {
            Types.Status status = Types.Status.Success;

            int playerId = (int)m_context.getVariable(GameContext.KEY_PLAYER_IN_TURN_ID);
            PlayerAction action = (PlayerAction)m_context.getVariable(GameContext.KEY_PLAYER_ACTION);
            Hand hand = (Hand)m_context.getVariable(GameContext.playerHandKey(playerId));
            Card card = null;
            if (action == PlayerAction.Hit) {
                card = m_deck.getNextCard();
                hand.addCard(card);

                if (hand.isBusted()) {
                    notifyListeners("BUSTED");
                    status = Types.Status.Success;
                }
                else {
                    //report failure so we keep coming back to this node after selecting the option
                    status = Types.Status.Failure;
                }
            }
            else {
                status = Types.Status.Success;
            }
            //TODO should this block?! and/or read return code to determine status?
            notifyPlayerAction(playerId, card, hand, action);
            return status;
        }
    }

    private class ChooseOptionNode extends LeafNode.AsyncLeafNode { // or leaf?
        private int m_playerId;

        public ChooseOptionNode(int playerId) {
            m_playerId = playerId;
        }

        @Override
        protected Types.Status runBlockingTask() {
            Set<PlayerAction> options = new HashSet<>();
            options.add(PlayerAction.Hit);
            options.add(PlayerAction.Stay);
            options.add(PlayerAction.QuitGame);
            //we could add the other options like double, split, etc here also but let's keep it simple for now.
            PlayerAction action = m_input.getInput(m_playerId, m_context, options);
            if (action == PlayerAction.Undecided) {
                //Try to help the player and give another go
                //This could be also implemented as a selectorNode with multiple children..
                giveAdvice(m_playerId, m_context);
                action = m_input.getInput(m_playerId, m_context, options);
                //Can't wait any longer..
                if (action == PlayerAction.Undecided) {
                    action = PlayerAction.Stay;
                }
            }

            m_context.setVariable(GameContext.KEY_PLAYER_IN_TURN_ID, m_playerId); //This should prob be set somewhere else but let's make sure
            m_context.setVariable(GameContext.KEY_PLAYER_ACTION, action);
            if (action == PlayerAction.QuitGame) {
                //Should this action handler be here or somewhere else?
                BlackJack.quitGame();
                return Types.Status.Failure;
            }
            return Types.Status.Success;
        }

        private void giveAdvice(int playerId, GameContext context) {
            Hand playerHand = (Hand)context.getVariable(GameContext.playerHandKey(playerId));
            Hand dealerHand = (Hand)context.getVariable(GameContext.KEY_DEALER_HAND);

            Simulator.Statistics hit = Simulator.simulateHit(playerHand, dealerHand, m_deck);
            Simulator.Statistics stay = Simulator.simulateStay(playerHand, dealerHand, m_deck);
            for (GameListener l: m_listeners) {
                l.giveAdvice(playerId, hit, stay, playerHand, context);
            }
        }
    }


    private class DetermineWinnerNode extends LeafNode {
        @Override
        public Types.Status tick(ExecutionContext context) {
            GameResult gameResults = (GameResult)context.getVariable(GameContext.KEY_RESULTS);

            Hand dealerHand = (Hand)m_context.getVariable(GameContext.KEY_DEALER_HAND);
            int dealerTicks = dealerHand.getBestPipCount();

            notifyListeners("Calculating results, dealer hand is " + dealerTicks);
            int playerCount = (int)m_context.getVariable(GameContext.KEY_PLAYER_COUNT);
            for (int n = 0; n < playerCount; n++) {
                String key= GameContext.playerHandKey(n);
                Hand hand = (Hand)m_context.getVariable(key);

                GameResult.Result result = Hand.compareHands(hand, dealerHand);
                notifyListeners("Player " + n + " result is " + result + " with hand " + hand.getBestPipCount());
                gameResults.setResult(key, result);
            }

            for (GameListener l: m_listeners) {
                l.gameEnded(gameResults, m_context);
            }

            return Types.Status.Success;
        }
    }
}
