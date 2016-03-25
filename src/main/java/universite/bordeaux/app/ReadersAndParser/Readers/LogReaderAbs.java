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
import universite.bordeaux.app.ReadersAndParser.FileReader;



/**
 *
 */
public abstract class LogReaderAbs {
    public static final int SEARCH_TRESEHOLD = 2;
    public static final int SUBSTRING_CONST = 3;
    protected int version;
    protected FileReader log;

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
    protected ArrayList<String> getWinners() {
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
    protected ArrayList<String> getCardsGone() {
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
        return cardsGone;
    }

    /**
     * parses the line:
     * ex: trash: a <span class=card-treasure>Silver</span>
     * and parse a list of cards that are in the trash.
     * @return a map with the cards and quantity of the trashed parsed
     */
    protected HashMap<String, Integer> getTrash() {
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
    protected ArrayList<String> getMarket() {
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
        return market;
    }

    /**
     * reads the player section of the log
     * and parses each player present on the log as a Player object.
     *
     * @return a list of Players parsed from the log
     */
    protected ArrayList<Document> getPlayers() {
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
                        String[] firstBreak;
                        if(pl.getPlayerName().contains("points")){
                            firstBreak = doc.text().replaceAll(pl.getPlayerName(),"playername").split("points");
                        }else{
                            firstBreak = doc.text().split("points");
                        }

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
                        if(!doc.text().contains("resigned")){
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
                        }else{
                            pl.setTurns(Integer.parseInt(doc.text()
                                                         .split(";")[1]
                                                         .replace(" turns", "")
                                                         .replace(" ", "")));
                        }
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
        return players.stream().map(PlayerItf::toDoc)
            .collect(Collectors
                     .toCollection(ArrayList::new));
    }

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
     * obtains a string and return a hash map with the cards and quantity.
     * @param cards string
     * @return hashmap
     */
    protected HashMap<String, Integer> obtainCards(String cards) {
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

    protected HashMap<String, Integer> getCardsForBuysMove(org.jsoup.nodes.Document doc, String playername) {
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

    protected HashMap<String,Integer> getCardsForPlaysMove(org.jsoup.nodes.Document doc, String playername) {
        String cards = "";
        if (playername.contains("plays") && doc.text().contains(playername)) {
            cards = doc.text()
                .trim()
                .replace(playername
                         + " plays ", "")
                .replaceAll("\\.", "");
        }else{
            cards = doc.text() .split(" plays ")[1] .replaceAll("\\.", "");
        }

        cards = cards.replace("again", "") .replace("a third time", "");
        return obtainCards(cards);
    }

    protected HashMap<String,Integer> getCardsForDrawsMove(org.jsoup.nodes.Document doc) {
        String cards = doc.text().trim().split(" draws: ")[1]
            .replaceAll("\\.\\)", "");
        return obtainCards(cards);
    }

    protected HashMap<String,Integer> getCardsForDrawsAndDiscardMove(org.jsoup.nodes.Document doc) {
        String cards = doc.text()
            .trim()
            .split(" draws and discard ")[1]
            .replaceAll("\\.", "");
        return obtainCards(cards);
    }

    protected HashMap<String,Integer> getCardsForGainsMove(org.jsoup.nodes.Document doc) {
        String cards =  doc.text()
            .trim()
            .split(" gains ")[1]
            .replaceAll("\\.", "")
            .replaceAll(" on top of the deck", "")
            .replaceAll(" on the deck", "")
            .replaceAll(" to replace it", "");
        return obtainCards(cards);
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
            result.add(obtainCards(actionList[0]));
            result.add(obtainCards(actionList[1]));
        } else {
            result.add(obtainCards(cards));
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
        }else if(cards.contains("giving")){
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

    protected Collection<HashMap<String,Integer>> getCardsForTrashMove(org.jsoup.nodes.Document doc) {
        Collection<HashMap<String,Integer>> result = new ArrayList<>();
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

    protected HashMap<String,Integer> getCardsForPuttingMove(org.jsoup.nodes.Document doc) {
        String cards =  doc.text()
            .trim()
            .split(" putting ")[1]
            .replaceAll("\\.", "")
            .replaceAll("into the hand", "");
        return obtainCards(cards);
    }

    protected HashMap<String,Integer> getCardsForGainingMove(org.jsoup.nodes.Document doc) {
        String cards =  doc.text()
            .trim()
            .split(" gaining ")[1]
            .replaceAll("\\.", "")
            .replaceAll("another", "a");
        return obtainCards(cards);
    }

    protected HashMap<String,Integer> getCardsForPlayingMove(org.jsoup.nodes.Document doc) {
        String cards =  doc.text()
            .trim()
            .split(" playing ")[1]
            .replaceAll("\\.", "");
        return obtainCards(cards);
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
            result.add(obtainCards(cards));
            result.add(obtainCards(cards));
        } else {
            result.add(obtainCards(cards));
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
        return obtainCards(cards);
    }

    protected HashMap<String,Integer> getCardsForDrawingMove(org.jsoup.nodes.Document doc) {
        String cards =  doc.text()
            .trim()
            .split(" drawing ")[1]
            .replaceAll("\\.", "")
            .replaceAll("from the Black"
                        + " Market deck", "");
        return obtainCards(cards);
    }

    protected HashMap<String,Integer> getCardsForTrashingMove(String doc) {
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

    protected ArrayList<Document> getGameLog() {
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
            if (doc.text().matches("(.*)'s turn [0-9]*(.*)")) {
                int tempTurn = 0;
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
                                             getCardsForPlaysMove(doc,playername));
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
                                             getCardsForBuysMove(doc,playername));
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
                                Collection<HashMap<String,Integer>> discCards = getCardsForDiscardAndGainMove(doc);
                                if (discCards.size() > 1 ){
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                ConstantLog.DISCARD,
                                                (HashMap) discCards.toArray()[0]);
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                ConstantLog.GAINS,
                                                (HashMap) discCards.toArray()[1]);
                                }else{
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                ConstantLog.DISCARD,
                                                (HashMap) discCards.toArray()[0]);
                                }
                                break;
                            case TRASHES:
                                Collection<HashMap<String,Integer>> trashCards = getCardsForTrashMove(doc);
                                if (trashCards.size() > 1 ){
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                ConstantLog.TRASH,
                                                (HashMap) trashCards.toArray()[0]);
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                ConstantLog.GAINS,
                                                (HashMap) trashCards.toArray()[1]);
                                }else{
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                ConstantLog.TRASH,
                                                (HashMap) trashCards.toArray()[0]);
                                }
                                break;
                            case TRASHING:
                                t.insertMove(playername,
                                            countLevel(doc.text()),
                                            ConstantLog.TRASH,
                                            getCardsForTrashingMove(log.getLine()));
                                break;
                            case REVEALS:
                                Collection<HashMap<String,Integer>> revealCards = getCardsForRevealMove(doc);
                                if (revealCards.size() > 1 ){
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                ConstantLog.REVEAL,
                                                (HashMap) revealCards.toArray()[0]);
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                ConstantLog.TRASH,
                                                (HashMap) revealCards.toArray()[1]);
                                }else{
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                ConstantLog.REVEAL,
                                                (HashMap) revealCards.toArray()[0]);
                                }

                                break;
                            case REVEALING:
                                Collection<HashMap<String,Integer>> revealingCards = getCardsForRevealingMove(doc);
                                if (revealingCards.size() > 1 ){
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                ConstantLog.REVEAL,
                                                (HashMap) revealingCards.toArray()[0]);
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                ConstantLog.PUTTING,
                                                (HashMap) revealingCards.toArray()[1]);
                                }else{
                                    t.insertMove(playername,
                                                countLevel(doc.text()),
                                                ConstantLog.REVEAL,
                                                (HashMap) revealingCards.toArray()[0]);
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

    protected Document toDoc() {
        return new Document()
            .append(ConstantLog.DATE, this.getDateTime())
            .append(ConstantLog.FILENAME, this.log.getName())
            .append(ConstantLog.ELOGAP, 0)
            .append(ConstantLog.WINNERS, this.getWinners())
            .append(ConstantLog.CARDSGONNE, this.getCardsGone())
            .append(ConstantLog.MARKET, this.getMarket())
            .append(ConstantLog.TRASH, this.getTrash())
            .append(ConstantLog.PLAYERS, this.getPlayers())
            .append(ConstantLog.LOG, this.getGameLog());
    }

    public void close(){
        log.close();
    }

}
