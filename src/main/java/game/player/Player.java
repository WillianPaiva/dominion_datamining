package game.player;
import java.util.ArrayList;
import java.util.HashMap;

public class Player {
    private String pl ;
    private HashMap<String,Integer> victoryCards = new HashMap<String,Integer>();
    private int points;
    private HashMap<String,Integer> deck = new HashMap<String,Integer>();
    private HashMap<String,Integer> firstHand = new HashMap<String,Integer>();
    private int turns;
    private ArrayList<String> opening = new ArrayList<String>();

    public Player(String pl){
        this.pl = pl ;
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

    public void insertDeck(int n , String s){
        deck.put(s,n);
    }


    public void setPoints(int n){
        this.points = n;
    }

    public void setTurns(int t){
        this.turns = t;
    }

    public void insertOpening(String s){
        this.opening.add(s);
    }

    public void insertFirstHand(int n , String s){
        this.firstHand.put(s,n);
    }

    public String toString(){
        return "\n"+pl + " " + points + "\n " + deck.toString() + "\n" + victoryCards.toString() + "\n first hand: " + firstHand.toString();
    }
}
