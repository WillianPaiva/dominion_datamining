package main.java.game;
import java.util.ArrayList;
import game.player.Player;

public class Turn {
    private Player p ;
    private ArrayList<Play> plays = new ArrayList<Play>();
    public Turn(Player p){
        this.p = p ;
    }

    public void insertPlay(Play play){
        plays.add(play);
    }
}
