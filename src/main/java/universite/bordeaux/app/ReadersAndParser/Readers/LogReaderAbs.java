package universite.bordeaux.app.ReadersAndParser.Readers;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.stream.Collectors;

import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import universite.bordeaux.app.Constant.ConstantLog;
import universite.bordeaux.app.GameDataStructure.GameTurn;
import universite.bordeaux.app.GameDataStructure.Player;
import universite.bordeaux.app.GameDataStructure.PlayerItf;
import universite.bordeaux.app.ReadersAndParser.CheckCard;
import universite.bordeaux.app.ReadersAndParser.FileReader;



/**
 *
 */
public abstract class LogReaderAbs {
    /**
     * constant to hold the index to split a line to get the number of cards.
     */
    public static final int SEARCH_TRESEHOLD = 2;

    /**
     * number of dots uset to indicate the subaction level.
     */
    public static final int SUBSTRING_CONST = 3;

    /**
     * holds the version of the parser.
     */
    protected int version;

    /**
     * the file reader with the log data.
     */
    protected FileReader log;
    private org.jsoup.nodes.Document doc;

    /**
     * @param fileLog constructor the class
     */
    public LogReaderAbs(final File fileLog) {
        this.log = new FileReader(fileLog);
    }

    /**
     * parses the first line and grab the list of winners.
     *
     * @return a list of winners present on the line
     */
    protected final ArrayList<String> getWinners() {
        org.jsoup.nodes.Document doc;

        ArrayList<String> result = new ArrayList<>();
        if (this.log.jumpline().contains("<title>")) {

            //creates a document with the first line of the log
            doc = Jsoup.parse(this.log.getLine());

            //find the match winner
            String[] win = doc.body().text().split("wins!");
            if (win[0].contains("rejoice in their shared victory!")) {
                String[] winners = win[0]
                    .split("rejoice in their shared victory!")[0].split("and");
                for (String x: winners) {
                    result.add(x.trim());
                }
            } else {
                result.add(win[0].trim());
                if (win[0].trim().equals("Game log")) {
                    throw new UnsupportedOperationException();
                }
            }
        }
        this.log.rewindFile();
        return result;
    }

    /**
     * parses the line:
     * ex:  All <span class=card-victory>Provinces</span> are gone.
     * and look for the list of cards that are listed as gonne.
     * @return a list of cards present on the line parsed
     */
    protected final ArrayList<String> getCardsGone() {
        org.jsoup.nodes.Document  doc;
        ArrayList<String> cardsGone = new ArrayList<>();
        if (this.log.jumpline().contains("<title>")) {
            //jumps to the next line
            doc = Jsoup.parse(this.log.jumpline());

            //finds how the game finished by getting the empty piles
            Elements links = doc.select("span");
            for (Element link : links) {
                String temp = CheckCard.verifyCard(link.text());
                if (temp != null) {
                    cardsGone.add(temp);
                }
            }
        }
        this.log.rewindFile();
        return cardsGone;
    }

    /**
     * parses the line:
     * ex: trash: a <span class=card-treasure>Silver</span>
     * and parse a list of cards that are in the trash.
     * @return a map with the cards and quantity of the trashed parsed
     */
    protected final HashMap<String, Integer> getTrash() {
        org.jsoup.nodes.Document doc;
        HashMap<String, Integer> result = null;
        //look for the line that describes the trash
        if (this.log.searchLineWithString("trash: (.*)") != null) {
            doc = Jsoup.parse(this.log.getLine());

            //parse the line trash and add the list to the game object
            if (!doc.text().contains("nothing")) {
                String[] trash = doc.text()
                    .replace("trash: ", "")
                    .replace("and", "")
                    .split(",");
                result = getCards(trash, SEARCH_TRESEHOLD);
            }
        }
        //return the file pointer to the beginning of the file
        this.log.rewindFile();
        return result;
    }

