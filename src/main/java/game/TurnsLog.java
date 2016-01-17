package game;
import java.util.ArrayList;
import game.Turn;

public class TurnsLog {
    private ArrayList<Turn> turns = new ArrayList<Turn>();
    public TurnsLog(){
    }

    public void insertPlay(Turn n){
        turns.add(n);
    }
}
