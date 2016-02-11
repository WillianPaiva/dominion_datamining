package universite.bordeaux.app.game;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import universite.bordeaux.app.game.player.Player;
import universite.bordeaux.app.reader.FileReader;
import universite.bordeaux.app.reader.ReadGameHead;

public class Game {

    //list of winners
    private ArrayList<String> winners ;

    //list of cards which the pile has finished
    private ArrayList<String> cardsGone ;

    //market cards
    private ArrayList<String> market ;

    //list of players in the match
    private ArrayList<Player> players ;

    //list of cards on the trash at the finish of the game
    private HashMap<String,Integer> trash ;

    //date and time in which the game was played
    private Date dateTime;


	/**
	 * {@inheritDoc}
	 *
	 * @see Object#Game()
	 */
    public Game(FileReader reader){
        this.winners = ReadGameHead.getWinners(reader);
        this.cardsGone = ReadGameHead.getCardsGone(reader);
        this.market = ReadGameHead.getMarket(reader);
        this.players = ReadGameHead.getPlayers(reader);
        this.trash = ReadGameHead.getTrash(reader);
        this.dateTime = setDateTime(reader.getName());
    }


	/**
   *
   * @return the list of empty piles
	 */
    public ArrayList<String> getCardsGone(){
        return this.cardsGone;
    }



	/**
	 *
	 *
   * @return the date in which the game was played
	 */
    public Date getDate(){
        return this.dateTime;
    }



	/**
	 *
	 *
   * @return the market card list
	 */
    public ArrayList<String> getMarket(){
        return this.market;
    }



	/**
   * search for a player on by name
	 *
   * @param playerName
   * @return player
	 */
    public Player getPlayer(String playerName){
        for(Player x: players){
            if(x.getPlayerName().equals(playerName)){
                return x;
            }
        }
        return null;
    }



	/**
	 *
	 *
   * @return the list of players
	 */
    public ArrayList<Player> getPlayers(){
        return this.players;
    }



	/**
	 *
	 *
   * @return the list of name of the winners
	 */
    public ArrayList<String> getWinners(){
        return this.winners;
    }



	/**
	 *
	 *
   * @return the list of cards on trash
	 */
    public HashMap<String,Integer> getTrash(){
        return this.trash;
    }




	/**
   * insert the date and time of the game
	 *
   * @param the name of the file (log)
	 */
    private Date setDateTime(String s){
        // game-20101015-000153-1034d2ce.html
        Date temp = new Date();
        String[] date = s.split("-");
        DateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.ENGLISH);
        try{
            temp = format.parse(date[1]+"T"+date[2]+"Z");
        }catch(ParseException e){
            System.out.println(e);
                }
        return temp;
    }

    public String toString(){
        return winners.toString() + " "  + cardsGone.size() + " " + market.size() +" "+ players.toString()+" "+trash.toString();
    }

}
