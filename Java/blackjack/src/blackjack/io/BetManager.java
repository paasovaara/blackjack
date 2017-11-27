package blackjack.io;

import blackjack.models.Bet;
import blackjack.utils.ConfigUtils;
import blackjack.utils.UDPServer;

import java.util.*;

public class BetManager {

    public interface BetChangeListener {
        void betChanged(int playerId, int newBet);
    }

    private final LinkedList<BetChangeListener> m_listeners = new LinkedList<>();

    private static Properties s_betPerTagLUT;
    static {
        try {
            s_betPerTagLUT = ConfigUtils.readPropertiesFile("bets.properties");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    class BetListener extends SensorListener implements Runnable {
        private final Set<String> m_tags = new HashSet<>();
        private boolean m_running = false;
        final int READ_TIMEOUT_MS = 2000;
        private int m_playerId;
        BetListener(int playerId) {
            super("^(rfid" + (playerId+1) +")");
            //super("^(rfid)[0-9]-(.*)");
            m_playerId = playerId;
        }

        public void run() {
            if (m_running)
                return;

            m_running = true;
            while(m_running) {
                String tag = blockUntilMessage(READ_TIMEOUT_MS);

                //If not a single message within timeout we consider tag to be removed
                synchronized (m_tags) {
                    if (tag == null) {
                        if (m_tags.size() > 0) {
                            m_tags.clear();
                            notifyChange(0);
                        }
                    }
                    else {
                        if (!m_tags.contains(tag)) {
                            m_tags.add(tag);
                            Bet bet = parseBet(m_playerId, m_tags);
                            notifyChange(bet.betAmount);
                        }
                    }
                }
            }
            m_running = false;
        }

        private void notifyChange(int newBet) {
            System.out.println("bet changed for player " + m_playerId + " = " + newBet);
            synchronized (m_listeners) {
                for (BetChangeListener l: m_listeners) {
                    l.betChanged(m_playerId, newBet);
                }
            }
        }

        public void stop() {
            //Will exit within READ_TIMEOUT_MS. we could also use a mutex and notify
            m_running = false;
        }

        public Set<String> getTags() {
            synchronized (m_tags) {
                return new HashSet<>(m_tags); //New just in case to prevent concurrent modification. we could (and should?) use a mutex also.
            }
        }
    }

    BetListener m_bets1 = new BetListener(0);
    BetListener m_bets2 = new BetListener(1);

    public void addListener(BetChangeListener l) {
        synchronized (m_listeners) {
            m_listeners.add(l);
        }
    }

    public void initialize(UDPServer server) {
        server.addListener(m_bets1);
        server.addListener(m_bets2);
        Thread t = new Thread(m_bets1);
        t.start();
        Thread t2 = new Thread(m_bets2);
        t2.start();

    }

    public void close() {
        m_bets1.stop();
        m_bets2.stop();
    }

    public List<Bet> readBets(final long timeout) {
        System.out.println("Starting to read bets with timeout " + timeout);

        //Block until timeout or both players have placed their bets.
        //Not beautiful but works I hope: TODO refactor
        final long start = System.currentTimeMillis();

        List<Bet> bets = new LinkedList<>();
        final long endTime = System.currentTimeMillis() + timeout;
        while(System.currentTimeMillis() <= endTime) {
            //long timeLeft = endTime - System.currentTimeMillis();
            bets = parseBets(m_bets1.getTags(), m_bets2.getTags());

            if (bets.size() >= 2) {
                System.out.println("Both players have placed their bet");

                break;
            }
            try {
                Thread.currentThread().sleep(1000);
            }
            catch (InterruptedException e) {}
            /*
            if (System.currentTimeMillis() >= (start + timeout)) {
                System.out.println("Bet reading timed out, betcount: " + bets.size());
                break;
            }*/
        }
        return bets;
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
            String value = s_betPerTagLUT.getProperty(tag, "1");
            int bet = Integer.parseInt(value.trim());
            sum += bet;
        }
        return new Bet(playerId, sum);
    }
}