    /**
     * get all the cards that are in the (cards in suply) list.
     * @return a list of cards presents on the line parsed
     */
    protected final ArrayList<String> getMarket() {
        org.jsoup.nodes.Document doc;
        ArrayList<String> market = new ArrayList<>();
        if (this.log.jumpline().contains("<title>")) {
            //jumps 2 line
            this.log.jumpline();
            this.log.jumpline();

            doc = Jsoup.parse(this.log.jumpline());

            //get all the cards available on market
            Elements links = doc.select("span");
            for (Element link : links) {
                String temp = CheckCard.verifyCard(link.text());
                if(temp != null) {
                    market.add(temp);
                }
            }
        }
        this.log.rewindFile();
        return market;
    }
    
    /** 
	 * the function adds turns, victory cards and points to the object PlayerItf
	 * @param PlayerItf pl, org.jsoup.nodes.Document doc
	 */
    protected void getPointsVcTurnPlayer(PlayerItf pl, org.jsoup.nodes.Document doc) {

        String[] firstBreak;

        firstBreak = doc.text().split("points");

        //set the player points
        String[] p = firstBreak[0].split(":");
        String temp = p[p.length - 1].replace(" ", "");

        try {
            pl.setPoints(Integer.parseInt(temp));
        } catch (NumberFormatException e) {
            pl.setPoints(0);
        }

        //split the string to get all victory points cards
        String list = firstBreak[1].split(";")[0];
        list = list.substring(2, list.length() - 1)
            .replace("and", "");
        if (!list.contains("nothing")) {
            String[] victoryCards = list.split(",");
            pl.setVictoryCard(getCards(victoryCards,
                                       SEARCH_TRESEHOLD));
            //insert the victorycards on the player object
        }
        pl.setTurns(Integer.parseInt(firstBreak[1]
                                     .split(";")[1]
                                     .replace(" turns", "")
                                     .replace(" ", "")));  
    	
    }

    /**
     * reads the player section of the log
     * and parses each player present on the log as a Player object.
     *
     * @return a list of Players parsed from the log
     */
    protected final ArrayList<Document> getPlayers() {
        boolean start = true;

        org.jsoup.nodes.Document doc;

        ArrayList<PlayerItf> players = new ArrayList<>();

        //jump to the players section of the log
        if (this.log.searchLineWithString("(.*)----------------------(.*)")
            != null) {
            doc = Jsoup.parse(this.log.getLine());
            if (this.log.getLine().contains("----------------------")
                && start) {
                start = false;
                //jumps to the first player
                this.log.jumpline();
                doc = Jsoup.parse(this.log.jumpline());
                while (!this.log.getLine().contains("----------------------")) {
                    //create the player
                    PlayerItf pl = createPlayer(doc);
                    
                    this.getPointsVcTurnPlayer(pl, doc);

                    //get next line
                    doc = Jsoup.parse(this.log.jumpline());
                    //get opening cards
                    Elements links = doc.select("span");
                    for (Element link : links) {
                        String temp = CheckCard.verifyCard(link.text());
                        if (temp != null) {
                            pl.insertOpening(temp);
                        }
                    }
                    //next line
                    doc = Jsoup.parse(this.log.jumpline());
                    //get deck cards
                    if (!doc.text().contains("0 cards")) {
                        String[] deck = doc.text()
                            .split("\\[[0-9]* cards\\]")[1]
                            .split(",");
                        pl.setDeck(getCards(deck, SEARCH_TRESEHOLD));
                    }
                    players.add(pl);
                    //jumps 1 line
                    this.log.jumpline();
                    doc = Jsoup.parse(this.log.jumpline());
                }
            }

        }
        //return the file pointer to the beginning of the file
        getFirstHand(players);
        this.log.rewindFile();
        return players.stream().map(PlayerItf::toDoc)
            .collect(Collectors
                     .toCollection(ArrayList::new));
    }

    /**
     * @param doc jsoup Docment to be parsed .
     * @return the new object player
     */
    protected PlayerItf createPlayer(final org.jsoup.nodes.Document doc) {
        this.doc = doc;
        String name = doc.select("b")
            .text()
            .replaceAll("^#[0-9]* ", "");
        return new Player(name);
    }

