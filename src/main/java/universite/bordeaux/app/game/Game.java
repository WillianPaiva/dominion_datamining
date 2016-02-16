package universite.bordeaux.app.game;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;

import universite.bordeaux.app.elo.Elo;
import universite.bordeaux.app.game.player.Player;
import universite.bordeaux.app.mapper.MongoMapper;
import universite.bordeaux.app.reader.FileReader;
import universite.bordeaux.app.reader.ReadGameHead;

public class Game implements GameItf{

    private ObjectId id = null;

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
   * create the object Game by reading the FileReader and parsing the log file
	 *
   * @param reader the FileReader of the log
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
   * create the object Game based on a Document
	 *
   * @param doc Document to be read and parsed to a Game object
	 */
    public Game(Document doc){
        this.winners = doc.get("winners",ArrayList.class);
        this.cardsGone = doc.get("cardsgonne",ArrayList.class);
        this.market = doc.get("market",ArrayList.class);
        this.trash = doctohash(doc.get("trash",Document.class));
        this.dateTime = doc.get("date",Date.class);
        this.players = new ArrayList<Player>();
        this.id = doc.get("_id",ObjectId.class);
        for(Object d: doc.get("players",ArrayList.class)){
            players.add(new Player((Document)d));
        }

    }


	/**
   * parser the object Game to Document format
	 *
   * @return the Document representation of the object Game
	 */
    public Document toDoc(){
        return new Document()
                            .append("date", this.dateTime)
                            .append("winners", this.winners)
                            .append("cardsgonne",this.cardsGone)
                            .append("market",this.market)
                            .append("trash",hashtodoc(this.trash))
                            .append("players",players(this.players));
    }

	/**
   * save the object Game on the mongoDB database if the object doesn't exist.
   * if object exists on the database it updates the object
	 *
	 */
    public void save(){
        if(this.id == null){
            this.id = MongoMapper.insertGame(this.toDoc());
            PlayerSimple temp;
            for(Player p: this.players){
                temp = new PlayerSimple(p.getPlayerName());
                temp.insertGame(this.id);
                temp.save();
            }
        }else{
            MongoMapper.updateGame(new Document("_id",this.id), new Document("$set",this.toDoc()));
        }
    }


	/**
   * creates an list Documents from a list of players
	 *
   * @param players list of to be converted
   * @return list of documents
	 */
    private ArrayList<Document> players(ArrayList<Player> p){
        ArrayList<Document> temp = new ArrayList<Document>();
        for(Player x: p){
            temp.add(x.toDoc());
        }
        return temp;
    }


	/**
   * creates a map from a a document
	 *
   * @param doc document to be converted
   * @return returns a map of with the documents data
	 */
    private HashMap<String,Integer> doctohash(Document doc){
        HashMap<String,Integer> temp = new HashMap<String,Integer>();
        for(Map.Entry<String,Object> x: doc.entrySet()){
            temp.put(x.getKey(),(int)x.getValue());
        }
        return temp;

    }

	/**
   * creates a Document from a map
	 *
   * @param map the map to create the document
   * @return Document with the map data
	 */
    private Document hashtodoc(HashMap<String,Integer> map){
        Document temp = new Document();
        for(String x: map.keySet()){
            temp.append(x, map.get(x));
        }
        return temp;
    }



	/**
   * generates the elo for each player of the game
   * and save to the database
	 *
	 */
    public void GenerateElo(){
        HashMap<String,Integer> temp = new HashMap<String,Integer>();
        PlayerSimple pl;
        for(Player p: this.players){
            pl = new PlayerSimple(p.getPlayerName());
            temp.put(p.getPlayerName(), pl.getElo());
        }
        HashMap<String,Integer> result = Elo.Calculate(this.winners, temp);
        for(Map.Entry<String,Integer> res: result.entrySet()){
            pl = new PlayerSimple(res.getKey());
            pl.setElo(res.getValue());
            pl.save();
            getPlayer(res.getKey()).setGameElo(res.getValue());
        }
        this.save();
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
   * @return the game ObjectId _id
	 */
	public ObjectId getId() {
		return id;
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
