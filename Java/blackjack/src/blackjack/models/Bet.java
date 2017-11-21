package blackjack.models;

public class Bet {
    public Bet(int id, int bet) {
        playerId = id;
        betAmount = bet;
    }
    //Let's use public members since this is just a POJO
    public int playerId;
    public int betAmount;

    @Override
    public String toString() {
        return "Player " + playerId + " bets " + betAmount;
    }
}
