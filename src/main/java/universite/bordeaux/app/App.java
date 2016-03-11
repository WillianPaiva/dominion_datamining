package universite.bordeaux.app;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import universite.bordeaux.app.Mongo.MongoConection;
import universite.bordeaux.app.reader.ErrorLogger;
import universite.bordeaux.app.reader.FileHandler;

/**
 *
 * @author Willian Ver Valen Paiva
 */
public class App {
   /**
    *
    * @param args
    * @throws Exception
    */
    public static void main(String[] args) throws Exception{
        int numberOfThreads = 10;
        if (args.length > 0) {
            try {
                numberOfThreads = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println(args[0] + " must be an integer.");
                System.exit(1);
            }
        }

        //set mongoDB logger of as it pollute the output
        Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);

        //create useful indexes for the database
        MongoConection.createIndexes();

        //specify the directory with the compressed logs
        File child = new File("sampleLogs/");

        //create the FileHandler on the logs and run the parser
        FileHandler fr = new FileHandler(child);
        fr.runParser(numberOfThreads);

        //close the file used to log error logs in case it was
        //open during the execution of the program
        ErrorLogger.getInstance().closeLogger();
    }
}
