package universite.bordeaux.app;

import java.io.File;
import reader.FileHandler;
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
        // ds.setURL("jdbc:h2:./h2data/test");
        // ds.setUser("sa");
        // ds.setPassword("sa");
        // Connection conn = ds.getConnection();

        test.runParser();
    }
}