    /**
     * gets the first hand for each player on the match.
     * @param players the players to be parsed.
     *
     */
    protected final void getFirstHand(ArrayList<PlayerItf> players) {
        org.jsoup.nodes.Document doc;
        this.log.rewindFile();
        if (this.log.searchLineWithString("(.*)'s first hand: (.*)")
            != null) {
            for (int x = 0; x < players.size(); x++) {
                doc = Jsoup.parse(this.log.getLine());
                String[] firstHand = doc.text().split("'s first hand: ");

                for (PlayerItf pla : players) {
                    if (pla.getPlayerName()
                        .equals(firstHand[0]
                                .substring(1))) {
                        pla.setFirstHand(getCards(firstHand[1]
                                                  .replace(".)", "")
                                                  .split("and"),
                                                  SEARCH_TRESEHOLD));
                    }
                }
                this.log.searchLineWithString("(.*)'s first hand: (.*)");
            }
        }
        this.log.rewindFile();
    }

    /**
     * @param cardsToParse list of string with the cards to be parsed .
     * @param limit the character index for the number and card name
     * @return a HashMap with cards and quantities
     */
    protected final HashMap<String, Integer> getCards(final String[] cardsToParse,
                                                      final int  limit) {
        HashMap<String, Integer> cards = new HashMap<>();
        for (String x : cardsToParse) {

            x = x.trim();
            String nb = x.substring(0,x.indexOf(' '));
            String card = x.substring(x.indexOf(' ')+1);
            int qty;
            try {
                qty = Integer.parseInt(nb);
            } catch (NumberFormatException e) {
                qty = 1;
            }
            card = CheckCard.verifyCard(card);
            if (card != null) {
                cards.put(card, qty);
            }
        }
        return cards;
    }

    /**
     * obtains a string and return a hash map with the cards and quantity.
     * @param cards string
     * @return hashmap
     */
    protected final HashMap<String, Integer> obtainCards(String cards) {
        HashMap<String, Integer> move;
        String[] playedCards;
        if (cards.contains(",")) {
            cards = cards.replace("and", "");
            playedCards = cards.split(",");
        } else {
            playedCards = cards.split(" and ");
        }
        move = this.getCards(playedCards, 4);
        return move;
    }

    /**
     * counts the depth of the sub action.
     * @param line line to count
     * @return a int with the depth value
     */
    protected final int countLevel(final String line) {
        int level = 0;
        String countLevel = line;
        while (countLevel.matches("\\.\\.\\.(.*)")) {
            countLevel = countLevel
                .substring(SUBSTRING_CONST);
            level++;
        }
        return level;
    }

    /**
     *  parses the line to get the cards played on the buys move.
     * @param doc line to be parsed
     * @param playername the player that is playing the actual turn
     * @return a map with the cards and amounts
     */
    protected final HashMap<String, Integer>
        getCardsForBuysMove(final org.jsoup.nodes.Document doc,
                            final String playername) {
        String cards = "";
        if (playername.contains("buys") && doc.text().contains(playername)) {
            cards = doc.text()
                .trim()
                .replace(playername + " buys ", "")
                .replaceAll("\\.", "");
        } else {
            cards = doc.text().split(" buys ")[1].replaceAll("\\.", "");
        }
        return obtainCards(cards);
    }

    /**
     *  parses the line to get the cards played on the plays move.
     * @param doc line to be parsed
     * @param playername the player that is playing the actual turn
     * @return a map with the cards and amounts
     */
    protected final HashMap<String, Integer>
        getCardsForPlaysMove(final org.jsoup.nodes.Document doc,
                             final String playername) {
        String cards = "";
        if (playername.contains("plays") && doc.text().contains(playername)) {
            cards = doc.text()
                .trim()
                .replace(playername
                         + " plays ", "")
                .replaceAll("\\.", "");
        } else {
            cards = doc.text() .split(" plays ")[1] .replaceAll("\\.", "");
        }

        cards = cards.replace("again", "") .replace("a third time", "");
        return obtainCards(cards);
    }

    /**
     *  parses the line to get the cards played on the draws move.
     * @param doc line to be parsed
     * @return a map with the cards and amounts
     */
    protected final HashMap<String, Integer>
        getCardsForDrawsMove(final org.jsoup.nodes.Document doc) {
        String cards = doc.text().trim().split(" draws: ")[1]
            .replaceAll("\\.\\)", "");
        return obtainCards(cards);
    }

