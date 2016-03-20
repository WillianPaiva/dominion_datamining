package universite.bordeaux.app.Mongo;

import org.bson.Document;
import universite.bordeaux.app.Mongo.MongoConection;

public final class LoadDataToDataBase {

    private LoadDataToDataBase();

    /**
     *
     * save the document in to the database
     */
    public static save(Document doc){
            MongoConnection.insertMatch(doc);
    }
}
