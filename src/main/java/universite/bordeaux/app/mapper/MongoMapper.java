package universite.bordeaux.app.mapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.BasicDBList;

import universite.bordeaux.app.game.Game;
import universite.bordeaux.app.game.player.Player;

public class MongoMapper {
    private MongoClient mongo;
    private MongoDatabase db ;
    ArrayList<String> games = new ArrayList<String>();

    public MongoMapper(){
        Logger.getLogger( "org.mongodb.driver" ).setLevel(Level.OFF);
        mongo = new MongoClient();
        db = mongo.getDatabase("game-logs");
    }


    public void insertTodb(Game g){
        Document temp = mappeGame(g);
        db.getCollection("logs").insertOne(temp);
        // Document t = temp.get("game-log",Document.class);
        // ArrayList<String> d = t.get("market",ArrayList.class);
        // System.out.println(d);
        ObjectId id = (ObjectId)temp.get( "_id" );
        for(Player p: g.getPlayers() ){
            games.clear();
            FindIterable<Document> iterable = db.getCollection("players").find(new Document("_id",p.getPlayerName()));
            iterable.forEach(new Block<Document>() {
                    @Override
                    public void apply(final Document document) {
                        ArrayList<String> b = document.get("games",ArrayList.class);
                        games.addAll(b);
                    }
                });

            games.add(id.toString());
            if(games.size() > 1){
                db.getCollection("players").updateOne(new Document("_id",p.getPlayerName()), new Document("$set",new Document("elo",0).append("games",games)));
            }else{
                db.getCollection("players").insertOne(new Document("_id", p.getPlayerName()).append("elo",0).append("games",games));
            }
        }
    }
    public FindIterable<Document> it(){
       return db.getCollection("players").find(new Document("_id","o ze"));
    }

    private Document mappeGame(Game g){
        return new Document("game-log",new Document()
                            .append("date", g.getDate().toString())
                            .append("winners", g.getWinners())
                            .append("cardsgonne",g.getCardsGone())
                            .append("market",g.getMarket())
                            .append("market",g.getMarket())
                            .append("trash",hashtodoc(g.getTrash()))
                            .append("market",g.getMarket())
                            .append("players",players(g.getPlayers())));
    }

    private ArrayList<Document> players(ArrayList<Player> p){
        ArrayList<Document> temp = new ArrayList<Document>();
        for(Player x: p){
            temp.add(new Document()
                     .append("name",x.getPlayerName())
                     .append("Elo",x.getGameElo())
                     .append("points",x.getPoints())
                     .append("turns",x.getTurns())
                     .append("victorycards",hashtodoc(x.getVictoryCards()))
                     .append("deck",hashtodoc(x.getDeck()))
                     .append("firsthand",hashtodoc(x.getFirstHand()))
                     );
        }
        return temp;
    }

    private Document hashtodoc(HashMap<String,Integer> map){
        Document temp = new Document();
        for(String x: map.keySet()){
            temp.append(x, map.get(x));
        }
        return temp;
    }
}
