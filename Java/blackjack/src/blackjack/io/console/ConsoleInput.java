package blackjack.io.console;

import blackjack.engine.GameContext;
import blackjack.engine.InputManager;
import blackjack.models.Bet;
import blackjack.models.GameSettings;
import blackjack.models.Hand;
import blackjack.models.PlayerAction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ConsoleInput implements InputManager {
    BufferedReader m_in;
    private boolean m_useDefaultBets;

    public ConsoleInput(GameSettings settings) {
        m_in = new BufferedReader(new InputStreamReader(System.in));
        m_useDefaultBets = settings.useDefaultBet;
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

    private int getPlayerCount(boolean human) {
        int players = -1;
        while(players < 0) {
            try {
                if (human) {
                    printInput("How many players?");
                }
                else {
                    printInput("How many AI?");
                }
                String in = readInput();
                players = Integer.parseInt(in.trim());
            }
            catch (Exception e) {
                printInfo("Not a valid number, try again");
            }
        }
        return players;
    }

    private int getBet(int playerId) {
        int bet = -1;
        while(bet < 0) {
            try {
                printInput("How much is the bet for player " + playerId + "?");
                String in = readInput();
                bet = Integer.parseInt(in.trim());
            }
            catch (Exception e) {
                printInfo("Not a valid number, try again");
            }
        }
        return bet;
    }

    @Override
    public List<Bet> getBets() {
        int players = getPlayerCount(true);
        LinkedList<Bet> bets = new LinkedList<>();
        for(int n = 0; n < players; n++) {
            int betValue = m_useDefaultBets ? 1 : getBet(n);
            if (betValue > 0) {
                Bet bet = new Bet(n, betValue);
                bets.add(bet);
            }
        }

        return bets;
    }

    @Override
    public PlayerAction getInput(int playerId, GameContext gameState, Set<PlayerAction> options, boolean longTimeout) {
        while(true) {
            try {
                String print = AsciiArt.printHands(gameState) + "\nChoose (h)it, (s)tay, (q)uit, (a)dvice or (d)ebug:";
                printInput(print);
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
                else if (input.equals("a")) {
                    return PlayerAction.Undecided;
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
