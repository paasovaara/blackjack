package blackjack.engine;

import behave.execution.ExecutionContext;

public class GameContext extends ExecutionContext {
    public static final String KEY_DECK = "deck";
    public static final String KEY_PLAYER_COUNT = "playercount";
    public static final String KEY_DEALER_HAND = "dealerhand";
    public static final String KEY_PLAYER_HAND_PREFIX = "playerhand_";
    public static final String KEY_PLAYER1_HAND = "playerhand_0";
    public static final String KEY_PLAYER2_HAND = "playerhand_1";

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
