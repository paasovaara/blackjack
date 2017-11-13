package blackjack.models;

public enum DealerAction {
    ChangeTurn, DealCard, Shuffle, ClearTable;

    public String toString() {
        switch (this) {
            case ChangeTurn: return "Turn";
            case DealCard: return "Deal";
            case Shuffle: return "Shuffle";
            case ClearTable: return "Clear";
            default: return "";
        }
    }
}
