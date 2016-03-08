package universite.bordeaux.app.mapper;
import java.util.ArrayList;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import universite.bordeaux.app.colors.ColorsTemplate;
import universite.bordeaux.app.reader.ErrorLogger;

public final class MongoConection {
    private static MongoClient mongo = new MongoClient("localhost", 27020);
    private static MongoDatabase db = mongo.getDatabase("game-logs");

    private MongoConection(){
    }

    public static ObjectId insertGame(Document game){
        db.getCollection("logs").insertOne(game);
        return (ObjectId)game.get( "_id" );
    }
    public static ObjectId insertPlayer(Document player){
        db.getCollection("players").insertOne(player);
        return (ObjectId)player.get( "_id" );
    }

    public static void updateGame(Document game , Document up){
        db.getCollection("logs").updateOne(game,up);
    }

    public static void updatePlayer(Document player, Document up){
        db.getCollection("players").updateOne(player,up);
    }

    public static FindIterable<Document> findPlayer(String player){
        return db.getCollection("players").find(new Document("name", player));
    }

    public static FindIterable<Document> findGamesByDate(){
        return db.getCollection("logs").find().sort(new Document("date",1));
    }
    public static long count(){
        return db.getCollection("logs").count();
    }

    public static void index(){
        createIndex("date", "logs");
        createIndex("players.name", "logs");
        createIndex("players.elo", "logs");
        createIndex("elo", "players");
    }
    public static void createIndex(String index, String collection){
        System.out.println("\n"+ColorsTemplate.ANSI_PURPLE + "Generating index for "+ColorsTemplate.ANSI_GREEN+collection+ColorsTemplate.ANSI_PURPLE+" on the field "+ColorsTemplate.ANSI_GREEN+index+ColorsTemplate.ANSI_RESET);
        db.getCollection(collection).createIndex(new Document(index,1));
        System.out.println("done");

    }
    public static int getPlayerTotalGames(String player){
        return db.getCollection("players").find(new Document("_id",player)).first().get("games",ArrayList.class).size();
    }

    public static FindIterable<Document> getPlayerGames(String player){
        return db.getCollection("logs").find(new Document("players.name",player)).sort(new Document("date",1));
    }

    public static ArrayList<String> getRank(int x){
        final ArrayList<String> result = new ArrayList<String>();
        FindIterable<Document> it = db.getCollection("players").find().sort(new Document("elo",-1)).limit(x);
        it.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    result.add(document.get("_id",String.class));
                }
            });
        return result;
    }
    // public void insertTodb(Game g){
    //     for(Player p: g.getPlayers() ){
    //         games.clear();
    //         FindIterable<Document> iterable = db.getCollection("players").find(new Document("_id",p.getPlayerName()));
    //         iterable.forEach(new Block<Document>()
    //                 @Override
    //                 public void apply(final Document document) {
    //                     ArrayList<String> b = document.get("games",ArrayList.class);
    //                     games.addAll(b);
    //                 }
    //             });

    //         games.add(id.toString());
    //         if(games.size() > 1){
    //             db.getCollection("players").updateOne(new Document("_id",p.getPlayerName()), new Document("$set",new Document("elo",1000).append("games",games)));
    //         }else{
    //             db.getCollection("players").insertOne(new Document("_id", p.getPlayerName()).append("elo",1000).append("games",games));
    //         }
    //     }
    // }
    // public void generateElo(){
    //     Date start = new Date();
    //     DateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.ENGLISH);
    //     try{
    //         start = format.parse("20101011T000000Z");
    //     }catch(ParseException e){
    //         System.out.println(e);
    //     }
    //     db.getCollection("logs").createIndex(new Document("game-log.date",1));
    //     FindIterable<Document> iterable = db.getCollection("logs").find().sort(new Document("game-log.date",1));
    //         iterable.forEach(new Block<Document>() {
    //                 @Override
    //                 public void apply(final Document document) {
    //                     ArrayList<Document> playersDoc = document.get("game-log",Document.class).get("players",ArrayList.class);
    //                     ArrayList<String> winners = document.get("game-log",Document.class).get("winners",ArrayList.class);
    //                     HashMap<String,Integer> players = new HashMap<String,Integer>();

    //                     for(Document doc: playersDoc){
    //                         elo = 0;
    //                         FindIterable<Document> it = db.getCollection("players").find(new Document("_id",doc.get("name",String.class)));
    //                         it.forEach(new Block<Document>() {
    //                                 @Override
    //                                 public void apply(final Document dc) {
    //                                     elo = dc.get("elo",Integer.class);
    //                                 }
    //                             });

    //                         players.put(doc.get("name",String.class),elo);
    //                             }
    //                     HashMap<String,Integer> result = Elo.Calculate(winners, players);
    //                     for (Map.Entry<String,Integer> entry : result.entrySet())
    //                         {
    //                             db.getCollection("players").updateOne(new Document("_id",entry.getKey()), new Document("$set",new Document("elo",entry.getValue())));
    //                         }
    //                     System.out.println(result);

    //                 }
    //             });

    // }

}
