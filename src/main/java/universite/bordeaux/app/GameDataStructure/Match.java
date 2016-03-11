package universite.bordeaux.app.GameDataStructure;

import org.bson.Document;
import org.bson.types.ObjectId;
import universite.bordeaux.app.Mongo.MongoConection;
import universite.bordeaux.app.ReadersAndParser.ErrorLogger;
import universite.bordeaux.app.ReadersAndParser.FileReader;
import universite.bordeaux.app.ReadersAndParser.HeaderParser;
import universite.bordeaux.app.ReadersAndParser.LogParser;
import universite.bordeaux.app.elo.Elo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Match implements MatchItf{
    //if the log has errors, set this flag to false
    private boolean flagFail=true; 

    private ObjectId id = null;

    //list of winners
    private ArrayList<String> winners ;

    //list of cards which the pile has finished
    private ArrayList<String> cardsGone ;

    //market cards
    private ArrayList<String> market ;

    //list of players in the match
    private ArrayList<PlayerItf> players ;

    //list of cards on the trash at the finish of the game
    private HashMap<String,Integer> trash ;

    //date and time in which the game was played
    private Date dateTime;

    private int eloGap = 0;

    private ArrayList<GameTurn> log;
    private String filename;


	/**
   * create the object Game by reading the FileReader and parsing the log file
	 *
   * @param reader the FileReader of the log
	 */
    public Match(FileReader reader){
        try{
            this.filename = reader.getName();
            this.winners = HeaderParser.getWinners(reader);
            this.cardsGone = HeaderParser.getCardsGone(reader);
            this.market = HeaderParser.getMarket(reader);
            this.players = HeaderParser.getPlayers(reader);
            this.trash = HeaderParser.getTrash(reader);
            this.dateTime = setDateTime(reader.getName());
            this.log = LogParser.getGameLog(reader);
        }catch(Exception e ){
            ErrorLogger.getInstance().logError("\n"+e.toString()+"\n"+ reader.getName());
            this.flagFail = false;
        }
    }

  /**
   * create the object Game based on a Document
	 *
   * @param doc Document to be read and parsed to a Game object
	 */
    public Match(Document doc){
        this.filename = doc.get("filename",String.class);
        this.winners = doc.get("winners",ArrayList.class);
        this.cardsGone = doc.get("cardsgonne",ArrayList.class);
        this.market = doc.get("market",ArrayList.class);
        this.trash = doctohash(doc.get("trash",Document.class));
        this.dateTime = doc.get("date",Date.class);
        this.players = new ArrayList<>();
        this.id = doc.get("_id",ObjectId.class);
        this.eloGap = doc.get("eloGap",Integer.class);
        for(Object d: doc.get("players",ArrayList.class)){
            players.add(new Player((Document)d));
        }

    }


	/**
   * parser the object Game to Document format
	 *
   * @return the Document representation of the object Game
	 */
    @Override
    public Document toDoc(){
        return new Document()
            .append("date", this.dateTime)
            .append("filename", this.filename)
            .append("eloGap", this.eloGap)
            .append("winners", this.winners)
            .append("cardsgonne",this.cardsGone)
            .append("market",this.market)
            .append("trash",hashtodoc(this.trash))
            .append("players",players(this.players))
            .append("log", logToDoc());

    }

    private ArrayList<Document> logToDoc(){
        ArrayList<Document> temp = new ArrayList<>();
        if(!log.isEmpty()){
            for(GameTurn t: log){
                temp.add(t.toDoc());
            }
        }
        return temp;
    }
  /**
   * save the object Game on the mongoDB database if the object doesn't exist.
   * if object exists on the database it updates the object
	 *
   */
    @Override
    public void save(){
        if (flagFail){
            if(this.id == null){
                this.id = MongoConection.insertGame(this.toDoc());
                SimplifiedPlayer temp;
                for(PlayerItf p: this.players){
                    temp = new SimplifiedPlayer(p.getPlayerName());
                    temp.save();
                }
            }else{
                MongoConection.updateGame(new Document("_id",this.id), new Document("$set",this.toDoc()));
            }
        }
    }


	/**
   * creates an list Documents from a list of players
	 *
   * @param players list of to be converted
   * @return list of documents
	 */
    private ArrayList<Document> players(ArrayList<PlayerItf> p){
        ArrayList<Document> temp = new ArrayList<>();
        for(PlayerItf x: p){
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
        HashMap<String,Integer> temp = new HashMap<>();
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
    @Override
    public void GenerateElo(){
        HashMap<String,Integer> temp = new HashMap<>();
        SimplifiedPlayer pl;
        int min = 99999999;
        int max = -999999999;
        for(PlayerItf p: this.players){
            pl = new SimplifiedPlayer(p.getPlayerName());
            temp.put(p.getPlayerName(), pl.getElo());
            if(pl.getElo() < min){
                min = pl.getElo();
            }
            if(pl.getElo() > max){
                max = pl.getElo();
            }
        }
        HashMap<String,Integer> result = Elo.Calculate(this.winners, temp);
        for(Map.Entry<String,Integer> res: result.entrySet()){
            pl = new SimplifiedPlayer(res.getKey());
            pl.setElo(res.getValue());
            pl.save();
            getPlayer(res.getKey()).setGameElo(res.getValue());
        }
        this.eloGap = (max-min);
        this.save();
    }



	/**
   *
   * @return the list of empty piles
	 */
    @Override
    public ArrayList<String> getCardsGone(){
        return this.cardsGone;
    }



	/**
	 *
	 *
   * @return the date in which the game was played
	 */
    @Override
    public Date getDate(){
        return this.dateTime;
    }



	/**
	 *
	 *
   * @return the market card list
	 */
    @Override
    public ArrayList<String> getMarket(){
        return this.market;
    }



	/**
   * search for a player on by name
	 *
   * @param playerName
   * @return player
	 */
    @Override
    public PlayerItf getPlayer(String playerName){
        for(PlayerItf x: players){
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
    @Override
    public ArrayList<PlayerItf> getPlayers(){
        return this.players;
    }



	/**
   * @return the game ObjectId _id
	 */
    @Override
	public ObjectId getId() {
		return id;
	}


	/**
	 *
	 *
   * @return the list of name of the winners
	 */
    @Override
    public ArrayList<String> getWinners(){
        return this.winners;
    }



	/**
	 *
	 *
   * @return the list of cards on trash
	 */
    @Override
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


    public void setEloGap(int gap){
        this.eloGap = gap;
    }

    public int getEloGap(){
        return this.eloGap;
    }

    @Override
    public String toString(){
        return winners.toString() + " "  + cardsGone.size() + " " + market.size() +" "+ players.toString()+" "+trash.toString();
    }

}
