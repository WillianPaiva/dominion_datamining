package universite.bordeaux.app.Mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import universite.bordeaux.app.colors.ColorsTemplate;


/**
 * a utility class used to hold useful methods to insert and querry
 * the database.
 * the aim of this class is to centralize the database utilities.
 * @author Willian Ver Valen Paiva
 */
public final class MongoConection {
    /**
     * the port number.
     */
    public static final int PORT = 27020;
    /**
     * the mongodb client.
     */
    private static final MongoClient MONGO_CLIENT;

    static {
        MONGO_CLIENT = new MongoClient("localhost", PORT);
    }

    /**
     * the mongodb database reference.
     */
    private static final MongoDatabase MONGO_DATABASE;

    static {
        MONGO_DATABASE = MONGO_CLIENT.getDatabase("game-logs");
    }

    /**
     * set the constructor private.
     */
    private MongoConection() {
    }

    /**
     * insert a Match into the database.
     * @param match the document to insert into de database.
     * @return the id of the new entrance.
     */
    public static ObjectId insertMatch(final Document match) {
        MONGO_DATABASE.getCollection("logs").insertOne(match);
        return (ObjectId) match.get("_id");
    }

    /**
     * insert a player into the database.
     * @param player the document to insert.
     * @return the id of the new entrance.
     */
    public static ObjectId insertPlayer(final Document player) {
        MONGO_DATABASE.getCollection("players").insertOne(player);
        return (ObjectId) player.get("_id");
    }

    /**
     * update a match in the database.
     * @param match the match to be updated.
     * @param up the changes to be made.
     */
    public static void updateMatch(final Document match, final Document up) {
        MONGO_DATABASE.getCollection("logs").updateOne(match, up);
    }

    /**
     * update a player in the database.
     * @param player the player to be updated.
     * @param up the changes to be made.
     */
    public static void updatePlayer(final Document player, final Document up) {
        MONGO_DATABASE.getCollection("players").updateOne(player, up);
    }

    /**
     * find a player in the database.
     * @param player player name
     * @return the FindIterable of the search.
     */
    public static FindIterable<Document> findPlayer(final String player) {
        return MONGO_DATABASE.getCollection("players")
                .find(new Document("name", player));
    }


    /**
     * create useful indexes.
     */
    public static void createIndexes() {
        createIndex("date", "logs");
        createIndex("players.name", "logs");
        createIndex("players.elo", "logs");
        createIndex("elo", "players");
    }

    /**
     * create a index into the database.
     * @param index field to create a index.
     * @param collection the collection name to create the index.
     */
    public static void createIndex(final String index,
                                   final String collection) {
        System.out.println("\n"
                + ColorsTemplate.ANSI_PURPLE
                + "Generating index for "
                + ColorsTemplate.ANSI_GREEN
                + collection
                + ColorsTemplate.ANSI_PURPLE
                + " on the field "
                + ColorsTemplate.ANSI_GREEN
                + index
                + ColorsTemplate.ANSI_RESET);
        MONGO_DATABASE.getCollection(collection)
                .createIndex(new Document(index, 1));
        System.out.println("done");

    }

}
