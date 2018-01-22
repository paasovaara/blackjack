package blackjack.io.console;

import blackjack.ai.CardCounter;
import blackjack.ai.Classifier;
import blackjack.ai.PolynomialClassifier;
import blackjack.ai.Sample;
import blackjack.engine.GameContext;
import blackjack.engine.InputManager;
import blackjack.models.Bet;
import blackjack.models.GameSettings;
import blackjack.models.Hand;
import blackjack.models.PlayerAction;
import blackjack.tree.GameNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ConsoleInput implements InputManager {
    BufferedReader m_in;
    private boolean m_useDefaultBets;

    private boolean m_playOnYourOwn;

    //TODO refactor AI into separate class
    private List<Integer> m_aiPlayers = new LinkedList<>();
    private Classifier m_classifier;
    private CardCounter m_counter;

    public ConsoleInput(GameSettings settings) {
        m_in = new BufferedReader(new InputStreamReader(System.in));
        m_useDefaultBets = settings.useDefaultBet;
        m_playOnYourOwn = settings.playOnlyAgainstItself;

        if (settings.AIModelFile != null) {
            if (settings.usePolynomialModel) {
                //TODO refactor the model file to include the metadata, not just the coefficients.
                m_classifier = new PolynomialClassifier(settings.AIModelFile, settings.polynomialModelDegree, settings.includeMinPips);
            }
            else {
                m_classifier = new Classifier(settings.AIModelFile);
            }
        }
    }

    public void initialize(GameNode game) {
        // Now CardCounter is only available when ConsoleInput is used.
        // We could let GameNode create the CardCounter and include it to the GameContext.
        // In that case we could also use Decorator-pattern and let CardCounter wrap (extend) Deck instead of GameListener.
        m_counter = new CardCounter();
        game.addListener(m_counter);
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
                    printInput("How many human players?");
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
        int players = m_playOnYourOwn ? 0 : getPlayerCount(true);
        int aiPlayers = m_playOnYourOwn ? 1 : getPlayerCount(false);

        createAiPlayers(aiPlayers, players);

        int totalCount = players + aiPlayers;

        LinkedList<Bet> bets = new LinkedList<>();
        for(int n = 0; n < totalCount; n++) {
            int betValue = m_useDefaultBets ? 1 : getBet(n);
            if (betValue > 0) {
                Bet bet = new Bet(n, betValue);
                bets.add(bet);
            }
        }

        return bets;
    }

    private void createAiPlayers(int count, int startIndex) {
        // For now it's enough just have a dumb list of id's
        // If needed create a separate class
        for (int id = startIndex; id < count + startIndex; id++) {
            m_aiPlayers.add(id);
        }
    }

    private boolean isAiPlayer(int playerId) {
        return m_aiPlayers.stream().anyMatch(id -> id == playerId);
    }

    private PlayerAction getAiPlayerAction(int playerId, GameContext gameState) {
        Hand hand = (Hand)gameState.getVariable(GameContext.playerHandKey(playerId));
        Hand dealer = (Hand)gameState.getVariable(GameContext.KEY_DEALER_HAND);

        int deckWeight = m_counter != null ? m_counter.getCount() : 0;
        Sample input = new Sample(hand.getBestPipCount(), hand.getMinPipCount(), dealer.getBestPipCount(), deckWeight);
        boolean shouldHit = m_classifier.predict(input);
        return shouldHit ? PlayerAction.Hit : PlayerAction.Stay;
    }

    @Override
    public PlayerAction getInput(int playerId, GameContext gameState, Set<PlayerAction> options, boolean longTimeout) {
        while(true) {
            try {
                String handPrint = AsciiArt.printHands(gameState);
                if (isAiPlayer(playerId)) {
                    printInfo(handPrint);
                    PlayerAction action = getAiPlayerAction(playerId, gameState);
                    printInfo(action == PlayerAction.Hit ? "Hit me baby" : "I'm staying");
                    //We shouldn't do this here but just to keep things simple..
                    if (!m_playOnYourOwn) {
                        Thread.currentThread().sleep(1000);
                    }
                    return action;
                }
                else {
                    String print = handPrint + "\nChoose (h)it, (s)tay, (q)uit, (a)dvice or (d)ebug:";
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
            }
            catch (Exception e) {
                printInfo("Really bad option");
            }
        }
    }

}
