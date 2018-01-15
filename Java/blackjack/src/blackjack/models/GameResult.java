package blackjack.models;

import blackjack.engine.BlackJack;
import blackjack.engine.GameContext;
import blackjack.utils.ConfigUtils;

import java.util.*;

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

    static class AllTimeScore {
        int winnings = 0;
        int losses = 0;
        int games = 0;
    }
    private static AllTimeScore m_allTimeScore = readAllTimeScore();

    public static void resetAllTimeScore() {
        m_allTimeScore = new AllTimeScore();
        saveAllTimeScore(m_allTimeScore);
    }

    public int getTotalWinnings() {
        return m_allTimeScore.winnings;
    }

    /**
     * Key is the player hand id. TODO use playerId?
     * @return
     */
    public Map<String, Result> getResults() {
        return m_results;
    }

    public void gameOver() {
        m_allTimeScore.games++;
        saveAllTimeScore(m_allTimeScore);
    }

    public void setResult(int playerId, Result result) {
        String playerKey = GameContext.playerHandKey(playerId);
        m_results.put(playerKey, result);
        //TODO player specific scores
        int bet = findBetAmount(playerId);
        if (result == Result.Blackjack || result == Result.Won) {
            m_allTimeScore.winnings += bet;
        }
        else if (result == Result.Lost || result == Result.Busted) {
            m_allTimeScore.losses += bet;
        }

        saveAllTimeScore(m_allTimeScore);
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
        buf.append("TOTAL: W[" + m_allTimeScore.winnings + "], L["+ m_allTimeScore.losses +"], G["+m_allTimeScore.games+"]\n");
        buf.append("............................\n");

        return buf.toString();
    }

    static AllTimeScore readAllTimeScore() {
        AllTimeScore score = new AllTimeScore();
        try {
            Properties props = ConfigUtils.readPropertiesFile("alltime.scores");
            score.winnings = Integer.parseInt(props.getProperty("winnings", "0"));
            score.losses = Integer.parseInt(props.getProperty("losses", "0"));
            score.games = Integer.parseInt(props.getProperty("games", "0"));
        }
        catch (Exception e){
            System.out.println("Could not read scores from file");
            e.printStackTrace();
            score = new AllTimeScore();
        }
        return score;
    }

    static void saveAllTimeScore(AllTimeScore score) {
        try {
            Properties props = new Properties();
            props.setProperty("winnings", Integer.toString(score.winnings));
            props.setProperty("losses", Integer.toString(score.losses));
            props.setProperty("games", Integer.toString(score.games));
            ConfigUtils.writePropertiesFile("alltime.scores", props);
        }
        catch (Exception e) {
            System.out.println("Failed to write scores to file");
            e.printStackTrace();
        }
    }
}
