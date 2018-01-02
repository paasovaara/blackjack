package blackjack.ai;

public class TrainingSample {
    public int bestPips;
    public int minPips;
    public int dealerPips;
    public int correctAction;

    public TrainingSample(){};

    public TrainingSample(int best, int min, int dealer, int correct) {
        bestPips = best;
        minPips = min;
        dealerPips = dealer;
        correctAction = correct;
    }

    @Override
    public String toString() {
        return Integer.toString(bestPips) + "," +
                Integer.toString(dealerPips) + "," +
                Integer.toString(minPips) + "," +
                Integer.toString(correctAction);
    }
}
