package universite.bordeaux.app.GameDataStructure;

import org.bson.Document;

import java.util.HashMap;

public interface PlayerItf{
    public Document toDoc();
    public String getPlayerName();
    public void insertVictoryCard(int quantity , String cardName);
    public HashMap<String,Integer> getVictoryCards();
    public void insertDeck(int quantity , String cardName);
    public HashMap<String,Integer> getDeck();
    public void setPoints(int points);
    public int getPoints();
    public void setTurns(int turns);
    public int getGameElo();
    public void setGameElo(int gameElo);
    public int getTurns();
    public void insertOpening(String s);
    public void insertFirstHand(int quantity , String cardName);
    public HashMap<String,Integer> getFirstHand();
    @Override
    public String toString();
}
