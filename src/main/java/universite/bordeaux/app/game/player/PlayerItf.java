package universite.bordeaux.app.game.player;

import java.util.HashMap;

import org.bson.Document;

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
