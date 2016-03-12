package universite.bordeaux.app.ReadersAndParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import universite.bordeaux.app.GameDataStructure.Player;
import universite.bordeaux.app.GameDataStructure.PlayerItf;

import java.util.ArrayList;
import java.util.HashMap;





/**
 * that is utility class to read a log file and parse its information on demand.
 *
 * @author Willian Ver Valen Paiva
 */
public final class HeaderParser {
    /**
     * the search tresehold for spliting lines.
     */
    public static final int SEARCH_TRESEHOLD = 3;

    /**
     * private and empty constructor.
     */
    private HeaderParser() { }


	/**
   * parses the first line and grab the list of winners.
   *
   * @param reader the FileReader object to be readed.
   * @return a list of winners present on the line
	 */
    public static ArrayList<String> getWinners(final FileReader reader) {
        Document doc;
        ArrayList<String> result = new ArrayList<>();
        if (reader.jumpline().contains("<title>")) {

            //creates a document with the first line of the log
            doc = Jsoup.parse(reader.getLine());

            //get the game number
            String title = doc.title();
            String[] parts = title.split("#");
            // game.setGameNumber(Integer.parseInt(parts[1]));

            //find the match winner
            String[] win = doc.body().text().split("wins!");
            if (win[0].contains("rejoice in their shared victory!")) {
                String[] winners = win[0]
                        .split("rejoice in their shared victory!")[0]
                        .split("and");
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
        reader.rewindFile();
        return result;
    }

    /**
     * parses the line:
     * ex:  All <span class=card-victory>Provinces</span> are gone.
     * and look for the list of cards that are listed as gonne.
     * @param reader the FileReader object to be read
     * @return a list of cards present on the line parsed
     */
    public static ArrayList<String> getCardsGone(final FileReader reader) {
        Document doc;
        ArrayList<String> cardsGone = new ArrayList<>();
        if (reader.jumpline().contains("<title>")) {
            //jumps to the next line
            doc = Jsoup.parse(reader.jumpline());

            //finds how the game finished by getting the empty piles
            Elements links = doc.select("span");
            for (Element link : links) {
                cardsGone.add(link.text());
            }
        }
        reader.rewindFile();
        return cardsGone;
    }

    /**
     * parses the line:
     * ex: trash: a <span class=card-treasure>Silver</span>
     * and parse a list of cards that are in the trash.
     * @param reader the FileReader object to be read
     * @return a map with the cards and quantity of the trashed parsed
     */
    public static HashMap<String, Integer> getTrash(final FileReader reader) {
        Document doc;
        HashMap<String, Integer> result = new HashMap<>();
        //look for the line that describes the trash
        if (reader.searchLineWithString("trash: (.*)") != null) {
            doc = Jsoup.parse(reader.getLine());

            //parse the line trash and add the list to the game object
            if (!doc.text().contains("nothing")) {
                String[] trash = doc.text()
                        .replace("trash: ", "")
                        .replace("and", "")
                        .split(",");
                for (String x: trash) {
                    x = x.trim();
                    String[] cards = x.split(" ", SEARCH_TRESEHOLD);
                    int qty;
                    try {
                        qty = Integer.parseInt(cards[0]);
                    } catch (NumberFormatException e) {
                        qty = 1;
                    }
                    String card = cards[1];
                    result.put(card, qty);
                }
            }
        }
        //return the file pointer to the beginning of the file
        reader.rewindFile();
        return result;
    }

    /**
     * get all the cards that are in the (cards in suply) list.
     * @param reader the FileReader object to be read
     * @return a list of cards presents on the line parsed
     */
    public static ArrayList<String> getMarket(final FileReader reader) {
        Document doc;
        ArrayList<String> market = new ArrayList<>();
        if (reader.jumpline().contains("<title>")) {
            //jumps 2 line
            reader.jumpline();
            reader.jumpline();

            doc = Jsoup.parse(reader.jumpline());

            //get all the cards available on market
            Elements links = doc.select("span");
            for (Element link : links) {
                market.add(link.text());
            }
        }
        reader.rewindFile();
        return market;
    }
    /**
     * reads the player section of the log
     * and parses each player present on the log as a Player object.
     *
     * @param reader the FileReader object to be read
     * @return a list of Players parsed from the log
     */
    public static ArrayList<PlayerItf> getPlayers(final FileReader reader) {
        boolean start = true;
        Document doc;
        ArrayList<PlayerItf> players = new ArrayList<>();
        //jump to the players section of the log
        if (reader.searchLineWithString("(.*)----------------------(.*)")
                != null) {
            doc = Jsoup.parse(reader.getLine());
            if (reader.getLine().contains("----------------------")
                    && start) {
                start = false;
                //jumps to the first player
                reader.jumpline();
                doc = Jsoup.parse(reader.jumpline());
                while (!reader.getLine().contains("----------------------")) {
                    //create the player
                    PlayerItf pl;
                    String name;
                    if (doc.select("b").text().contains("points")) {
                        name = doc.select("b").text().split(":")[0];
                    } else {
                        name = doc.select("b")
                                .text()
                                .replaceAll("^#[0-9]* ", "");
                    }
                    pl = new Player(name);
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
                            //insert the victorycards on the player object
                            for (String x: victoryCards) {
                                x = x.trim();
                                String[] cards = x.split(" ", SEARCH_TRESEHOLD);
                                int qty;
                                try {
                                    qty = Integer.parseInt(cards[0]);
                                } catch (NumberFormatException e) {
                                    qty = 1;
                                }
                                String card = cards[1];
                                pl.insertVictoryCard(qty, card);
                            }
                        }
                        //get the turns
                        pl.setTurns(Integer.parseInt(firstBreak[1]
                                .split(";")[1]
                                .replace(" turns", "")
                                .replace(" ", "")));
                    }
                    //get next line
                    doc = Jsoup.parse(reader.jumpline());
                    //get opening cards
                    Elements links = doc.select("span");
                    for (Element link : links) {
                        pl.insertOpening(link.text());
                    }
                    //next line
                    doc = Jsoup.parse(reader.jumpline());
                    //get deck cards
                    if (!doc.text().contains("0 cards")) {
                        String[] deck = doc.text()
                                .split("\\[[0-9]* cards\\]")[1]
                                .split(",");
                        for (String x: deck) {
                            x = x.trim();
                            String[] cards = x.split(" ", SEARCH_TRESEHOLD);
                            int qty;
                            try {
                                qty = Integer.parseInt(cards[0]);
                            } catch (NumberFormatException e) {
                                qty = 1;
                            }
                            String card = cards[1];
                            pl.insertDeck(qty, card);
                        }
                    }
                    players.add(pl);
                    //jumps 1 line
                    reader.jumpline();
                    doc = Jsoup.parse(reader.jumpline());
                }
            }

        }
        //return the file pointer to the beginning of the file
        reader.rewindFile();
        if (reader.searchLineWithString("(.*)'s first hand: (.*)")
                != null) {
            for (int x = 0; x < players.size(); x++) {
                doc = Jsoup.parse(reader.getLine());
                String[] firstHand = doc.text().split("'s first hand: ");
                for (String y: firstHand[1].replace(".)", "").split("and")) {
                    y = y.trim();
                    String[] cards = y.split(" ", SEARCH_TRESEHOLD);
                    int qty;
                    try {
                        qty = Integer.parseInt(cards[0]);
                    } catch (NumberFormatException e) {
                        qty = 1;
                    }
                    String card = cards[1];
                    for (PlayerItf pla: players) {
                        if (pla.getPlayerName()
                                .equals(firstHand[0]
                                        .substring(1))) {
                            pla.insertFirstHand(qty, card);
                        }
                    }
                }
                reader.searchLineWithString("(.*)'s first hand: (.*)");
            }
        }
        reader.rewindFile();
        return players;
    }
}
