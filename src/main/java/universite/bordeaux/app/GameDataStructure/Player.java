package universite.bordeaux.app.GameDataStructure;

import org.bson.Document;

import universite.bordeaux.app.Constant.ConstantLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * player implementation .
 * @author Willian Ver Valen Paiva
 */
public class Player implements PlayerItf {
    /**
     * base elo.
     */
    public static final int BASE_ELO = 1000;
    /**
     * the player name.
     */
    private String playerName;
    /**
     * map of the player victory cards.
     */
    private HashMap<String, Integer> victoryCards;
    /**
     * the player amount of points.
     */
    private int points;
    /**
     * map of cards on the player deck.
     */
    private HashMap<String, Integer> deck;
    /**
     * map with the player first hand cards.
     */
    private HashMap<String, Integer> firstHand;
    /**
     * total number of turns.
     */
    private int turns;
    /**
     * opening cards of a player.
     */
    private ArrayList<String> opening;

    /**
     * the Elo of the player at the finish of this match
     * it is initiate a 1000 as the base Elo value.
     */
    private int gameElo = BASE_ELO;
    /**
     * the strategy used by the player.
     */
    private String strategy = "unknown";


    /**
     * Creates and returns the Player object based on a string (player name).
     * @param name the player name
     */
    public Player(final String name) {
        this.playerName = name;
        victoryCards = new HashMap<>();
        deck = new HashMap<>();
        firstHand = new HashMap<>();
        opening = new ArrayList<>();
    }

    /**
     * creates a player by loading the data from a Document object.
     * @param doc the Document to load
     */
    public Player(final Document doc) {
        this.playerName = doc.get(ConstantLog.NAME, String.class);
        this.points = doc.get(ConstantLog.POINTS, Integer.class);
        this.strategy = doc.get(ConstantLog.STRATEGY, String.class);
        this.turns = doc.get(ConstantLog.TURNS, Integer.class);
        this.gameElo = doc.get(ConstantLog.ELO, Integer.class);
        victoryCards = new HashMap<>();
        this.victoryCards = docToHash(doc.get(ConstantLog.VICTOYCARDS, Document.class));
        deck = new HashMap<>();
        this.deck = docToHash(doc.get(ConstantLog.DECK, Document.class));
        firstHand = new HashMap<>();
        this.firstHand = docToHash(doc.get(ConstantLog.FIRSTHAND, Document.class));
        opening = new ArrayList<>();
        this.opening = doc.get(ConstantLog.OPENING, ArrayList.class);

    }

    /**
     * Creates a Document object containing the information of
     * the Player object.
     * @return Document object
     */
    @Override
    public final Document toDoc() {
        return new Document()
                .append(ConstantLog.NAME, this.playerName)
                .append(ConstantLog.ELO, this.gameElo)
                .append(ConstantLog.POINTS, this.points)
                .append(ConstantLog.STRATEGY, this.strategy)
                .append(ConstantLog.TURNS, this.turns)
                .append(ConstantLog.VICTOYCARDS, hashToDoc(this.victoryCards))
                .append(ConstantLog.DECK, hashToDoc(this.deck))
                .append(ConstantLog.FIRSTHAND, hashToDoc(this.firstHand))
                .append(ConstantLog.OPENING, opening);
    }

    /**
     * converts Document to HashMap.
     * @param doc Document to convert.
     * @return HashMap
     */
    private HashMap<String, Integer> docToHash(final Document doc) {
        HashMap<String, Integer> temp = new HashMap<>();
        for (Map.Entry<String, Object> x: doc.entrySet()) {
            temp.put(x.getKey(), (int) x.getValue());
        }
        return temp;

    }

    /**
     * Converts HashMap to Document.
     * @param map the hashmap to be converted
     * @return Document
     */
    private Document hashToDoc(final HashMap<String, Integer> map) {
        Document temp = new Document();
        for (String x: map.keySet()) {
            temp.append(x, map.get(x));
        }
        return temp;
    }

    /**
     * Returns the player name.
     * @return String, player name
     */
    @Override
    public final String getPlayerName() {
        return this.playerName;
    }

    /**
     * @param quantity Card quantity
     * @param cardName Card name
     */
    @Override
    public final void insertVictoryCard(final int quantity,
                                        final String cardName) {
        victoryCards.put(cardName, quantity);
    }

    
    /** 
     * @param victoryCard HashMap<String, Integer>
     */
    public final void setVictoryCard(final HashMap<String, Integer> victoryCard){
    	this.victoryCards = victoryCard;
    }
    /**
     * adds a card to the players deck.
     * @param quantity number of cards of the same type
     * @param cardName name of the card
     */
    @Override
    public final void insertDeck(final int quantity,
                                 final String cardName) {
        deck.put(cardName, quantity);
    }
    
    /** 
     * @param deck HashMap<String, Integer>
     */
    public final void setDeck(final HashMap<String, Integer> deck){
    	this.deck = deck;
    }
    /**
     * Sets a score for the player.
     * @param pts amount of points
     */
    @Override
    public final void setPoints(final int pts) {
        this.points = pts;
    }
    /**
     * @param trs the number of turns played by a player.
     */
    @Override
    public final void setTurns(final int trs) {
        this.turns = trs;
    }



    /**
     * @param s opening cards
     */
    @Override
    public final void insertOpening(final String s) {
        this.opening.add(s);
    }

    /**
     * puts a card and it's amount in the first hand of the player.
     * @param quantity number of the specified cards
     * @param cardName a string with the name of the card
     */
    @Override
    public final void insertFirstHand(final int quantity,
                                      final String cardName) {
        this.firstHand.put(cardName, quantity);
    }
    
    public final void setFirstHand(final HashMap<String, Integer> firstHand){
    	this.firstHand = firstHand;
    }

    @Override
    public final String toString() {
        return "\n"
                + playerName
                + " "
                + points
                + "\n "
                + deck.toString()
                + "\n"
                + victoryCards.toString()
                + "\n first hand: "
                + firstHand.toString();
    }
}
