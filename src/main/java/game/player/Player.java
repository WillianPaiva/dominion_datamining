package game.player;
import java.util.ArrayList;
import java.util.HashMap;

public class Player {
    private String pl ;
    private HashMap<String,Integer> victoryCards = new HashMap<String,Integer>();
    private int points;
    private HashMap<String,Integer> deck = new HashMap<String,Integer>();
    private int turns;
    private ArrayList<String> opening = new ArrayList<String>();

    public Player(String pl){
        this.pl = pl ;
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


    public String toString(){
        return pl + " " + points + " " + deck.size();
    }
}
