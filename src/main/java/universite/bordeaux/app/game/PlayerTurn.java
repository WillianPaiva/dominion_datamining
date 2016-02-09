package universite.bordeaux.app.game;
import java.util.ArrayList;
import universite.bordeaux.app.game.player.Player;

public class PlayerTurn {
    private Player p ;
    private ArrayList<Play> plays = new ArrayList<Play>();
    public PlayerTurn(Player p){
        this.p = p ;
    }

    public void insertPlay(Play play){
        plays.add(play);
    }
}
