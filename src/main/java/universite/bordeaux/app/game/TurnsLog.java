package universite.bordeaux.app.game;
import java.util.ArrayList;
import universite.bordeaux.app.game.Turn;

public class TurnsLog {
    private ArrayList<Turn> turns = new ArrayList<Turn>();
    public TurnsLog(){
    }

    public void insertPlay(Turn n){
        turns.add(n);
    }
}
