package universite.bordeaux.app.ReadersAndParser.Readers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.bson.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import universite.bordeaux.app.GameDataStructure.GameTurn;
import universite.bordeaux.app.GameDataStructure.Player;
import universite.bordeaux.app.GameDataStructure.PlayerItf;
import universite.bordeaux.app.ReadersAndParser.FileReader;
import universite.bordeaux.app.ReadersAndParser.LogParser;



/**
 *
 * @author ktagnaouti
 */
public abstract class LogReaderAbs implements LogReader {
    public static final int SEARCH_TRESEHOLD = 2;
    public static final int SUBSTRING_CONST = 3;
    private int version;
    private FileReader log;

    public LogReaderAbs(final String fileLog) {
       this.log = new FileReader(new File(fileLog));
    }

   public Document GetDoc() {
       return null;
   }


    /**
     * parses the first line and grab the list of winners.
     *
     * @return a list of winners present on the line
     */
    private Document getWinners() {
        org.jsoup.nodes.Document doc;

        ArrayList<String> result = new ArrayList<>();
        if (this.log.jumpline().contains("<title>")) {

            //creates a document with the first line of the log
            doc = Jsoup.parse(this.log.getLine());

            //find the match winner
            String[] win = doc.body().text().split("wins!");
            if (win[0].contains("rejoice in their shared victory!")) {
                String[] winners = win[0].split("rejoice in their shared victory!")[0].split("and");
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
    private Document getCardsGone() {
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
    private Document getTrash() {
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
    private Document getMarket() {
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
    private Document getPlayers() {
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
                            pl.setVictoryCard(getCards(victoryCards, SEARCH_TRESEHOLD));
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
    private PlayerItf createPlayer(org.jsoup.nodes.Document doc) {
        String name = doc.select("b")
                .text()
                .replaceAll("^#[0-9]* ", "");
        return new Player(name);
    }

    private void getFirstHand(ArrayList<PlayerItf> players) {
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
                        pla.setFirstHand(getCards(firstHand[1].replace(".)", "").split("and"),SEARCH_TRESEHOLD));
                    }
                }
                this.log.searchLineWithString("(.*)'s first hand: (.*)");
            }
        }
        this.log.rewindFile();
    }



    private HashMap<String, Integer> getCards(final String[] cardsToParse, final int  limit) {
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
    private static HashMap<String, Integer> getCards(String cards) {
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

    private int countLevel(String line) {
        int level = 0;
        String countLevel = line;
        while (countLevel.matches("\\.\\.\\.(.*)")) {
            countLevel = countLevel
                .substring(SUBSTRING_CONST);
            level++;
        }
        return level;
    }

    private HashMap<String, Integer> getCardsForBuysMove(org.jsoup.nodes.Document doc) {
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
    private Document getGameLog() {
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

                        //gets the plays move
                        if (LogElements.isPlaysMove(this.log.getLine())){
                            String cards = "";
                            //TODO
                            // if (playername.contains("plays")) {
                            //     cards = doc.text()
                            //             .trim()
                            //             .replace(playername
                            //                     + " plays ", "")
                            //             .replaceAll("\\.", "");
                            // } else {
                            cards = doc.text()
                                .split(" plays ")[1]
                                .replaceAll("\\.", "");
                            // }
                            cards = cards.replace("again", "")
                                    .replace("a third time", "");
                            int level = 0;
                            String countLevel = doc.text();
                            while (countLevel.matches("\\.\\.\\.(.*)")) {
                                countLevel = countLevel
                                        .substring(SUBSTRING_CONST);
                                level++;
                            }
                            String[] playedCards;
                            if (cards.contains(",")) {
                              cards = cards.replace("and", "");
                              playedCards = cards.split(",");
                            }else{
                              playedCards = cards.split(" and ");
                            }
                            t.insertMove(playername,
                                    level,
                                    "plays",
                                    getCards(playedCards, 4));
                            break;

                            //gets the buys move
                        } else if (LogElements.isBuysMove(this.log.getLine())) {
                            String cards = "";
                            //TODO
                            // if (playername.contains("buys")) {
                            //     cards = doc.text()
                            //             .trim()
                            //             .replace(playername + " buys ", "")
                            //             .replaceAll("\\.", "");
                            // } else {
                                cards = doc.text()
                                        .split(" buys ")[1]
                                        .replaceAll("\\.", "");
                            // }
                            int level = 0;
                            String countLevel = doc.text();
                            while (countLevel.matches("\\.\\.\\.(.*)")) {
                                countLevel = countLevel
                                        .substring(SUBSTRING_CONST);
                                level++;
                            }
                            t.insertMove(playername,
                                    level,
                                    "buys",
                                    getCards(cards));
                            break;

                            //gets the draws on the finish of the turn
                        } else if (LogElements.isDrawsLastAction(this.log.getLine())) {
                            String cards =  doc.text()
                                    .trim()
                                    .split(" draws: ")[1]
                                    .replaceAll("\\.\\)", "");
                            t.insertMove(playername,
                                    0,
                                    "draws",
                                    getCards(cards));
                            break;

                            //gets the move that consists
                            // in draw and discard a same card
                        } else if (LogElements.isMoveDrawDiscardCard(this.log.getLine())) {
                            String cards =  doc.text()
                                    .trim()
                                    .split(" draws and discard ")[1]
                                    .replaceAll("\\.", "");
                            int level = 0;
                            String countLevel = doc.text();
                            while (countLevel.matches("\\.\\.\\.(.*)")) {
                                countLevel = countLevel
                                        .substring(SUBSTRING_CONST);
                                level++;
                            }
                            t.insertMove(playername,
                                    level,
                                    "draws",
                                    getCards(cards));
                            t.insertMove(playername,
                                    level,
                                    "discard",
                                    getCards(cards));
                            break;

                            //gets the gains action
                        } else if (LogElements.isGainsAction(this.log.getLine())) {
                            String cards =  doc.text()
                                    .trim()
                                    .split(" gains ")[1]
                                    .replaceAll("\\.", "")
                                    .replaceAll(" on top of the deck", "")
                                    .replaceAll(" on the deck", "")
                                    .replaceAll(" to replace it", "");
                            int level = 0;
                            String countLevel = doc.text();
                            while (countLevel.matches("\\.\\.\\.(.*)")) {
                                countLevel = countLevel
                                        .substring(SUBSTRING_CONST);
                                level++;
                            }
                            t.insertMove(playername,
                                    level,
                                    "gains",
                                    getCards(cards));
                            break;
                            //gets the double action discards and gains
                        } else if (LogElements.isActionDiscardsAndGains(this.log.getLine())) {
                            String cards =  doc.text()
                                    .trim()
                                    .split(" discards ")[1]
                                    .replaceAll("\\.", "")
                                    .replaceAll("on the deck", "");
                            if (cards.contains(" and gains ")) {
                                String[] actionList;
                                actionList = cards.split(" and gains ");
                                int level = 0;
                                String countLevel = doc.text();
                                while (countLevel.matches("\\.\\.\\.(.*)")) {
                                    countLevel = countLevel
                                            .substring(SUBSTRING_CONST);
                                    level++;
                                }
                                t.insertMove(playername,
                                        level,
                                        "discards",
                                        getCards(actionList[0]));
                                t.insertMove(playername,
                                        level,
                                        "gains",
                                        getCards(actionList[1]));
                            } else {
                                int level = 0;
                                String countLevel = doc.text();
                                while (countLevel.matches("\\.\\.\\.(.*)")) {
                                    countLevel = countLevel
                                            .substring(SUBSTRING_CONST);
                                    level++;
                                }
                                t.insertMove(playername,
                                        level,
                                        "discards",
                                        getCards(cards));
                            }
                            break;
                            //get trashes
                        } else if (LogElements.isTrashes(this.log.getLine())) {
                            String cards =  doc.text()
                                    .trim()
                                    .split(" trashes ")[1]
                                    .replaceAll("and gets (.*)\\.", "")
                                    .replaceAll("\\.", "");
                            if (cards.contains("gaining")) {
                                String[] actionList = cards.split(", gaining ");
                                int level = 0;
                                String countLevel = doc.text();
                                while (countLevel.matches("\\.\\.\\.(.*)")) {
                                    countLevel = countLevel
                                            .substring(SUBSTRING_CONST);
                                    level++;
                                }
                                t.insertMove(playername,
                                        level,
                                        "trashes",
                                        getCards(actionList[0]));
                                t.insertMove(playername,
                                        level,
                                        "gains",
                                        getCards(actionList[1]
                                                .replaceAll(" in hand", "")));
                            } else {
                                int level = 0;
                                String countLevel = doc.text();
                                while (countLevel.matches("\\.\\.\\.(.*)")) {
                                    countLevel = countLevel
                                            .substring(SUBSTRING_CONST);
                                    level++;
                                }
                                t.insertMove(playername,
                                        level,
                                        "trashes",
                                        getCards(cards));
                            }
                            break;
                            // get reveals
                        } else if (LogElements.isReveals(this.log.getLine())) {
                            String cards =  doc.text()
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
                            int level = 0;
                            String countLevel = doc.text();
                            while (countLevel.matches("\\.\\.\\.(.*)")) {
                                countLevel = countLevel
                                        .substring(SUBSTRING_CONST);
                                level++;
                            }
                            t.insertMove(playername,
                                    level,
                                    "reveals",
                                    getCards(cards));
                            if (trashes) {
                                t.insertMove(playername,
                                        level,
                                        "trashes",
                                        getCards(cards));
                            }
                            break;


                            //get the trashing action
                        } else if (LogElements.isTrashingAction(this.log.getLine())) {
                            String cards =  this.log
                                    .getLine()
                                    .trim()
                                    .split(" trashing ")[1]
                                    .replaceAll("(\\</span\\>)"
                                            + " (for|from).*\\.$", "$1");
                            cards = Jsoup.parse(cards)
                                    .text()
                                    .replaceAll("\\.", "")
                                    .replaceAll("the", "a");
                            int level = 0;
                            String countLevel = doc.text();
                            while (countLevel.matches("\\.\\.\\.(.*)")) {
                                countLevel = countLevel
                                        .substring(SUBSTRING_CONST);
                                level++;
                            }
                            t.insertMove(playername,
                                    level,
                                    "trashes",
                                    getCards(cards));
                            break;

                        } else if (LogElements.isRevealingAction(this.log.getLine())) {
                            String cards =  doc.text()
                                    .trim()
                                    .split(" revealing ")[1]
                                    .replaceAll("\\.", "");
                            int level = 0;
                            String countLevel = doc.text();
                            while (countLevel.matches("\\.\\.\\.(.*)")) {
                                countLevel = countLevel
                                        .substring(SUBSTRING_CONST);
                                level++;
                            }
                            if (cards.contains(" and putting it in the hand")) {
                                cards = cards.replaceAll(" and putting "
                                        + "it in the hand", "");
                                t.insertMove(playername,
                                        level,
                                        "revealing",
                                        getCards(cards));
                                t.insertMove(playername,
                                        level,
                                        "putting",
                                        getCards(cards));
                            } else {
                                t.insertMove(playername,
                                        level,
                                        "revealing",
                                        getCards(cards));
                            }
                            break;
                        } else if (LogElements.isPuttingAction(this.log.getLine())) {
                            String cards =  doc.text()
                                    .trim()
                                    .split(" putting ")[1]
                                    .replaceAll("\\.", "")
                                    .replaceAll("into the hand", "");
                            int level = 0;
                            String countLevel = doc.text();
                            while (countLevel.matches("\\.\\.\\.(.*)")) {
                                countLevel = countLevel
                                        .substring(SUBSTRING_CONST);
                                level++;
                            }
                            t.insertMove(playername,
                                    level,
                                    "putting",
                                    getCards(cards));
                            break;
                        } else if (LogElements.isGainingAction(this.log.getLine())) {
                            String cards =  doc.text()
                                    .trim()
                                    .split(" gaining ")[1]
                                    .replaceAll("\\.", "")
                                    .replaceAll("another", "a");
                            int level = 0;
                            String countLevel = doc.text();
                            while (countLevel.matches("\\.\\.\\.(.*)")) {
                                countLevel = countLevel
                                        .substring(SUBSTRING_CONST);
                                level++;
                            }
                            t.insertMove(playername,
                                    level,
                                    "gains",
                                    getCards(cards));
                            break;
                        } else if (LogElements.isDiscardingAction(this.log.getLine())) {
                            String cards =  doc.text()
                                    .trim()
                                    .split(" discarding ")[1]
                                    .replaceAll("\\.", "")
                                    .replaceAll("the ([0-9]+)", "$1")
                                    .replaceAll("and the", "a");
                            int level = 0;
                            String countLevel = doc.text();
                            while (countLevel.matches("\\.\\.\\.(.*)")) {
                                countLevel = countLevel
                                        .substring(SUBSTRING_CONST);
                                level++;
                            }
                            t.insertMove(playername,
                                    level,
                                    "discarding",
                                    getCards(cards));
                            break;
                        } else if (LogElements.isPlayingAction(this.log.getLine())) {
                            String cards =  doc.text()
                                    .trim()
                                    .split(" playing ")[1]
                                    .replaceAll("\\.", "");
                            int level = 0;
                            String countLevel = doc.text();
                            while (countLevel.matches("\\.\\.\\.(.*)")) {
                                countLevel = countLevel
                                        .substring(SUBSTRING_CONST);
                                level++;
                            }
                            t.insertMove(playername,
                                    level,
                                    "playing",
                                    getCards(cards));
                            break;
                        } else if (LogElements.isDrawingAction(this.log.getLine())) {
                            String cards =  doc.text()
                                    .trim()
                                    .split(" drawing ")[1]
                                    .replaceAll("\\.", "")
                                    .replaceAll("from the Black"
                                            + " Market deck", "");
                            int level = 0;
                            String countLevel = doc.text();
                            while (countLevel.matches("\\.\\.\\.(.*)")) {
                                countLevel = countLevel
                                        .substring(SUBSTRING_CONST);
                                level++;
                            }
                            t.insertMove(playername,
                                    level,
                                    "draws",
                                    getCards(cards));
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



    /**
     * get the player name from the string.
     * @param s string
     * @return string player name
     */
    private static String turnGetPlayer(final String s) {
        return s.trim()
                .replaceAll("^—", "")
                .trim()
                .replaceAll("'s turn [0-9]*(.*)", "").trim();
    }

    /**
     * creates a Document from a map.
     *
     * @param map the map to create the document
     * @return Document with the map data
     */
    private Document hashToDoc(final HashMap<String, Integer> map) {
        Document temp = new Document();
        for (String x: map.keySet()) {
            temp.append(x, map.get(x));
        }
        return temp;
    }

}
