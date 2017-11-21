package blackjack.io;

import blackjack.models.Bet;
import blackjack.utils.ConfigUtils;

import java.util.*;

public class PlayerBetListener extends SensorListener {

    private static final String PLAYER_1_BET_PREFIX = "rfid1-";
    private static final String PLAYER_2_BET_PREFIX = "rfid2-";

    private static Properties s_betPerTagLUT;
    static {
        try {
            s_betPerTagLUT = ConfigUtils.readPropertiesFile("bets.properties");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    PlayerBetListener() {
        super("^(rfid)");
        //super("^(rfid)[0-9]-(.*)");
    }

    public List<Bet> readBets(long totalTimeoutMs) {
        System.out.println("Starting to read bets with timeout " + totalTimeoutMs);
        Set<String> player1Tags = new HashSet<>();
        Set<String> player2Tags = new HashSet<>();

        final long endTime = System.currentTimeMillis() + totalTimeoutMs;
        while(System.currentTimeMillis() <= endTime) {
            long timeLeft = endTime - System.currentTimeMillis();
            String msg = blockUntilMessage(timeLeft);
            if (msg == null) {
                //No bets.
            }
            else if (msg.startsWith(PLAYER_1_BET_PREFIX)) {
                String tag = msg.replace(PLAYER_1_BET_PREFIX, ""); // We could parse this with regex also..
                player1Tags.add(tag);
            }
            else if (msg.startsWith(PLAYER_2_BET_PREFIX)) {
                String tag = msg.replace(PLAYER_2_BET_PREFIX, ""); // We could parse this with regex also..
                player2Tags.add(tag);
            }
            else {
                System.out.println("Unknown rfid message: " + msg);
            }

            if (!player1Tags.isEmpty() && !player2Tags.isEmpty()) {
                //No need to wait any longer, both players have placed their bets
                System.out.println("Both players have placed their bet");

                break;
            }
        }
        return parseBets(player1Tags, player2Tags);
    }

    List<Bet> parseBets(Set<String> tags1, Set<String> tags2) {
        LinkedList list = new LinkedList();
        Bet bet0 = parseBet(0, tags1);
        if (bet0.betAmount > 0) {
            list.add(bet0);
        }
        Bet bet1 = parseBet(1, tags2);
        if (bet1.betAmount > 0) {
            list.add(bet1);
        }

        return list;
    }

    Bet parseBet(int playerId, Set<String> tags) {
        int sum = 0;
        for (String tag: tags) {
            String value = s_betPerTagLUT.getProperty(tag, "0");
            int bet = Integer.parseInt(value.trim());
            sum += bet;
        }
        return new Bet(playerId, sum);
    }
}