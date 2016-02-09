package universite.bordeaux.app.mapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import universite.bordeaux.app.game.Game;
import universite.bordeaux.app.game.player.Player;

public class MongoMapper {
    private MongoClient mongo;
    private MongoDatabase db ;
  public MongoMapper(){
      Logger.getLogger( "org.mongodb.driver" ).setLevel(Level.OFF);
      mongo = new MongoClient();
      db = mongo.getDatabase("game-logs");
  }

    public void insertTodb(Game g){
        Document temp = mappeGame(g);
        db.getCollection("logs").insertOne(temp);
        ObjectId id = (ObjectId)temp.get("_id");
        // System.out.println(id.toString());
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
                     .append("Elo",0)
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
