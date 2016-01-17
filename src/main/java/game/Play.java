package game;
import java.util.HashMap;

public class Play {

    private int type ;
    private HashMap<String,Integer> move = new HashMap<String,Integer>();
    private HashMap<Integer,Play> followingPlays = new HashMap<Integer,Play>();
    private int counter = 0;
    public Play(int type){
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
