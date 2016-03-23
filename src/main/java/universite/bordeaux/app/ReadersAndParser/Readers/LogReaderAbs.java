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

import universite.bordeaux.app.GameDataStructure.GameTurn;
import universite.bordeaux.app.GameDataStructure.Player;
import universite.bordeaux.app.GameDataStructure.PlayerItf;
import universite.bordeaux.app.ReadersAndParser.FileReader;



/**
 *
 */
public abstract class LogReaderAbs {
    public static final int SEARCH_TRESEHOLD = 2;
    public static final int SUBSTRING_CONST = 3;
    protected int version;
    protected FileReader log;

    public LogReaderAbs(final String fileLog) {
        this.log = new FileReader(new File(fileLog));
    public LogReaderAbs(final File fileLog) {
        this.log = new FileReader(fileLog);
    }

    /**
     * parses the first line and grab the list of winners.
     *
     * @return a list of winners present on the line
     */
    protected Document getWinners() {
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
        return new Document("winners",result);
    }

    /**
     * parses the line:
     * ex:  All <span class=card-victory>Provinces</span> are gone.
     * and look for the list of cards that are listed as gonne.
     * @return a list of cards present on the line parsed
     */
    protected Document getCardsGone() {
        org.jsoup.nodes.Document  doc;
        ArrayList<String> cardsGone = new ArrayList<>();
        if (this.log.jumpline().contains("<title>")) {
            //jumps to the next line
            doc = Jsoup.parse(this.log.jumpline());

            //finds how the game finished by getting the empty piles
            Elements links = doc.select("span");
            for (Element link : links) {
                cardsGone.add(link.text());
            }
        }
        this.log.rewindFile();
        return new Document("cardsgone",cardsGone);
    }

    /**
     * parses the line:
     * ex: trash: a <span class=card-treasure>Silver</span>
     * and parse a list of cards that are in the trash.
     * @return a map with the cards and quantity of the trashed parsed
     */
    protected Document getTrash() {
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
        return new Document("trash",hashToDoc(result));
    }

    /**
     * get all the cards that are in the (cards in suply) list.
     * @return a list of cards presents on the line parsed
     */
    protected Document getMarket() {
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
                market.add(link.text());
            }
        }
        this.log.rewindFile();
        return new Document("market", market);
    }

    /**
     * reads the player section of the log
     * and parses each player present on the log as a Player object.
     *
     * @return a list of Players parsed from the log
     */
    protected Document getPlayers() {
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
                    //break the string into 2 parts to find the points
                    if (doc.text().contains("points")) {
                        String[] firstBreak = doc.text().split("points");
                        //set the player points
                        String[] p = firstBreak[0].split(":");
                        String temp = p[p.length - 1].replace(" ", "");
                        if (temp.contains("resigned")) {
                            pl.setPoints(0);
                        } else {
                            try {
                                pl.setPoints(Integer.parseInt(temp));
                            } catch (NumberFormatException e) {
                                pl.setPoints(0);
                            }
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
                        //get the turns
                        pl.setTurns(Integer.parseInt(firstBreak[1]
                                                     .split(";")[1]
                                                     .replace(" turns", "")
                                                     .replace(" ", "")));
                    }
                    //get next line
                    doc = Jsoup.parse(this.log.jumpline());
                    //get opening cards
                    Elements links = doc.select("span");
                    for (Element link : links) {
                        pl.insertOpening(link.text());
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
        return new Document("players", players.stream()
                            .map(PlayerItf::toDoc)
                            .collect(Collectors
                                     .toCollection(ArrayList::new)));
    }

    //if (doc.select("b").text().contains("points")) {
    //    name = doc.select("b").text().split(":")[0];
    // } else {
    //TODO java doc
    protected PlayerItf createPlayer(org.jsoup.nodes.Document doc) {
        String name = doc.select("b")
            .text()
            .replaceAll("^#[0-9]* ", "");
        return new Player(name);
    }

    protected void getFirstHand(ArrayList<PlayerItf> players) {
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
                                                  .split("and")
                                                  ,SEARCH_TRESEHOLD));
                    }
                }
                this.log.searchLineWithString("(.*)'s first hand: (.*)");
            }
        }
        this.log.rewindFile();
    }

    protected HashMap<String, Integer> getCards(final String[] cardsToParse,
                                              final int  limit) {
        HashMap<String, Integer> cards = new HashMap<>();
        for (String x : cardsToParse) {

            x = x.trim();

            String[] temp = x.split(" ", limit);
            int qty;
            try {
                qty = Integer.parseInt(temp[0]);
            } catch (NumberFormatException e) {
                qty = 1;
            }
            String card = temp[1];
            cards.put(card, qty);
        }
        return cards;
    }

    /**
     * gets a string and return a hash map with the cards and quantity.
     * @param cards string
     * @return hashmap
     */
    protected static HashMap<String, Integer> getCards(String cards) {
        HashMap<String, Integer> move = new HashMap<>();
        if (cards.contains(",")) {
            cards = cards.replace("and", "");
            String[] playedCards = cards.split(",");
            for (String x: playedCards) {
                x = x.trim();
                String[] card = x.split(" ", 4);
                int qty;
                try {
                    qty = Integer.parseInt(card[0]);
                } catch (NumberFormatException e) {
                    qty = 1;
                }
                String c = card[1];
                move.put(c, qty);
            }
        } else {
            String[] playedCards = cards.split(" and ");
            for (String x: playedCards) {
                x = x.trim();
                String[] card = x.split(" ", 4);
                int qty;
                try {
                    qty = Integer.parseInt(card[0]);
                } catch (NumberFormatException e) {
                    qty = 1;
                }
                String c = card[1];
                move.put(c, qty);
            }
        }
        return move;
    }

    protected int countLevel(String line) {
        int level = 0;
        String countLevel = line;
        while (countLevel.matches("\\.\\.\\.(.*)")) {
            countLevel = countLevel
                .substring(SUBSTRING_CONST);
            level++;
        }
        return level;
    }

    protected HashMap<String, Integer> getCardsForBuysMove(org.jsoup.nodes.Document doc) {
        String cards = "";
        //TODO
        // if (playername.contains("buys")) {
        //     cards = doc.text()
        //             .trim()
        //             .replace(playername + " buys ", "")
        //             .replaceAll("\\.", "");
        // } else {
        cards = doc.text().split(" buys ")[1].replaceAll("\\.", "");

        return getCards(cards);
    }

    protected HashMap<String,Integer> getCardsForPlaysMove(org.jsoup.nodes.Document doc) {
        String cards = "";
        //TODO
        // if (playername.contains("plays")) {
        //     cards = doc.text()
        //             .trim()
        //             .replace(playername
        //                     + " plays ", "")
        //             .replaceAll("\\.", "");
        cards = doc.text() .split(" plays ")[1] .replaceAll("\\.", "");

        cards = cards.replace("again", "") .replace("a third time", "");

        return getCards(cards);
    }

    protected HashMap<String,Integer> getCardsForDrawsMove(org.jsoup.nodes.Document doc) {
        String cards = doc.text().trim().split(" draws: ")[1]
            .replaceAll("\\.\\)", "");
        return getCards(cards);
    }

    protected HashMap<String,Integer> getCardsForDrawsAndDiscardMove(org.jsoup.nodes.Document doc) {
        String cards = doc.text()
            .trim()
            .split(" draws and discard ")[1]
            .replaceAll("\\.", "");
        return getCards(cards);
    }

    protected HashMap<String,Integer> getCardsForGainsMove(org.jsoup.nodes.Document doc) {
        String cards =  doc.text()
            .trim()
            .split(" gains ")[1]
            .replaceAll("\\.", "")
            .replaceAll(" on top of the deck", "")
            .replaceAll(" on the deck", "")
            .replaceAll(" to replace it", "");
        return getCards(cards);
    }

    protected Collection<HashMap<String,Integer>> getCardsForDiscardAndGainMove(org.jsoup.nodes.Document doc) {
        Collection<HashMap<String,Integer>> result = new ArrayList<>();
        String cards =  doc.text()
            .trim()
            .split(" discards ")[1]
            .replaceAll("\\.", "")
            .replaceAll("on the deck", "");

        if (cards.contains(" and gains ")) {
            String[] actionList;
            actionList = cards.split(" and gains ");
            result.add(getCards(actionList[0]));
            result.add(getCards(actionList[1]));
        } else {
            result.add(getCards(cards));
        }
        return result;
    }

    protected Collection<HashMap<String,Integer>> getCardsForRevealMove(org.jsoup.nodes.Document doc) {
        Collection<HashMap<String,Integer>> result = new ArrayList<>();
        String cards = doc.text()
            .trim()
            .split(" reveals ")[1]
            .replaceAll("\\.", "");
        boolean trashes = false;
        if (cards.contains("and trashes it")) {
            trashes = true;
            cards = cards.replaceAll("and trashes it", "");
        }
        cards = cards
            .replaceAll(" and then ", ",")
            .replaceAll(", and ", ",")
            .replaceAll(" and ", ",");
        result.add(getCards(cards));
        if (trashes) {
            result.add(getCards(cards));
        }
        return result;

    }

    protected Collection<HashMap<String,Integer>> getCardsForTrashMove(org.jsoup.nodes.Document doc) {
        Collection<HashMap<String,Integer>> result = new ArrayList<>();
        String cards =  doc.text()
            .trim()
            .split(" trashes ")[1]
            .replaceAll("and gets (.*)\\.", "")
            .replaceAll("\\.", "");
        if (cards.contains("gaining")) {
            String[] actionList = cards.split(", gaining ");
            result.add(getCards(actionList[0]));
            result.add(getCards(actionList[1].replaceAll(" in hand", "")));
        } else {
            result.add(getCards(cards));
        }
        return result;
    }

    protected HashMap<String,Integer> getCardsForPuttingMove(org.jsoup.nodes.Document doc) {
        String cards =  doc.text()
            .trim()
            .split(" putting ")[1]
            .replaceAll("\\.", "")
            .replaceAll("into the hand", "");
        return getCards(cards);
    }

    protected HashMap<String,Integer> getCardsForGainingMove(org.jsoup.nodes.Document doc) {
        String cards =  doc.text()
            .trim()
            .split(" gaining ")[1]
            .replaceAll("\\.", "")
            .replaceAll("another", "a");
        return getCards(cards);
    }

    protected HashMap<String,Integer> getCardsForPlayingMove(org.jsoup.nodes.Document doc) {
        String cards =  doc.text()
            .trim()
            .split(" playing ")[1]
            .replaceAll("\\.", "");
        return getCards(cards);
    }

    protected Collection<HashMap<String,Integer>> getCardsForRevealingMove(org.jsoup.nodes.Document doc) {
        Collection<HashMap<String,Integer>> result = new ArrayList<>();
        String cards =  doc.text()
            .trim()
            .split(" revealing ")[1]
            .replaceAll("\\.", "");
        if (cards.contains(" and putting it in the hand")) {
            cards = cards.replaceAll(" and putting "
                                     + "it in the hand", "");
            result.add(getCards(cards));
            result.add(getCards(cards));
        } else {
            result.add(getCards(cards));
        }
        return result;
    }

    protected HashMap<String,Integer> getCardsForDiscardingMove(org.jsoup.nodes.Document doc) {
        String cards =  doc.text()
            .trim()
            .split(" discarding ")[1]
            .replaceAll("\\.", "")
            .replaceAll("the ([0-9]+)", "$1")
            .replaceAll("and the", "a");
        return getCards(cards);
    }

    protected HashMap<String,Integer> getCardsForDrawingMove(org.jsoup.nodes.Document doc) {
        String cards =  doc.text()
            .trim()
            .split(" drawing ")[1]
            .replaceAll("\\.", "")
            .replaceAll("from the Black"
                        + " Market deck", "");
        return getCards(cards);
    }

    protected HashMap<String,Integer> getCardsForTrashingMove(org.jsoup.nodes.Document doc) {
        String cards =  doc.data()
            .trim()
            .split(" trashing ")[1]
            .replaceAll("(\\</span\\>)"
                        + " (for|from).*\\.$", "$1");
        cards = Jsoup.parse(cards)
            .text()
            .replaceAll("\\.", "")
            .replaceAll("the", "a");
        return getCards(cards);
    }

    protected Document getGameLog() {
        org.jsoup.nodes.Document doc;
        ArrayList<GameTurn> turns = new ArrayList<>();
        boolean finished = false;
        int last = 0;
        int turn = 1;
        GameTurn t = new GameTurn(turn);
        String playername = "";

        //get the start of the game log
        doc = Jsoup.parse(this.log.searchLineWithString("(.*)'s turn 1(.*)"));

        while (!finished) {
            if (doc.text().matches("(.*)'s turn [0-9]+(.*)")) {
                int tempTurn = 0;
                if (!doc.text().contains("(possessed by")) {
                    tempTurn = Integer.parseInt(doc.text()
                                                .split("'s turn")[1]
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
                                            "plays",
                                            getCardsForPlaysMove(doc));
                                break;
                            case PLAYING:
                                t.insertMove(playername,
                                            countLevel(doc.text()),
                                            "playing",
                                            getCardsForPlayingMove(doc));
                                break;
                            case BUYS:
                                t.insertMove(playername,
                                            countLevel(doc.text()),
                                            "buys",
                                            getCardsForBuysMove(doc));
                                break;
                            case DRAWS_LAST_ACTION:
                                t.insertMove(playername,
                                            0,
                                            "draws",
                                            getCardsForDrawsMove(doc));
                                break;
                            case DRAWS_DISCARD:
                                t.insertMove(playername,
                                            countLevel(doc.text()),
                                            "draws",
                                            getCardsForDrawsAndDiscardMove(doc));
                                t.insertMove(playername,
                                            countLevel(doc.text()),
                                            "discard",
                                            getCardsForDrawsAndDiscardMove(doc));
                                break;
                            case DRAWING:
                                t.insertMove(playername,
                                            0,
                                            "drawing",
                                            getCardsForDrawingMove(doc));
                                break;
                            case GAINS:
                                t.insertMove(playername,
                                            countLevel(doc.text()),
                                            "gains",
                                            getCardsForGainsMove(doc));
                                break;
                            case GAINING:
                                t.insertMove(playername,
                                            countLevel(doc.text()),
                                            "gaining",
                                            getCardsForGainingMove(doc));
                                break;
                            case DISCARD_AND_GAINS:
                                Collection<HashMap<String,Integer>> discCards = getCardsForDiscardAndGainMove(doc);
                                if (discCards.size() > 1 ){
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                "discard",
                                                (HashMap) discCards.toArray()[0]);
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                "gain",
                                                (HashMap) discCards.toArray()[1]);
                                }else{
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                "discard",
                                                (HashMap) discCards.toArray()[0]);
                                }
                                break;
                            case TRASHES:
                                Collection<HashMap<String,Integer>> trashCards = getCardsForTrashMove(doc);
                                if (trashCards.size() > 1 ){
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                "trash",
                                                (HashMap) trashCards.toArray()[0]);
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                "gain",
                                                (HashMap) trashCards.toArray()[1]);
                                }else{
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                "trash",
                                                (HashMap) trashCards.toArray()[0]);
                                }
                                break;
                            case TRASHING:
                                t.insertMove(playername,
                                            countLevel(doc.text()),
                                            "trash",
                                            getCardsForTrashingMove(doc));
                                break;
                            case REVEALS:
                                Collection<HashMap<String,Integer>> revealCards = getCardsForRevealMove(doc);
                                if (revealCards.size() > 1 ){
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                "reveal",
                                                (HashMap) revealCards.toArray()[0]);
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                "trash",
                                                (HashMap) revealCards.toArray()[1]);
                                }else{
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                "reveal",
                                                (HashMap) revealCards.toArray()[0]);
                                }

                                break;
                            case REVEALING:
                                Collection<HashMap<String,Integer>> revealingCards = getCardsForRevealingMove(doc);
                                if (revealingCards.size() > 1 ){
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                "reavealing",
                                                (HashMap) revealingCards.toArray()[0]);
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                "putting",
                                                (HashMap) revealingCards.toArray()[1]);
                                }else{
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                "reavealing",
                                                (HashMap) revealingCards.toArray()[0]);
                                }
                                break;
                            case PUTTING:
                                t.insertMove(playername,
                                            countLevel(doc.text()),
                                            "putting",
                                            getCardsForPuttingMove(doc));
                                break;
                            case DISCARDING:
                                t.insertMove(playername,
                                            countLevel(doc.text()),
                                            "discarding",
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
        return new Document("log",turns);
    }

    protected Date getDateTime(){
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
            .trim()
            .replaceAll("'s turn [0-9]*(.*)", "").trim();
    }

    /**
     * creates a Document from a map.
     *
     * @param map lhe map to create the document
     * @return Document with the map data
     */
    protected Document hashToDoc(final HashMap<String, Integer> map) {
        Document temp = new Document();
        for (String x: map.keySet()) {
            temp.append(x, map.get(x));
        }
        return temp;
    }
}
