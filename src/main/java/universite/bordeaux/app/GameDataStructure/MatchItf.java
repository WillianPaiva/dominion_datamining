package universite.bordeaux.app.GameDataStructure;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.bson.Document;
import org.bson.types.ObjectId;

public interface MatchItf {
    // Converts the Game object in the Document format, to use with mongodb server
    public Document toDoc();
    // Saves the Game in the database
    public void save();
    // Generates elo for each player of the game and saves it to the database
    public void GenerateElo();
    // list of the getters on the object
    public ArrayList<String> getCardsGone();
    public Date getDate();
    public ArrayList<String> getMarket();
    public PlayerItf getPlayer(String playerName);
    public ArrayList<PlayerItf> getPlayers();
    public ObjectId getId();
    public ArrayList<String> getWinners();
    public HashMap<String,Integer> getTrash();
    @Override
    public String toString();
}
