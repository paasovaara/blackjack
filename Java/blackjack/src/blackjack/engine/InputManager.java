package blackjack.engine;

import blackjack.models.Bet;
import blackjack.models.PlayerAction;

import java.util.List;
import java.util.Set;

public interface InputManager {
    List<Bet> getBets();

    /**
     * @param playerId index from 0:getPlayerCount()
     * @param gameState
     * @return
     */
    //PlayerAction getInput(int playerId, GameContext gameState);

    PlayerAction getInput(int playerId, GameContext gameState, Set<PlayerAction> options);
}
