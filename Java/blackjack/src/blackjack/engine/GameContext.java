package blackjack.engine;

import behave.execution.ExecutionContext;

public class GameContext extends ExecutionContext {
    public static final String KEY_DECK = "deck";
    public static final String KEY_PLAYER_COUNT = "playercount";
    public static final String KEY_PLAYER_BET_PREFIX = "playerbet";
    public static final String KEY_DEALER_HAND = "dealerhand";
    public static final String KEY_PLAYER_HAND_PREFIX = "playerhand_";
    public static final String KEY_PLAYER_IN_TURN_ID = "playerturn";
    public static final String KEY_PLAYER_ACTION = "playeraction";

    //public static final String KEY_PLAYER1_HAND = "playerhand_0";
    //public static final String KEY_PLAYER2_HAND = "playerhand_1";
    public static final String KEY_RESULTS = "results";

    public static final int DEALER_PLAYER_ID = -1;

    public static String playerHandKey(int id) {
        return KEY_PLAYER_HAND_PREFIX + id;
    }

    public static String playerBetKey(int id) {
        return KEY_PLAYER_BET_PREFIX + id;
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
