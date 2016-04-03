package universite.bordeaux.app.GameDataStructure;

import java.util.HashMap;

import org.bson.Document;


/**
 * interface to provide access to the player class.
 * @author Willian Ver Valen Paiva
 */
public interface PlayerItf {
    /**
     * creates the document version of a player object.
     * @return a Document
     */
    Document toDoc();

    /**
     * return the name of the player.
     * @return String name
     */
    String getPlayerName();

    /**
     * insert a victory card on list.
     * @param quantity number of cards
     * @param cardName card name.
     */
    void insertVictoryCard(final int quantity, final String cardName);
    
    /**
     * set victoryCard
     * @param victoryCard
     */
    void setVictoryCard(final HashMap<String, Integer> victoryCard);

    /**
     * insert a card on players deck.
     * @param quantity number of cards
     * @param cardName card name
     */
    void insertDeck(final int quantity, final String cardName);
    
    /**
     * @param deck
     */
    void setDeck(final HashMap<String, Integer> deck);

    /**
     * set player points.
     * @param points number of points.
     */
    void setPoints(final int points);

    /**
     * set the total of turns.
     * @param turns number of turns.
     */
    void setTurns(final int turns);

    /**
     * insert the opening of the player.
     * @param s card name.
     */
    void insertOpening(final String s);


    /**
     * insert players first hand.
     * @param quantity number of cards
     * @param cardName card name.
     */
    void insertFirstHand(final int quantity, final String cardName);
    
    /**
     * @param firstHand HashMap<String, Integer>
     */
    void setFirstHand(final HashMap<String, Integer> firstHand);

    @Override
    String toString();
}