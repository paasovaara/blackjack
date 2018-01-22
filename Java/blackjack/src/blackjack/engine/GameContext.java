package blackjack.engine;

import behave.execution.ExecutionContext;
import blackjack.models.Bet;

import java.util.LinkedList;
import java.util.List;

public class GameContext extends ExecutionContext {
    public static final String KEY_DECK = "deck";
    public static final String KEY_CARD_COUNTER = "cardcounter";
    public static final String KEY_TIMES_WAITED = "waited";
    public static final String KEY_PLAYER_IDS_IN_GAME = "playerids";
    public static final String KEY_PLAYER_COUNT = "playercount";
    public static final String KEY_PLAYER_BET_PREFIX = "playerbet";
    public static final String KEY_DEALER_HAND = "dealerhand";
    public static final String KEY_PLAYER_HAND_PREFIX = "playerhand_";
    public static final String KEY_PLAYER_IN_TURN_ID = "playerturn";
    public static final String KEY_PLAYER_ACTION = "playeraction";
    public static final String KEY_RESULTS = "results";

    public static final int DEALER_PLAYER_ID = -1;

    public static String playerHandKey(int id) {
        if (id == DEALER_PLAYER_ID) {
            return KEY_DEALER_HAND;
        }
        return KEY_PLAYER_HAND_PREFIX + id;
    }

    public static String playerBetKey(int id) {
        return KEY_PLAYER_BET_PREFIX + id;
    }

    public List<Integer> getPlayers() {
        return (List<Integer>)getVariable(KEY_PLAYER_IDS_IN_GAME);
    }

    public void setPlayers(List<Bet> bets) {
        LinkedList<Integer> players = new LinkedList<>();
        for(Bet bet: bets) {
            String playerBetKey = GameContext.playerBetKey(bet.playerId);
            setVariable(playerBetKey, bet.betAmount);
            players.add(bet.playerId);
        }
        setVariable(KEY_PLAYER_IDS_IN_GAME, players);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("\n== CONTEXT =====================\n");
        for(String key: keySet()) {
            Object o = getVariable(key);
            if (o != null) {
                buf.append(key + "=" + o.toString() + "\n");
            }
        }
        buf.append("== ENDOFCONTEXT =====================\n");
        return buf.toString();
    }
}
