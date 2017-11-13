package blackjack.models;

public enum DealerAction {
    RevealDealerCard, ChangeTurn, DealCard, Shuffle, ClearTable;

    public String toString() {
        switch (this) {
            case RevealDealerCard: return "Reveal";
            case ChangeTurn: return "Turn";
            case DealCard: return "Deal";
            case Shuffle: return "Shuffle";
            case ClearTable: return "Clear";
            default: return "";
        }
    }
}
