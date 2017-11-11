package blackjack.engine;

import blackjack.models.GameContext;
import blackjack.models.Input;

import java.util.Set;

public interface InputManager {
    int getPlayerCount();

    /**
     * @param playerId index from 0:getPlayerCount()
     * @param gameState
     * @return
     */
    Input getInput(int playerId, GameContext gameState);

    Input getInput(int playerId, GameContext gameState, Set<Input> options);
}
