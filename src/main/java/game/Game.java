package game;
import java.util.ArrayList;

import game.player.Player;

public class Game {
    private ArrayList<String> winners = new ArrayList<String>();
    private ArrayList<String> cardsGone = new ArrayList<String>();
    private ArrayList<String> cardsInSuply = new ArrayList<String>();
    private ArrayList<Player> players = new ArrayList<Player>();
    private int gameNumber;

    public Game(){}

    public void insertCardGone(String s){
        this.cardsGone.add(s);
    }


    public void insertCardsInSuply(String s){
        this.cardsInSuply.add(s);
    }

    public void insertPlayer(Player s){
        this.players.add(s);
    }

    public void insertWinner(String s){
        this.winners.add(s);
    }

    public void setGameNumber(int i){
        this.gameNumber = i ;
    }

    public String toString(){
        return winners.toString() + " " + gameNumber + " " + cardsGone.size() + " " + cardsInSuply.size() +" "+ players.toString();
    }

}
