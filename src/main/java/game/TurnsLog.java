package game;
import java.util.HashMap;

public class TurnsLog {
    private int number ;
    private HashMap<Integer,Turn> turns = new HashMap<Integer,Turn>();
    public TurnsLog(int number){
        this.number = number ;
    }

    public void insertPlay(Turn n){
        turns.put(number,n);
    }
}