    /**
     *  parses the line to get the cards played on the draws and discard move.
     * @param doc line to be parsed
     * @return a map with the cards and amounts
     */
    protected final HashMap<String, Integer>
        getCardsForDrawsAndDiscardMove(final org.jsoup.nodes.Document doc) {
        String cards = doc.text()
            .trim()
            .split(" draws and discard ")[1]
            .replaceAll("\\.", "");
        return obtainCards(cards);
    }

    /**
     *  parses the line to get the cards played on the gains move.
     * @param doc line to be parsed
     * @return a map with the cards and amounts
     */
    protected final HashMap<String, Integer>
        getCardsForGainsMove(final org.jsoup.nodes.Document doc) {
        String cards =  doc.text()
            .trim()
            .split(" gains ")[1]
            .replaceAll("\\.", "")
            .replaceAll(" on top of the deck", "")
            .replaceAll(" on the deck", "")
            .replaceAll(" to replace it", "");
        return obtainCards(cards);
    }

    /**
     *  parses the line to get the cards played on the discard and gain move.
     * @param doc line to be parsed
     * @return a collection of map with the cards and amounts
     */
    protected final Collection<HashMap<String, Integer>>
        getCardsForDiscardAndGainMove(final org.jsoup.nodes.Document doc) {
        Collection<HashMap<String, Integer>> result = new ArrayList<>();
        String cards =  doc.text()
            .trim()
            .split(" discards ")[1]
            .replaceAll("\\.", "")
            .replaceAll("on the deck", "");

        if (cards.contains(" and gains ")) {
            String[] actionList;
            actionList = cards.split(" and gains ");
            result.add(obtainCards(actionList[0]));
            result.add(obtainCards(actionList[1]));
        } else {
            result.add(obtainCards(cards));
        }
        return result;
    }

    /**
     *  parses the line to get the cards played on the reveals move.
     * @param doc line to be parsed
     * @return a collection of map with the cards and amounts
     */
    protected final Collection<HashMap<String, Integer>>
        getCardsForRevealMove(final org.jsoup.nodes.Document doc) {
        Collection<HashMap<String, Integer>> result = new ArrayList<>();
        String cards = doc.text()
            .trim()
            .split(" reveals ")[1]
            .replaceAll("\\.", "");
        boolean trashes = false;
        if (cards.contains("and trashes it")) {
            trashes = true;
            cards = cards.replaceAll("and trashes it", "");
        } else if (cards.contains("giving")) {
            cards = cards.split("giving")[0].replaceAll(",","");
        }
        cards = cards
            .replaceAll(" and then ", ",")
            .replaceAll(", and ", ",")
            .replaceAll(" and ", ",");
        result.add(obtainCards(cards));
        if (trashes) {
            result.add(obtainCards(cards));
        }
        return result;

    }

    /**
     *  parses the line to get the cards played on the trash move.
     * @param doc line to be parsed
     * @return a collection of map with the cards and amounts
     */
    protected final Collection<HashMap<String, Integer>>
        getCardsForTrashMove(final org.jsoup.nodes.Document doc) {
        Collection<HashMap<String, Integer>> result = new ArrayList<>();
        String cards =  doc.text()
            .trim()
            .split(" trashes ")[1]
            .replaceAll("and gets (.*)\\.", "")
            .replaceAll("\\.", "");
        if (cards.contains("gaining")) {
            String[] actionList = cards.split(", gaining ");
            result.add(obtainCards(actionList[0]));
            result.add(obtainCards(actionList[1].replaceAll(" in hand", "")));
        } else {
            result.add(obtainCards(cards));
        }
        return result;
    }

    /**
     *  parses the line to get the cards played on the putting move.
     * @param doc line to be parsed
     * @return a map with the cards and amounts
     */
    protected final HashMap<String, Integer>
        getCardsForPuttingMove(final org.jsoup.nodes.Document doc) {
        String cards =  doc.text()
            .trim()
            .split(" putting ")[1]
            .replaceAll("\\.", "")
            .replaceAll("into the hand", "");
        return obtainCards(cards);
    }

