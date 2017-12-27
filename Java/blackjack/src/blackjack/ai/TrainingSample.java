package blackjack.ai;

public class TrainingSample {
    public int bestPips;
    public int minPips;
    public int dealerPips;
    public int correctAction;

    @Override
    public String toString() {
        return Integer.toString(bestPips) + "," +
                Integer.toString(dealerPips) + "," +
                Integer.toString(minPips) + "," +
                Integer.toString(correctAction);
    }
}
