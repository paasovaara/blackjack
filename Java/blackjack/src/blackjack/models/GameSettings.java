package blackjack.models;

public class GameSettings {
    public int deckCount = 1; // Less decks => benefits the player. Note that for optimal decisions AI needs to be trained separately for each deck amount.
    public float shuffleDeckRatio = 0.5f;
    public int delayAfterResultsMs = 3000;
    public int dealerActionLengthMs = 2000;
    public boolean useDefaultBet = true; // Doesn't ask the bet in every round.
    public boolean clearScoreAtStartup = true;

    public boolean playOnlyAgainstItself = false; // Start simulation by having AI to play only against itself (no human players)

    public String AIModelFile = "model-polynomial-all-feats-5.csv";
    public boolean usePolynomialModel = true;
    public int polynomialModelDegree = 5;
    public boolean includeMinPips = true;

    public static final GameSettings DEFAULT = new GameSettings();
    public static final GameSettings AI_DEFAULT;
    public static final GameSettings AI_SIMULATION_ONLY;
    static {
        AI_DEFAULT = new GameSettings();
        AI_DEFAULT.delayAfterResultsMs = 0;
        AI_DEFAULT.dealerActionLengthMs = 0;

        AI_SIMULATION_ONLY = new GameSettings();
        AI_SIMULATION_ONLY.delayAfterResultsMs = 0;
        AI_SIMULATION_ONLY.dealerActionLengthMs = 0;
        AI_SIMULATION_ONLY.playOnlyAgainstItself = true;

    }
}