    /**
     *  parses the line to get the cards played on the gaining move.
     * @param doc line to be parsed
     * @return a map with the cards and amounts
     */
    protected final HashMap<String, Integer>
        getCardsForGainingMove(final org.jsoup.nodes.Document doc) {
        String cards =  doc.text()
            .trim()
            .split(" gaining ")[1]
            .replaceAll("\\.", "")
            .replaceAll("another", "a");
        return obtainCards(cards);
    }

    /**
     *  parses the line to get the cards played on the playing move.
     * @param doc line to be parsed
     * @return a map with the cards and amounts
     */
    protected final HashMap<String, Integer>
        getCardsForPlayingMove(final org.jsoup.nodes.Document doc) {
        String cards =  doc.text()
            .trim()
            .split(" playing ")[1]
            .replaceAll("\\.", "");
        return obtainCards(cards);
    }

    /**
     *  parses the line to get the cards played on the revealing move.
     * @param doc line to be parsed
     * @return a collection of map with the cards and amounts
     */
    protected final Collection<HashMap<String, Integer>>
        getCardsForRevealingMove(final org.jsoup.nodes.Document doc) {
        Collection<HashMap<String, Integer>> result = new ArrayList<>();
        String cards =  doc.text()
            .trim()
            .split(" revealing ")[1]
            .replaceAll("\\.", "");
        if (cards.contains(" and putting it in the hand")) {
            cards = cards.replaceAll(" and putting "
                                     + "it in the hand", "");
            result.add(obtainCards(cards));
            result.add(obtainCards(cards));
        } else {
            result.add(obtainCards(cards));
        }
        return result;

    }

    /**
     *  parses the line to get the cards played on the discarding move.
     * @param doc line to be parsed
     * @return a map with the cards and amounts
     */
    protected final HashMap<String, Integer>
        getCardsForDiscardingMove(final org.jsoup.nodes.Document doc) {
        String cards =  doc.text()
            .trim()
            .split(" discarding ")[1]
            .replaceAll("\\.", "")
            .replaceAll("the ([0-9]+)", "$1")
            .replaceAll("and the", "a");
        return obtainCards(cards);
    }

    /**
     *  parses the line to get the cards played on the drawing move.
     * @param doc line to be parsed
     * @return a map with the cards and amounts
     */
    protected final HashMap<String, Integer>
        getCardsForDrawingMove(final org.jsoup.nodes.Document doc) {
        String cards =  doc.text()
            .trim()
            .split(" drawing ")[1]
            .replaceAll("\\.", "")
            .replaceAll("from the Black"
                        + " Market deck", "");
        return obtainCards(cards);
    }

    /**
     *  parses the line to get the cards played on the trashing move.
     * @param doc line to be parsed
     * @return a map with the cards and amounts
     */
    protected final HashMap<String, Integer>
        getCardsForTrashingMove(final String doc) {
        String cards =  doc.trim()
            .split(" trashing ")[1]
            .replaceAll("(\\</span\\>)"
                        + " (for|from).*\\.$", "$1");
        cards = Jsoup.parse(cards)
            .text()
            .replaceAll("\\.", "")
            .replaceAll("the", "a");
        return obtainCards(cards);
    }


