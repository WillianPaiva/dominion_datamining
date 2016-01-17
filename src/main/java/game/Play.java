package game;
import java.util.HashMap;

import game.player.Player;

public class Play {

    private String type ;
    private HashMap<String,Integer> move = new HashMap<String,Integer>();
    private HashMap<Integer,Play> followingPlays = new HashMap<Integer,Play>();
    private int counter = 0;
    private Player p;
    public Play(String type , Player p){
        this.type = type ;
    }

    public void insertCard(int qty, String card){
        move.put(card,qty);
    }

    public void insertFollowPlay(Play p){
        this.followingPlays.put(counter,p);
        counter++;
    }
}
