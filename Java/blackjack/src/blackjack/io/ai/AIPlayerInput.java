package blackjack.io.ai;

import blackjack.engine.GameContext;
import blackjack.engine.InputManager;
import blackjack.models.Bet;
import blackjack.models.PlayerAction;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class AIPlayerInput implements InputManager {
    Random m_random = new Random();

    @Override
    public List<Bet> getBets() {
        List<Bet> list = new LinkedList<>();
        list.add(new Bet(0, 1));
        return list;
    }

    @Override
    public PlayerAction getInput(int playerId, GameContext gameState, Set<PlayerAction> options, boolean longTimeout) {
        if (m_random.nextBoolean()) {
            return PlayerAction.Hit;
        }
        return PlayerAction.Stay;
    }
}
