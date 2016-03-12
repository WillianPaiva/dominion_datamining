package universite.bordeaux.app.GameDataStructure;


import org.bson.Document;
import org.bson.types.ObjectId;
import universite.bordeaux.app.Mongo.MongoConection;

/**
 * this class is a simplified version of player
 * that is designed to hold his global elo and a list
 * of all played games.
 * @author Willian Ver Valen Paiva
 */
public class SimplifiedPlayer {
    /**
     * base elo constant.
     */
    public static final int BASE_ELO = 1000;
    /**
     * the player id.
     */
    private ObjectId id;
    /**
     * the player name.
     */
    private final String name;
    /**
     * player global elo.
     */
    private int elo;


    /**
     * constructor.
     * @param nm the player name.
     */
    public SimplifiedPlayer(final String nm) {
        Document doc = MongoConection.findPlayer(nm).first();
        if (doc != null) {
            this.id = doc.get("_id", ObjectId.class);
            this.name = doc.get("name", String.class);
            this.elo = doc.get("elo", Integer.class);
        } else {
            this.name = nm;
            this.elo = BASE_ELO;
        }

    }

    /**
     *  creates a Document version of the simplified player.
     * @return a Document
     */
    public final Document toDoc() {
        return new Document("name", name).append("elo", elo);
    }

    /**
     * save the simplified player into the database.
     */
    public final void save() {
        if (this.id == null) {
            this.id = MongoConection.insertPlayer(this.toDoc());
        } else {
            MongoConection.updatePlayer(new Document("_id", id),
                    new Document("$set", this.toDoc()));
        }
  }


}
