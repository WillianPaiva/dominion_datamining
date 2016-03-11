package universite.bordeaux.app.GameDataStructure;

import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Player implements PlayerItf{
    private String pl ;
    private HashMap<String,Integer> victoryCards = new HashMap<>();
    private int points;
    private HashMap<String,Integer> deck = new HashMap<>();
    private HashMap<String,Integer> firstHand = new HashMap<>();
    private int turns;
    private ArrayList<String> opening = new ArrayList<>();
    private int GameElo  = 1000;


    /**
     * Creates and returns the Player object based on a string (player name)
     * @param pl
     */
    public Player(String pl){
        this.pl = pl ;
    }

    /**
     * Loads player data from a Document object
     * @param doc
     */
    public Player(Document doc){
        this.pl = doc.get("name",String.class);
        this.points = doc.get("points", Integer.class);
        this.turns = doc.get("turns", Integer.class);
        this.GameElo = doc.get("elo", Integer.class);
        this.victoryCards = doctohash(doc.get("victorycards",Document.class));
        this.deck = doctohash(doc.get("deck",Document.class));
        this.firstHand = doctohash(doc.get("firsthand",Document.class));
        this.opening = doc.get("opening",ArrayList.class);

    }

    /**
     * Creates a Document object containing the information of the Player object
     * @return Document object
     */
    @Override
    public Document toDoc(){
        return new Document()
            .append("name",this.pl)
            .append("elo", this.GameElo)
            .append("points",this.points)
            .append("turns",this.turns)
            .append("victorycards",hashtodoc(this.victoryCards))
            .append("deck",hashtodoc(this.deck))
            .append("firsthand",hashtodoc(this.firstHand))
            .append("opening",opening);
    }

    /**
     * converts Document to HashMap
     * @param doc
     * @return HashMap
     */
    private HashMap<String,Integer> doctohash(Document doc){
        HashMap<String,Integer> temp = new HashMap<>();
        for(Map.Entry<String,Object> x: doc.entrySet()){
            temp.put(x.getKey(),(int)x.getValue());
        }
        return temp;

    }

    /**
     * Converts HashMap to Document
     * @param map
     * @return Document
     */
    private Document hashtodoc(HashMap<String,Integer> map){
        Document temp = new Document();
        for(String x: map.keySet()){
            temp.append(x, map.get(x));
        }
        return temp;
    }

    /**
     * Returns the player name
     * @return String, player name
     */
    @Override
    public String getPlayerName(){
        return this.pl;
    }

    /**
     * @param quantity Card quantity
     * @param cardName Card name
     */
    @Override
    public void insertVictoryCard(int quantity , String cardName){
        victoryCards.put(cardName, quantity);
    }
    /**
     * return the cards used to count the victoy points such as Estate, Duchy, Province, Colony
     * @return HashMap with the cards and their amount
     */
    @Override
    public HashMap<String,Integer> getVictoryCards(){
        return this.victoryCards;
    }

    /**
     * adds a card to the players deck
     * @param quantity number of cards of the same type
     * @param cardName name of the card
     */
    @Override
    public void insertDeck(int quantity , String cardName){
        deck.put(cardName , quantity);
    }
    /**
     * @return The list of cards and their amount in a players deck (cards owned)
     */
    @Override
    public HashMap<String,Integer> getDeck(){
        return this.deck;
    }

    /**
     * Sets a score for the player
     * @param points amount of points
     */
    @Override
    public void setPoints(int points){
        this.points = points;
    }
    /**
     * @return A value containing the player's points
     */
    @Override
    public int getPoints(){
        return this.points;
    }

    /**
     * @param turns the number of urns played by a player
     */
    @Override
    public void setTurns(int turns){
        this.turns = turns;
    }


/**
 * @return players elo
 */
    @Override
    public int getGameElo() {

      return GameElo;
	}

	/**
	 * @param gameElo the gameElo to set
	 */
    @Override
	public void setGameElo(int gameElo) {
		GameElo = gameElo;
	}

	/**
	 * @return the turns played by a player
	 */
    @Override
	public int getTurns(){
        return this.turns;
    }

    /**
     * @param s opening cards
     */
    @Override
    public void insertOpening(String s){
        this.opening.add(s);
    }

    /**
     * puts a card and it's amount in the first hand of the player
     * @param quantity number of the specified cards
     * @param cardName a string with the name of the card
     */
    @Override
    public void insertFirstHand(int quantity , String cardName){
        this.firstHand.put(cardName, quantity);
    }
    /**
     * @return a HashMap containing the first hand of the player (cards and amount)
     */
    @Override
    public HashMap<String,Integer> getFirstHand(){
        return this.firstHand;
    }

    @Override
    public String toString(){
        return "\n"+pl + " " + points + "\n " + deck.toString() + "\n" + victoryCards.toString() + "\n first hand: " + firstHand.toString();
    }
}
