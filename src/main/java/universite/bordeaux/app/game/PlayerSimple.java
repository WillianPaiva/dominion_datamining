package universite.bordeaux.app.game;

import java.util.ArrayList;

import org.bson.Document;
import org.bson.types.ObjectId;

import universite.bordeaux.app.game.player.Player;
import universite.bordeaux.app.mapper.MongoMapper;

public class PlayerSimple {
    private String id;
    private int elo;
    private ArrayList<ObjectId> games;
    private boolean first;

    public PlayerSimple(String id){
        Document doc = MongoMapper.findPlayer(id).first();
        if(doc != null){
            this.id = doc.get("_id",String.class);
            this.elo = doc.get("elo",Integer.class);
            this.games = doc.get("games",ArrayList.class);
            this.first = false;
        }else{
            this.id = id;
            this.elo = 1000;
            this.games = new ArrayList<ObjectId>();
            this.first = true;
        }

    }

    public Document toDoc(){
        return new Document("_id",id).append("elo",elo).append("games", games);
    }
    public Document toDocWithoutID(){
        return new Document("elo",elo).append("games", games);
    }

    public void save(){
        if(this.first == true){
            MongoMapper.insertPlayer(this.toDoc());
        }else{
            MongoMapper.updatePlayer(new Document("_id",id), new Document("$set",this.toDocWithoutID()));
        }
  }

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the elo
	 */
	public int getElo() {
		return elo;
	}

	/**
	 * @param elo the elo to set
	 */
	public void setElo(int elo) {
		this.elo = elo;
	}

	/**
	 * @return the games
	 */
	public ArrayList<ObjectId> getGames() {
		return games;
	}

    public void insertGame(ObjectId g){
        games.add(g);
    }

}
