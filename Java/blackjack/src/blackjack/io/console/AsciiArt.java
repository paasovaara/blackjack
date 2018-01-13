package blackjack.io.console;

import blackjack.engine.GameContext;
import blackjack.models.Hand;

import java.util.List;

public class AsciiArt {
    private AsciiArt() {}

    private static boolean isDealerTurn(Integer curPlayer) {
        return curPlayer != null && curPlayer == -1;
    }

    private static boolean isPlayerTurn(Integer curPlayer) {
        return curPlayer != null && curPlayer != -1;
    }

    public static String printHands(GameContext gameState) {
        StringBuffer buf = new StringBuffer();

        Integer curPlayer = (Integer) gameState.getVariable(GameContext.KEY_PLAYER_IN_TURN_ID);
        buf.append("=========================================================\n");
        Hand dealerHand =(Hand)gameState.getVariable(GameContext.KEY_DEALER_HAND);
        buf.append("\t\t\t\t");
        buf.append(Integer.toString(dealerHand.getBestPipCount()));
        if (isDealerTurn(curPlayer)) {
            buf.append("\t\t\t\t^\n");
        }
        else {
            buf.append("\n");
        }
        buf.append("\n");
        List<Integer> players = gameState.getPlayers();
        for(int id: players) {
            String key = GameContext.playerHandKey(id);
            Hand hand = (Hand)gameState.getVariable(key);
            buf.append("\t");
            buf.append(Integer.toString(hand.getBestPipCount()) + "[" + hand.getMinPipCount() + "]");
        }
        buf.append("\n");
        if (isPlayerTurn(curPlayer)) {
            buf.append("\t");
            for(int n = 0; n < curPlayer; n++) {
                buf.append("\t\t");
            }
            buf.append("^\n");
        }
        buf.append("=========================================================");
        return buf.toString();
    }


}
