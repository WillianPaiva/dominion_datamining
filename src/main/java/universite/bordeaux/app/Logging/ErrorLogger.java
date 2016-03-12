package universite.bordeaux.app.Logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * a singleton to write a log with erros occurred during the parsing.
 * @author ebayol
 */
public final class ErrorLogger {

    /**
     * the filewriter.
     */
    private static FileWriter writer;

    /**
     * the instance to be able to create a singleton.
     */
    private static volatile ErrorLogger instance = null;

    /**
     * private constructor.
     */
    private ErrorLogger() {
        try {
            writer = new FileWriter(new File("errorLog.txt"));
        } catch (IOException e) {
            System.err.println("couldn't create the errorLog.txt file");
        }
    }

    /**
     * methode to get the instance and create a new instance if none exists.
     * @return the ErrorLogger instance
     */
    public static ErrorLogger getInstance() {
        if (ErrorLogger.instance == null) {
            synchronized (ErrorLogger.class) {
                if (ErrorLogger.instance == null) {
                    ErrorLogger.instance = new ErrorLogger();
                }
            }
        }
        return ErrorLogger.instance;
    }

    /**
     * writes the message on the log file.
     * @param exceptionMessage message to be write.
     */
    public void logError(final String exceptionMessage) {
        try {
        ErrorLogger.writer.write(exceptionMessage + "\n");
        } catch (IOException e) {
            System.err.println("cannot log error");
        }
    }

    /**
     * close the file descriptor.
     */
    public void closeLogger() {
        try {
            ErrorLogger.writer.close();
        } catch (IOException e) {
            System.err.println("cannot close errorLog.txt");
        }
    }
}
