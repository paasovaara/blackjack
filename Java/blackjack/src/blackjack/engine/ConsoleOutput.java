package blackjack.engine;

public class ConsoleOutput implements GameListener {
    @Override
    public void showMessage(String msg, GameContext context) {
        System.out.println(msg);
    }
}
