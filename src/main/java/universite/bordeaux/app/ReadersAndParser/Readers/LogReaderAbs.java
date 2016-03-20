package universite.bordeaux.app.ReadersAndParser.Readers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.bson.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import universite.bordeaux.app.GameDataStructure.Player;
import universite.bordeaux.app.GameDataStructure.PlayerItf;
import universite.bordeaux.app.ReadersAndParser.FileReader;



/**
 *
 * @author ktagnaouti
 */
public abstract class LogReaderAbs implements LogReader {
    public static final int SEARCH_TRESEHOLD = 2;
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
                result = getCards(trash);
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
                            pl.setVictoryCard(getCards(victoryCards));
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
                        pl.setDeck(getCards(deck));
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
    private PlayerItf createPlayer(org.jsoup.nodes.Document doc){
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
                        pla.setFirstHand(getCards(firstHand[1].replace(".)", "").split("and")));
                    }
                }
                this.log.searchLineWithString("(.*)'s first hand: (.*)");
            }
        }
        this.log.rewindFile();
    }



    private HashMap<String,Integer> getCards( String[] cardsToParse) {
        HashMap<String,Integer> cards = new HashMap<>();
        for (String x : cardsToParse) {

            x = x.trim();

            String[] temp = x.split(" ", SEARCH_TRESEHOLD);
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
