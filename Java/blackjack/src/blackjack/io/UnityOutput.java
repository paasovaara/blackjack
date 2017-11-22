package blackjack.io;

import blackjack.engine.GameContext;
import blackjack.engine.GameListener;
import blackjack.models.Card;
import blackjack.models.Deck;
import blackjack.models.Hand;
import blackjack.utils.Config;
import blackjack.utils.EventSender;

//public class UnityOutput implements GameListener {
public class UnityOutput extends ConsoleOutput {
    Config m_config;
    EventSender m_sender = new EventSender();

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
    public void stay(int playerId, Hand hand, GameContext context) {
        super.stay(playerId, hand, context);
        // nothing to do
    }

    @Override
    public void busted(int playerId, Hand hand, GameContext context) {
        super.busted(playerId, hand, context);
        String msg = "busted{<p>}";
        msg = msg.replaceAll("<p>", Integer.toString(playerId));

        m_sender.sendMessage(msg.getBytes());
    }
}
