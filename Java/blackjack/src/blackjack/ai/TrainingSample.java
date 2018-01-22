package blackjack.ai;

public class TrainingSample extends Sample {
    public int correctAction;

    public TrainingSample(){
        super(0, 0, 0, 0);
    };

    public TrainingSample(int best, int min, int dealer, int deck, int correct) {
        super(best, min, dealer, deck);
        correctAction = correct;
    }

    @Override
    public String toString() {
        return Integer.toString(bestPips) + "," +
                Integer.toString(dealerPips) + "," +
                Integer.toString(minPips) + "," +
                Integer.toString(deckWeight) + "," +
                Integer.toString(correctAction);
    }
}
