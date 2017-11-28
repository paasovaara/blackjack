package blackjack.models;

import blackjack.engine.BlackJack;
import blackjack.engine.GameContext;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameResult {

    public enum Result {
        Busted,
        Lost,
        Push,
        Won,
        Blackjack;

        @Override
        public String toString() {
            switch (this) {
                case Busted: return "Busted";
                case Lost: return "Lost";
                case Push: return "Push";
                case Won: return "Won";
                case Blackjack: return "Blackjack";
                default: return "";
            }
        }
    } //add BlackJack in case we want to give out extra money on BlackJack

    private Map<String, Result> m_results = new HashMap<>();
    private List<Bet> m_bets = new LinkedList<>();

    private int m_totalWinnings = 0;
    public GameResult(int totalWinnings) {
        m_totalWinnings = totalWinnings;
    }

    public int getTotalWinnings() {
        return m_totalWinnings;
    }
    /**
     * Key is the player hand id. TODO use playerId?
     * @return
     */
    public Map<String, Result> getResults() {
        return m_results;
    }

    public void setResult(int playerId, Result result) {
        String playerKey = GameContext.playerHandKey(playerId);
        m_results.put(playerKey, result);
        if (result == Result.Blackjack || result == Result.Won) {
            int bet = findBetAmount(playerId);
            m_totalWinnings += bet;
            BlackJack.saveTotalWinnings(m_totalWinnings);
        }
    }

    public int findBetAmount(int playerId) {
        if (m_bets != null) {
            for (Bet bet: m_bets) {
                if (bet.playerId == playerId) {
                    return bet.betAmount;
                }
            }
        }
        return 0;
    }

    public void setBets(List<Bet> bets) {
        m_bets = bets;
    }

    public List<Bet> getBets() {
        return m_bets;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("\n.. Results ...................\n");
        for(String key: m_results.keySet()) {
            Result result = m_results.get(key);
            buf.append(key + " => " + result.toString() + "\n");
        }
        buf.append("............................\n");

        return buf.toString();
    }
}
