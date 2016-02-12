package universite.bordeaux.app.game.player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;

public class Player {
    private String pl ;
    private HashMap<String,Integer> victoryCards = new HashMap<String,Integer>();
    private int points;
    private HashMap<String,Integer> deck = new HashMap<String,Integer>();
    private HashMap<String,Integer> firstHand = new HashMap<String,Integer>();
    private int turns;
    private ArrayList<String> opening = new ArrayList<String>();
    private int GameElo  = 1000;


    public Player(String pl){
        this.pl = pl ;
    }

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

    private HashMap<String,Integer> doctohash(Document doc){
        HashMap<String,Integer> temp = new HashMap<String,Integer>();
        for(Map.Entry<String,Object> x: doc.entrySet()){
            temp.put(x.getKey(),(int)x.getValue());
        }
        return temp;

    }

    private Document hashtodoc(HashMap<String,Integer> map){
        Document temp = new Document();
        for(String x: map.keySet()){
            temp.append(x, map.get(x));
        }
        return temp;
    }

    public String getPlayerName(){
        return this.pl;
    }

    public void setPlayer(){
        this.pl = pl;
    }

    public void insertVictoryCard(int n , String s){
        victoryCards.put(s,n);
    }
    public HashMap<String,Integer> getVictoryCards(){
        return this.victoryCards;
    }

    public void insertDeck(int n , String s){
        deck.put(s,n);
    }
    public HashMap<String,Integer> getDeck(){
        return this.deck;
    }


    public void setPoints(int n){
        this.points = n;
    }
    public int getPoints(){
        return this.points;
    }

    public void setTurns(int t){
        this.turns = t;
    }

	/**
	 * @return the gameElo
	 */
  public int getGameElo() {

      return GameElo;
	}

	/**
	 * @param gameElo the gameElo to set
	 */
	public void setGameElo(int gameElo) {
		GameElo = gameElo;
	}

	public int getTurns(){
        return this.turns;
    }

    public void insertOpening(String s){
        this.opening.add(s);
    }

    public void insertFirstHand(int n , String s){
        this.firstHand.put(s,n);
    }
    public HashMap<String,Integer> getFirstHand(){
        return this.firstHand;
    }

    public String toString(){
        return "\n"+pl + " " + points + "\n " + deck.toString() + "\n" + victoryCards.toString() + "\n first hand: " + firstHand.toString();
    }
}
