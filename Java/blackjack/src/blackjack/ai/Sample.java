package blackjack.ai;

public class Sample {
    public int bestPips;
    public int minPips;
    public int dealerPips;
    public int deckWeight;

    public Sample(int best, int min, int dealer, int weight) {
        bestPips = best;
        minPips = min;
        dealerPips = dealer;
        deckWeight = weight;
    }
}
