package blackjack.models;

public enum PlayerAction {
    Hit, Stay, QuitGame;

    public String toString() {
        switch (this) {
            case Hit: return "Hit";
            case Stay: return "Stay";
            case QuitGame: return "Quit";
            default: return "";
        }
    }
}
