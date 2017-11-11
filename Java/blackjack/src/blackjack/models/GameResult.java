package blackjack.models;

import java.util.HashMap;
import java.util.Map;

public class GameResult {

    public enum Result {
        Lost,
        Tied,
        Won;

        @Override
        public String toString() {
            switch (this) {
                case Lost: return "Lost";
                case Tied: return "Tied";
                case Won: return "Won";
                default: return "";
            }
        }
    } //add BlackJack in case we want to give out extra money on BlackJack

    private Map<String, Result> m_results = new HashMap<>();

    public Map<String, Result> getResults() {
        return m_results;
    }

    public void setResult(String playerKey, Result result) {
        m_results.put(playerKey, result);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("== Results =====================\n");
        for(String key: m_results.keySet()) {
            Result result = m_results.get(key);
            buf.append(key + " => " + result.toString() + "\n");
        }
        buf.append("================================\n");

        return buf.toString();
    }
}
