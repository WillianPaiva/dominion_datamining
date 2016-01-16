package game;
import java.util.HashMap;

public class Play {

    private int type ;
    private HashMap<String,Integer> move = new HashMap<String,Integer>();
    private Play followPlay;

    public Play(int type){
        this.type = type ;
    }

    public void insertCard(int qty, String card){
        move.put(card,qty);
    }

    public void setFollowPlay(Play p){
        this.followPlay = p;
    }
}
