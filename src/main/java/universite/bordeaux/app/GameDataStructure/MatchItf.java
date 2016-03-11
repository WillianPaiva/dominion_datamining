package universite.bordeaux.app.GameDataStructure;

import org.bson.Document;

/**
 * Interface to access to the Match.
 * @author Willian Ver Valen Paiva
 */
public interface MatchItf {

    /**
     * Converts the Match object in the Document format.
     * @return A Document.
     */
    Document toDoc();

    /**
     * save the Match in to the database.
     */
    void save();

    @Override
    String toString();
}
