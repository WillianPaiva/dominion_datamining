package universite.bordeaux.app;

import universite.bordeaux.app.Mongo.MongoConection;
import universite.bordeaux.app.Logging.ErrorLogger;
import universite.bordeaux.app.ReadersAndParser.LogHandler;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * main class of the program responsible to launch the parser on the logs.
 * @author Willian Ver Valen Paiva
 */
public class App {
    /**
     * default number of threads to use.
     */
    public static final int NUMBER_OF_THREADS = 4;
    /**
    *main function.
    * @param args expected to have the number of threads
    */
    public static void main(final String[] args) {
        int numberOfThreads = NUMBER_OF_THREADS;
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
        LogHandler fr = new LogHandler(child);
        fr.runParser(numberOfThreads);

        //close the file used to log error logs in case it was
        //open during the execution of the program
        ErrorLogger.getInstance().closeLogger();
    }
}