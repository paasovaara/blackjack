package blackjack.models;

public enum DealerAction {
    RevealDealerCard, Blackjack, ChangeTurn, DealCard, Shuffle, StartGame, ReturnWinnings;

    public String toString() {
        switch (this) {
            case RevealDealerCard: return "Reveal";
            case Blackjack: return "Blackjack";
            case ChangeTurn: return "Turn";
            case DealCard: return "Deal";
            case Shuffle: return "Shuffle";
            case StartGame: return "Start";
            case ReturnWinnings: return "Return Winnings";

            default: return "";
        }
    }
}
