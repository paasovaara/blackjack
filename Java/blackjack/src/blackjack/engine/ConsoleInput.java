package blackjack.engine;

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

    private void print(String msg) {
        System.out.println(msg);
    }

    @Override
    public int getPlayerCount() {
        int players = -1;
        while(players <= 0) {
            try {
                print("How many players?");
                String in = readInput();
                players = Integer.parseInt(in.trim());
            }
            catch (Exception e) {
                print("Not a valid number, try again");
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
                print("Player[" + playerId + "]: Your hand is " + hand.toString() + "\nChoose (h)it, (s)tay, (q)uit or (d)ebug: ");
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
                    print(gameState.toString());
                }
                else {
                    print("Not a valid option");
                }
            }
            catch (Exception e) {
                print("Really bad option");
            }
        }
    }
}
