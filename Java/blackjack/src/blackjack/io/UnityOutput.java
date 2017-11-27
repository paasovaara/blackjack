package blackjack.io;

import blackjack.engine.GameContext;
import blackjack.engine.GameListener;
import blackjack.models.*;
import blackjack.utils.Config;
import blackjack.utils.EventSender;

import java.util.List;
import java.util.Map;

//public class UnityOutput implements GameListener {
public class UnityOutput extends ConsoleOutput implements BetManager.BetChangeListener {
    Config m_config;
    EventSender m_sender = new EventSender();

    private boolean m_updateBets = true;
    public void setUpdateBets(boolean update) {
        m_updateBets = update;
    }

    public void init(Config config) {
        m_config = config;
        try {
            m_sender.initialize(config.host, config.port);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void gameStarted(List<Bet> playerBets, GameContext context) {
        super.gameStarted(playerBets, context);
        //A bit ugly but works
        int player1Bet = 0;
        int player2Bet = 0;
        for (Bet bet: playerBets) {
            if (bet.playerId == 0) {
                player1Bet = bet.betAmount;
            }
            else if (bet.playerId == 1) {
                player2Bet = bet.betAmount;
            }
        }

        String msg = "start{<bet1>}{<bet2>}";
        msg = msg.replaceAll("<bet1>", Integer.toString(player1Bet))
                .replaceAll("<bet2>", Integer.toString(player2Bet));

        m_sender.sendMessage(msg.getBytes());
    }

    @Override
    public void tellInstructions() {
        super.tellInstructions();
        String msg = "instructions";
        m_sender.sendMessage(msg.getBytes());
    }

    @Override
    public void dealCard(int playerId, Card card, Hand hand, GameContext context) {
        super.dealCard(playerId, card, hand, context);
        String msg = "deal{<p>}{<s>}{<r>}";
        msg = msg.replaceAll("<p>", Integer.toString(playerId))
                .replaceAll("<s>", card.getSuite().toString())
                .replaceAll("<r>", card.isHidden()? "h" : Integer.toString(card.getRank().getId()));

        m_sender.sendMessage(msg.getBytes());
    }

    @Override
    public void hitMe(int playerId, Card card, Hand hand, GameContext context) {
        super.hitMe(playerId, card, hand, context);
        String msg = "hitMe{<p>}{<s>}{<r>}";
        msg = msg.replaceAll("<p>", Integer.toString(playerId))
                .replaceAll("<s>", card.getSuite().toString())
                .replaceAll("<r>", card.isHidden()? "h" : Integer.toString(card.getRank().getId()));

        m_sender.sendMessage(msg.getBytes());
    }

    @Override
    public void shuffle(Deck deck) {
        super.shuffle(deck);
        String msg = "shuffle";
        m_sender.sendMessage(msg.getBytes());
    }

    @Override
    public void turnChanged(int playerId, GameContext context) {
        String msg = "turnChange{<p>}";
        msg = msg.replaceAll("<p>", Integer.toString(playerId));

        m_sender.sendMessage(msg.getBytes());
    }

    @Override
    public void revealDealerCard(Card card, Hand hand, GameContext context) {
        super.revealDealerCard(card, hand, context);
        String msg = "reveal{<s>}{<r>}";
        msg = msg.replaceAll("<s>", card.getSuite().toString())
                .replaceAll("<r>", Integer.toString(card.getRank().getId()));

        m_sender.sendMessage(msg.getBytes());
    }

    @Override
    public void busted(int playerId, Hand hand, GameContext context) {
        super.busted(playerId, hand, context);
        String msg = "busted{<p>}";
        msg = msg.replaceAll("<p>", Integer.toString(playerId));

        m_sender.sendMessage(msg.getBytes());
    }

    @Override
    public void blackjack(int playerId, GameContext context) {
        String msg = "blackjack{<p>}";
        msg = msg.replaceAll("<p>", Integer.toString(playerId));
        m_sender.sendMessage(msg.getBytes());
    }

    @Override
    public void gameEnded(GameResult results, GameContext context) {
        super.gameEnded(results, context);

        final String msgTemplate = "result{<p>}{<r>}{<b>}";
        Map<String, GameResult.Result> resultMap = results.getResults();
        List<Integer> players = context.getPlayers();

        for (Integer id: players) {
            String key = GameContext.playerHandKey(id);
            GameResult.Result res = resultMap.get(key);
            Integer bet = (Integer)context.getVariable(GameContext.playerBetKey(id));
            if (res != null && bet != null) {
                String msg = msgTemplate.replaceAll("<p>", Integer.toString(id))
                        .replaceAll("<r>", res.toString())
                        .replaceAll("<b>", Integer.toString(bet));
                m_sender.sendMessage(msg.getBytes());
            }
            else {
                System.out.println("Error, cannot find result for player " + id);
            }
        }
        String msg = msgTemplate.replaceAll("<p>", "-1")
                .replaceAll("<r>","None").replaceAll("<b>", "0");
        m_sender.sendMessage(msg.getBytes());
    }

    @Override
    public void betChanged(int playerId, int newBet) {
        if (m_updateBets) {
            String msg = "bet{<p>}{<b>}";
            msg = msg.replaceAll("<p>", Integer.toString(playerId))
                    .replaceAll("<b>", Integer.toString(newBet));

            m_sender.sendMessage(msg.getBytes());

        }
    }
}
