package universite.bordeaux.app.GameDataStructure;


import org.bson.Document;
import org.bson.types.ObjectId;
import universite.bordeaux.app.Mongo.MongoConection;

public class SimplifiedPlayer {
    private ObjectId id;
    private final String name;
    private int elo;

    public SimplifiedPlayer(String name){
        Document doc = MongoConection.findPlayer(name).first();
        if(doc != null){
            this.id = doc.get("_id",ObjectId.class);
            this.name = doc.get("name",String.class);
            this.elo = doc.get("elo",Integer.class);
        }else{
            this.name = name;
            this.elo = 1000;
        }

    }

    public Document toDoc(){
        return new Document("name",name).append("elo",elo);
    }

    public void save(){
        if(this.id == null){
            this.id = MongoConection.insertPlayer(this.toDoc());
        }else{
            MongoConection.updatePlayer(new Document("_id",id), new Document("$set",this.toDoc()));
        }
  }

	/**
	 * @return the id
	 */
  public ObjectId getId() {
		return id;
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


}
