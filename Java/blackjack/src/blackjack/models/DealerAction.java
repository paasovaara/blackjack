package blackjack.models;

public enum DealerAction {
    DealCard, Shuffle, ClearTable;

    public String toString() {
        switch (this) {
            case DealCard: return "Deal";
            case Shuffle: return "Shuffle";
            case ClearTable: return "Clear";
            default: return "";
        }
    }
}
