package blackjack.engine;

import behave.execution.ExecutionContext;

import java.util.LinkedList;

public class GameContext extends ExecutionContext {
    public static final String KEY_DECK = "deck";
    public static final String KEY_PLAYER_COUNT = "playercount";
    public static final String KEY_DEALER_HAND = "dealerhand";
    public static final String KEY_PLAYER_HAND_PREFIX = "playerhand_";
    public static final String KEY_PLAYER1_HAND = "playerhand_0";
    public static final String KEY_PLAYER2_HAND = "playerhand_1";

    final static LinkedList<String> KEYSET = new LinkedList<>(); // mainly for debugging
    static {
        KEYSET.add(KEY_DECK);
        KEYSET.add(KEY_PLAYER_COUNT);
        KEYSET.add(KEY_DEALER_HAND);
        KEYSET.add(KEY_PLAYER1_HAND);
        KEYSET.add(KEY_PLAYER2_HAND);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("== CONTEXT ==\n");
        for(String key: KEYSET) {
            Object o = getVariable(key);
            if (o != null) {
                buf.append(key + "=" + o.toString() + "\n");
            }
        }
        buf.append("== ENDOFCONTEXT ==\n");
        return buf.toString();
    }
}