    /**
     * reponsible to recognize and iterate over all turns of a log and call the proper
     * function to treat the action made.
     * @return a list of Documents representing the turns.
     */
    protected final ArrayList<Document> getGameLog() {
        org.jsoup.nodes.Document doc;
        ArrayList<GameTurn> turns = new ArrayList<>();
        boolean finished = false;
        int last = 0;
        int turn = 1;
        GameTurn t = new GameTurn(turn);
        String playername = "";

        //get the start of the game log
        String logBegin = this.log.searchLineWithString("(.*)'s turn 1(.*)");
        if (logBegin == null) {
            throw new UnsupportedOperationException();
        }
        doc = Jsoup.parse(logBegin);
        while (!finished) {
            if (doc.text().matches("(.*)'s turn [0-9]*(.*)")) {
                int tempTurn = 0;
                //if a turn is possessed by another player this turn has no number in the log
                if (!doc.text().contains("(possessed by")) {
                    String[] tn = doc.text().split("'s turn");
                    tempTurn = Integer.parseInt(tn[tn.length -1]
                                                .replaceAll("[^0-9]+", ""));
                }
                if (tempTurn > turn) {
                    turn = tempTurn;
                    t = new GameTurn(turn);
                }

                playername = turnGetPlayer(doc.text());
            }
            doc = Jsoup.parse(this.log.jumpline());

            while (!doc.text().matches("(.*)'s turn [0-9]*(.*)")) {
                if (doc.text().contains("game aborted: ")
                    || doc.text().contains("resigns from the game")
                    || doc.text().matches("All [a-z A-z]* are gone.")
                    || doc.text().matches("(.*) are all gone.")) {
                    finished = true;
                    break;
                } else {
                    if (!doc.text().contains("nothing")
                        && !doc.text().contains("reshuffles.)")) {
                        switch (LogElements.type(this.log.getLine())) {
                        case PLAYS:
                            t.insertMove(playername,
                                         countLevel(doc.text()),
                                         ConstantLog.PLAYS,
                                         getCardsForPlaysMove(doc,
                                                              playername));
                            break;
                        case PLAYING:
                            t.insertMove(playername,
                                         countLevel(doc.text()),
                                         ConstantLog.PLAYS,
                                         getCardsForPlayingMove(doc));
                            break;
                        case BUYS:
                            t.insertMove(playername,
                                         countLevel(doc.text()),
                                         ConstantLog.BUYS,
                                         getCardsForBuysMove(doc,
                                                             playername));
                            break;
                        case DRAWS_LAST_ACTION:
                            t.insertMove(playername,
                                         0,
                                         ConstantLog.DRAWS,
                                         getCardsForDrawsMove(doc));
                            break;
                        case DRAWS_DISCARD:
                            t.insertMove(playername,
                                         countLevel(doc.text()),
                                         ConstantLog.DRAWS,
                                         getCardsForDrawsAndDiscardMove(doc));
                            t.insertMove(playername,
                                         countLevel(doc.text()),
                                         ConstantLog.DISCARD,
                                         getCardsForDrawsAndDiscardMove(doc));
                            break;
                        case DRAWING:
                            t.insertMove(playername,
                                         0,
                                         ConstantLog.DRAWS,
                                         getCardsForDrawingMove(doc));
                            break;
                        case GAINS:
                            t.insertMove(playername,
                                         countLevel(doc.text()),
                                         ConstantLog.GAINS,
                                         getCardsForGainsMove(doc));
                            break;
                        case GAINING:
                            t.insertMove(playername,
                                         countLevel(doc.text()),
                                         ConstantLog.GAINS,
                                         getCardsForGainingMove(doc));
                            break;
                        case DISCARD_AND_GAINS:
                            Collection<HashMap<String, Integer>> discCards;
                            discCards = getCardsForDiscardAndGainMove(doc);
                            if (discCards.size() > 1) {
                                t.insertMove(playername,
                                             countLevel(doc.text()),
                                             ConstantLog.DISCARD,
                                             (HashMap) discCards
                                             .toArray()[0]);
                                t.insertMove(playername,
                                             countLevel(doc.text()),
                                             ConstantLog.GAINS,
                                             (HashMap) discCards
                                             .toArray()[1]);
                            } else {
                                t.insertMove(playername,
                                             countLevel(doc.text()),
                                             ConstantLog.DISCARD,
                                             (HashMap) discCards
                                             .toArray()[0]);
                            }
                            break;
                        case TRASHES:
                            Collection<HashMap<String, Integer>> trashCards;
                            trashCards = getCardsForTrashMove(doc);
                            if (trashCards.size() > 1) {
                                t.insertMove(playername,
                                             countLevel(doc.text()),
                                             ConstantLog.TRASH,
                                             (HashMap) trashCards
                                             .toArray()[0]);
                                t.insertMove(playername,
                                             countLevel(doc.text()),
                                             ConstantLog.GAINS,
                                             (HashMap) trashCards
                                             .toArray()[1]);
                            } else {
                                t.insertMove(playername,
                                             countLevel(doc.text()),
                                             ConstantLog.TRASH,
                                             (HashMap) trashCards
                                             .toArray()[0]);
                            }
                            break;
                        case TRASHING:
                            t.insertMove(playername,
                                         countLevel(doc.text()),
                                         ConstantLog.TRASH,
                                         getCardsForTrashingMove(log.getLine()));
                            break;
                        case REVEALS:
                            Collection<HashMap<String, Integer>> revealCards;
                            revealCards = getCardsForRevealMove(doc);
                            if (revealCards.size() > 1) {
                                t.insertMove(playername,
                                             countLevel(doc.text()),
                                             ConstantLog.REVEAL,
                                             (HashMap) revealCards
                                             .toArray()[0]);
                                t.insertMove(playername,
                                             countLevel(doc.text()),
                                             ConstantLog.TRASH,
                                             (HashMap) revealCards
                                             .toArray()[1]);
                            } else {
                                t.insertMove(playername,
                                             countLevel(doc.text()),
                                             ConstantLog.REVEAL,
                                             (HashMap) revealCards
                                             .toArray()[0]);
                            }

                            break;
                        case REVEALING:
                            Collection<HashMap<String, Integer>> revealingCards;
                            revealingCards = getCardsForRevealingMove(doc);
                            if (revealingCards.size() > 1) {
                                t.insertMove(playername,
                                             countLevel(doc.text()),
                                             ConstantLog.REVEAL,
                                             (HashMap) revealingCards
                                             .toArray()[0]);
                                t.insertMove(playername,
                                             countLevel(doc.text()),
                                             ConstantLog.PUTTING,
                                             (HashMap) revealingCards
                                             .toArray()[1]);
                            } else {
                                t.insertMove(playername,
                                             countLevel(doc.text()),
                                             ConstantLog.REVEAL,
                                             (HashMap) revealingCards
                                             .toArray()[0]);
                            }
                            break;
                        case PUTTING:
                            t.insertMove(playername,
                                         countLevel(doc.text()),
                                         ConstantLog.PUTTING,
                                         getCardsForPuttingMove(doc));
                            break;
                        case DISCARDING:
                            t.insertMove(playername,
                                         countLevel(doc.text()),
                                         ConstantLog.DISCARD,
                                         getCardsForDiscardingMove(doc));
                            break;
                        case NOT_DEFINED:
                            break;
                        default:
                            break;
                        }
                    }
                }
                doc = Jsoup.parse(this.log.jumpline());
            }
            if (last != turn) {
                turns.add(t);
                last = turn;
            }

        }
        this.log.rewindFile();

        return turns.stream().map(GameTurn::toDoc)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * reads the name of the file and extracts the date.
     * @return the date and time of the game
     */
    protected final Date getDateTime() {
        Date temp = new Date();
        String[] date = this.log.getName().split("-");
        DateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'",
                                                 Locale.ENGLISH);
        try {
            temp = format.parse(date[1] + "T" + date[2] + "Z");
        } catch (ParseException e) {
            System.out.println(e);
        }
        return temp;
    }

