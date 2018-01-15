package blackjack.models;

public class GameSettings {
    public int deckCount = 1;
    public float shuffleDeckRatio = 0.5f;
    public int delayAfterResultsMs = 3000;
    public int dealerActionLengthMs = 2000;
    public boolean useDefaultBet = true;
    public boolean clearScoreAtStartup = true;

    public String AIModelFile = "model-polynomial-5.csv";
    public boolean usePolynomialModel = true;
    public int polynomialModelDegree = 5;

    public static final GameSettings DEFAULT = new GameSettings();
    public static final GameSettings AI_DEFAULT;
    static {
        AI_DEFAULT = new GameSettings();
        AI_DEFAULT.delayAfterResultsMs = 0;
        AI_DEFAULT.dealerActionLengthMs = 0;
        AI_DEFAULT.useDefaultBet = true;
        AI_DEFAULT.clearScoreAtStartup = true;
    }
}
