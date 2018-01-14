package blackjack.io.sensors;

import blackjack.engine.GameContext;
import blackjack.engine.InputManager;
import blackjack.io.views.UnityOutput;
import blackjack.models.Bet;
import blackjack.models.PlayerAction;
import blackjack.utils.UDPServer;

import java.util.*;

public class SensorInput implements InputManager  {
    UDPServer m_server;
    PlayerInputListener m_player1 = new PlayerInputListener(0); // Or store in a map?!
    PlayerInputListener m_player2 = new PlayerInputListener(1);
    //PlayerBetListener m_betListener = new PlayerBetListener();
    BetManager m_betManager = new BetManager();

    public static final long TIMEOUT_READ_BETS_MS = 10000;
    public static final long SHORT_TIMEOUT_READ_INPUT_MS = 6000;
    public static final long LONG_TIMEOUT_READ_INPUT_MS = 12000;

    class PlayerInputListener extends SensorListener {
        PlayerInputListener(int playerId) {
            super("^((stay|hit)\\{" + playerId + "\\})");
        }
    }

    public void initialize(int port) throws Exception {
        m_server = new UDPServer();
        m_server.initialize(port);
        m_server.addListener(m_player1);
        m_server.addListener(m_player2);
        //m_server.addListener(m_betListener);
        m_betManager.initialize(m_server);

        //DEBUG
        /*
        m_server.addListener(new UDPServer.PacketListener() {
            @Override
            public void packetArrived(String payload) {
                System.out.println("DEBUG: " + payload);
            }
        });*/

        m_server.startServer();
    }
    //MAJOR HACK
    UnityOutput m_unity;
    public void setUnityOutput(UnityOutput unity) {
        m_unity = unity;
    }
    public BetManager getBetManager() {
        return m_betManager;
    }

    @Override
    public List<Bet> getBets() {
        //Read using RFID
        m_betManager.clearTags();
        if (m_unity != null) {
            m_unity.setUpdateBets(true);
        }
        List<Bet> bets = m_betManager.readBets(TIMEOUT_READ_BETS_MS);
        if (m_unity != null) {
            m_unity.setUpdateBets(false);
        }
        return bets;

    }

    @Override
    public PlayerAction getInput(int playerId, GameContext gameState, Set<PlayerAction> options, boolean longTimeout) {
        System.out.println("Starting to wait for sensor input");
        SensorListener listener = null;
        if (playerId == 0) {
            listener = m_player1;
        }
        else if (playerId == 1) {
            listener = m_player2;
        }
        if (listener == null)
            throw new RuntimeException("Player id invalid for SensorInput");

        long timeout = longTimeout ? LONG_TIMEOUT_READ_INPUT_MS : SHORT_TIMEOUT_READ_INPUT_MS;
        //long timeout = SHORT_TIMEOUT_READ_INPUT_MS;
        String msg = listener.blockUntilMessage(timeout);
        System.out.println("Message waited, result: " + msg);
        if (msg == null) {
            System.out.println("Timed out, no input.");
            return PlayerAction.Undecided;
        }
        else if (msg.startsWith("stay")) {
            return PlayerAction.Stay;
        }
        else if (msg.startsWith("hit")) {
            return PlayerAction.Hit;
        }
        else {
            return PlayerAction.QuitGame;
        }
    }

}
