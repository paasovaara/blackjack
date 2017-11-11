package blackjack.engine;

import blackjack.models.Deck;
import blackjack.models.GameContext;

/**
 * Single game of blackjack. Should this extend CompositeNode?
 */
public class Game {
    InputManager m_input;
    Deck m_deck;
    GameContext m_context;

    private static final String KEY_DECK = "deck";

    public Game(InputManager input, int howManyDecks) {
        m_input = input;
        m_deck = new Deck(howManyDecks);
        m_deck.shuffle();

        m_context = new GameContext();
        m_context.setVariable(KEY_DECK, m_deck);
    }

    /**
     * Context contains the state of this whole game
     */
    public GameContext getContext() {
        return m_context;
    }
}
