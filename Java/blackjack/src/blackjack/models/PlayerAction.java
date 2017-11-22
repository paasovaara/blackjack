package blackjack.models;

public enum PlayerAction {
    Hit, Stay, Undecided, QuitGame;

    public String toString() {
        switch (this) {
            case Hit: return "Hit";
            case Stay: return "Stay";
            case QuitGame: return "Quit";
            case Undecided: return "Undecided";
            default: return "";
        }
    }
}
