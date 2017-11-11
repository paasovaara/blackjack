package blackjack.engine;

import behave.execution.ExecutionContext;

import java.util.LinkedList;

public class GameContext extends ExecutionContext {
    public static final String KEY_DECK = "deck";
    public static final String KEY_PLAYER_COUNT = "playercount";

    final static LinkedList<String> KEYSET = new LinkedList<>();
    static {
        KEYSET.add(KEY_DECK);
        KEYSET.add(KEY_PLAYER_COUNT);
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