    /**
     * get the player name from the string.
     * @param s string
     * @return string player name
     */
    protected static String turnGetPlayer(final String s) {
        return s.trim()
            .replaceAll("^—", "")
            .replaceAll("'s turn [0-9]*(.*)", "").trim();
    }

    /**
     * creates a Document from a map.
     *
     * @param map lhe map to create the document
     * @return Document with the map data
     */
    protected final Document hashToDoc(final HashMap<String, Integer> map) {
    	Document temp = new Document();
    	if(map != null ) {      
	        for (String x: map.keySet()) {
	            temp.append(x, map.get(x));
	        }
    	}
        return temp;
    }

    /**
     * generates a Document compatible with mongodb based on the log.
     * @return a Document
     */
    protected final Document toDoc() {
        return new Document()
            .append(ConstantLog.DATE, this.getDateTime())
            .append(ConstantLog.FILENAME, this.log.getName())
            .append(ConstantLog.ELOGAP, 0)
            .append(ConstantLog.WINNERS, this.getWinners())
            .append(ConstantLog.CARDSGONNE, this.getCardsGone())
            .append(ConstantLog.MARKET, this.getMarket())
            .append(ConstantLog.TRASH, hashToDoc(this.getTrash()))
            .append(ConstantLog.PLAYERS, this.getPlayers())
            .append(ConstantLog.LOG, this.getGameLog());
    }

    /**
     * closes the file descriptor.
     */
    public final void close(){
        log.close();
    }

}
