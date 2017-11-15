package blackjack.io;

import blackjack.engine.GameContext;
import blackjack.engine.InputManager;
import blackjack.models.PlayerAction;
import blackjack.utils.UDPServer;

import java.util.Set;

public class SensorInput implements InputManager  {
    UDPServer m_server;
    PlayerInputListener m_player1 = new PlayerInputListener(0); // Or store in a map?!
    PlayerInputListener m_player2 = new PlayerInputListener(1);

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
        m_server.startServer();
    }

    @Override
    public int getPlayerCount() {
        //TODO read from RFID, block until get one.
        return 2;
    }

    @Override
    public PlayerAction getInput(int playerId, GameContext gameState, Set<PlayerAction> options) {
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

        String msg = listener.blockUntilMessage(30000);
        System.out.println("Message waited, result: " + msg);
        if (msg == null) {
            System.out.println("No input so assuming STAY ");
            return PlayerAction.Stay;
        }
        //TODO match playerId
        //TODO regex groups
        //TODO block while timeout elapsed. or then again have the playerId in the SensorInput?
        if (msg.startsWith("stay")) {
            return PlayerAction.Stay;
        }
        else if (msg.startsWith("hit")) {
            return PlayerAction.Hit;
        }
        else {
            return PlayerAction.QuitGame;
        }
    }
    /*
    abstract class ReadUDPUntilTimeout<T> extends Thread {
        T result;

        abstract T readInput();
    }*/




}
