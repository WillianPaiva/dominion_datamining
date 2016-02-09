package universite.bordeaux.app.game;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import universite.bordeaux.app.game.player.Player;

public class Game {

    //list of winners
    private ArrayList<String> winners = new ArrayList<String>();

    //list of cards which the pile has finished
    private ArrayList<String> cardsGone = new ArrayList<String>();

    //market cards
    private ArrayList<String> cardsInSuply = new ArrayList<String>();

    //list of players in the match
    private ArrayList<Player> players = new ArrayList<Player>();

    //list of cards on the trash at the finish of the game
    private HashMap<String,Integer> trash = new HashMap<String,Integer>();

    //date and time in which the game was played
    private Date dateTime;


	/**
	 * {@inheritDoc}
	 *
	 * @see Object#Game()
	 */
    public Game(){}

	/**
   * insert a card to the list of empty piles
	 *
   * @param card  the card to be inserted
	 */
    public void insertCardGone(String card){
        this.cardsGone.add(card);
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
   * insert a card to the market list
	 *
   * @param card the card to be inserted
	 */
    public void insertCardsInSuply(String card){
        this.cardsInSuply.add(card);
    }



	/**
	 *
	 *
   * @return the market card list
	 */
    public ArrayList<String> getMarket(){
        return this.cardsInSuply;
    }


	/**
   * insert a player to the list of players
	 *
   * @param player the player to be inserted to the list
	 */
    public void insertPlayer(Player player){
        this.players.add(player);
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
   * @return the number of players in the mach
	 */
    public int getTotalPlayers(){
        return this.players.size();
    }




	/**
   * insert a player name to the winner list
	 *
   * @param playerName
	 */
    public void insertWinner(String playerName){
        this.winners.add(playerName);
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
   *insert a card to the list of cards on the trash
   * @param quantity
   * @param card
	 */
    public void insertTrash(int quantity, String card){
        trash.put(card,quantity);
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
