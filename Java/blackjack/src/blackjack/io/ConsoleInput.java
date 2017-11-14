package blackjack.io;

import blackjack.engine.GameContext;
import blackjack.engine.InputManager;
import blackjack.models.GameResult;
import blackjack.models.Hand;
import blackjack.models.PlayerAction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

public class ConsoleInput implements InputManager {
    BufferedReader m_in;

    public ConsoleInput() {
        m_in = new BufferedReader(new InputStreamReader(System.in));
    }

    private String readInput() throws IOException {
        return m_in.readLine();
    }

    private void printInfo(String msg) {
        System.out.println(msg);
    }

    private void printInput(String msg) {
        System.out.println(msg);
        System.out.print(">");
    }

    @Override
    public int getPlayerCount() {
        int players = -1;
        while(players <= 0) {
            try {
                printInput("How many players?");
                String in = readInput();
                players = Integer.parseInt(in.trim());
            }
            catch (Exception e) {
                printInfo("Not a valid number, try again");
            }
        }
        return players;
    }

    @Override
    public PlayerAction getInput(int playerId, GameContext gameState, Set<PlayerAction> options) {
        while(true) {
            try {
                String key = GameContext.playerHandKey(playerId);
                Hand hand = (Hand)gameState.getVariable(key);
                Hand dealerHand =(Hand)gameState.getVariable(GameContext.KEY_DEALER_HAND);
                printInput("Player[" + playerId + "]: Your hand is " + hand.toString() + " while Dealer has " +dealerHand.toString()+"\nChoose (h)it, (s)tay, (q)uit or (d)ebug:");
                //Now hardcoding the options, TODO read from options-set
                String input = readInput().trim().toLowerCase();
                if (input.equals("h")) {
                    return PlayerAction.Hit;
                }
                else if (input.equals("s")) {
                    return PlayerAction.Stay;
                }
                else if (input.equals("q")) {
                    return PlayerAction.QuitGame;
                }
                else if (input.equals("d")) {
                    printInfo(gameState.toString());
                }
                else {
                    printInfo("Not a valid option");
                }
            }
            catch (Exception e) {
                printInfo("Really bad option");
            }
        }
    }
}
