package universite.bordeaux.app;
import java.io.File;
import java.util.Arrays;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import reader.FileHandler;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        File child = new File("sampleLogs/");
        FileHandler test = new FileHandler(child);
        test.runParser();
        String pass = "dominionDatabase";
        MongoCredential credential = MongoCredential.createCredential("dominion", "project", pass.toCharArray());
        MongoClient mongoClient = new MongoClient(new ServerAddress("151.80.32.75"),Arrays.asList(credential));
        MongoDatabase db = mongoClient.getDatabase("project");
    }
}
