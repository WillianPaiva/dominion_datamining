package universite.bordeaux.app.game;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import universite.bordeaux.app.game.player.Player;

public class Game {
    private ArrayList<String> winners = new ArrayList<String>();
    private ArrayList<String> cardsGone = new ArrayList<String>();
    private ArrayList<String> cardsInSuply = new ArrayList<String>();
    private ArrayList<Player> players = new ArrayList<Player>();
    private HashMap<String,Integer> trash = new HashMap<String,Integer>();
    private Date dateTime;
    public Game(){}

    public void insertCardGone(String s){
        this.cardsGone.add(s);
    }
    public ArrayList<String> getCardsGone(){
        return this.cardsGone;
    }
    public Date getDate(){
        return this.dateTime;
    }
    public void insertCardsInSuply(String s){
        this.cardsInSuply.add(s);
    }
    public ArrayList<String> getMarket(){
        return this.cardsInSuply;
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
    public ArrayList<Player> getPlayers(){
        return this.players;
    }
    public int getTotalPlayers(){
        return this.players.size();
    }

    public void insertWinner(String s){
        this.winners.add(s);
    }

    public ArrayList<String> getWinners(){
        return this.winners;
    }

    public void insertTrash(int q, String c){
        trash.put(c,q);
    }
    public HashMap<String,Integer> getTrash(){
        return this.trash;
    }

    public void insertDateTime(String s){
        String[] date = s.split("-");
        this.dateTime = new Date(Integer.parseInt(date[1].substring(0,3)),
                                 Integer.parseInt(date[1].substring(4,5)),
                                 Integer.parseInt(date[1].substring(6,7)),
                                 Integer.parseInt(date[2].substring(0,1)),
                                 Integer.parseInt(date[2].substring(2,3)),
                                 Integer.parseInt(date[2].substring(4,5)));
    }

    public String toString(){
        return winners.toString() + " "  + cardsGone.size() + " " + cardsInSuply.size() +" "+ players.toString()+" "+trash.toString();
    }

}
