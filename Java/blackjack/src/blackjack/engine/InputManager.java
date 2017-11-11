package blackjack.engine;

import blackjack.models.PlayerAction;

import java.util.Set;

public interface InputManager {
    int getPlayerCount();

    /**
     * @param playerId index from 0:getPlayerCount()
     * @param gameState
     * @return
     */
    //PlayerAction getInput(int playerId, GameContext gameState);

    PlayerAction getInput(int playerId, GameContext gameState, Set<PlayerAction> options);
}
