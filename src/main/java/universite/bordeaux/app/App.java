package universite.bordeaux.app;

import org.h2.jdbcx.JdbcDataSource;
import java.io.File;
import java.sql.Connection;
import reader.FileHandler;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import java.util.logging.Logger;
import java.util.logging.Level;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        // Logger.getLogger( "org.mongodb.driver" ).setLevel(Level.OFF);
        File child = new File("sampleLogs/");
        FileHandler test = new FileHandler(child);
        // JdbcDataSource ds = new JdbcDataSource();
        // ds.setURL("jdbc:h2:./test");
        // ds.setUser("sa");
        // ds.setPassword("sa");
        // Connection conn = ds.getConnection();

        test.runParser();
        // MongoClient mongoClient = new MongoClient();
        // mongoClient.getDB("dominion")
//         MongoDatabase db = mongoClient.getDatabase("local");
//         for (String s : mongoClient.getDatabaseNames()) {
//             System.out.println(s);
// }
    }
}
