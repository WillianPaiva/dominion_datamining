package game;
import java.util.ArrayList;
import java.util.HashMap;

import game.player.Player;

public class Game {
    private ArrayList<String> winners = new ArrayList<String>();
    private ArrayList<String> cardsGone = new ArrayList<String>();
    private ArrayList<String> cardsInSuply = new ArrayList<String>();
    private ArrayList<Player> players = new ArrayList<Player>();
    private HashMap<String,Integer> trash = new HashMap<String,Integer>();
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

    public Player getPlayer(String s){
        for(Player x: players){
            if(x.getPlayerName().equals(s)){
                return x;
            }
        }
        return null;
    }

    public int getTotalPlayers(){
        return this.players.size();
    }

    public void insertWinner(String s){
        this.winners.add(s);
    }

    public void setGameNumber(int i){
        this.gameNumber = i ;
    }

    public void insertTrash(int q, String c){
        trash.put(c,q);
    }

    public String toString(){
        return winners.toString() + " " + gameNumber + " " + cardsGone.size() + " " + cardsInSuply.size() +" "+ players.toString()+" "+trash.toString();
    }

}
