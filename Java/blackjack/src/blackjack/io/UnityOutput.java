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
                .replaceAll("<r>", Integer.toString(card.getRank().getId()));

        m_sender.sendMessage(msg.getBytes());
    }

}
