package universite.bordeaux.app;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import universite.bordeaux.app.mapper.MongoConection;
import universite.bordeaux.app.reader.ErrorLogger;
import universite.bordeaux.app.reader.FileHandler;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {

        Logger.getLogger( "org.mongodb.driver" ).setLevel(Level.OFF);
        MongoConection.index();
        File child = new File("sampleLogs/");
        FileHandler test = new FileHandler(child);
        test.runParser(10);

        // System.out.println("\n Starting the ELO callculation this can take a long time...");
        // EloGenerator.Generate();

        // Chart ch = new Chart("Elo curve", "Elo curve", "number of games played", "Elo");
        // ch.pack( );
        // RefineryUtilities.centerFrameOnScreen( ch );
        // ch.setVisible( true );
        ErrorLogger.getInstance().closeLogger();
    }
}
