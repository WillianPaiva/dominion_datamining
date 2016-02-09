package universite.bordeaux.app.game;
import java.util.ArrayList;
import universite.bordeaux.app.game.PlayerTurn;

public class Turn {
    private int number ;
    private ArrayList<PlayerTurn> turns = new ArrayList<PlayerTurn>();
    public Turn(int number){
        this.number = number ;
    }

    public void insertPlay(PlayerTurn play){
        turns.add(play);
    }
}
