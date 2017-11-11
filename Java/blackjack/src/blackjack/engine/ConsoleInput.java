package blackjack.engine;

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
        print("Choose option player " + playerId + ": ");
        for(PlayerAction option: options) {
            print(option.toString());
        }

        return null;
    }
}
