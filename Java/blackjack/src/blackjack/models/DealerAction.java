package blackjack.models;

public enum DealerAction {
    RevealDealerCard, ChangeTurn, DealCard, Shuffle, StartGame, ReturnWinnings;

    public String toString() {
        switch (this) {
            case RevealDealerCard: return "Reveal";
            case ChangeTurn: return "Turn";
            case DealCard: return "Deal";
            case Shuffle: return "Shuffle";
            case StartGame: return "Start";
            case ReturnWinnings: return "Return Winnings";

            default: return "";
        }
    }
}
