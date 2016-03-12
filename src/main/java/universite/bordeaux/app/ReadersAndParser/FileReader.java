package universite.bordeaux.app.ReadersAndParser;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;


/**
 * class to make essy the manipulation of the file descriptor.
 * @author Willian Ver Valen Paiva
 */
public class FileReader {
    /**
     * the Scanner .
     */
    private Scanner scan;
    /**
     * the logfile oppened on this file reader.
     */
    private File log;

    /**
     * value on the line that the file descriptor is point .
     */
    private String line;

    /**
     * constructor.
     * @param logFile file to be used
     */
    public FileReader(final File logFile) {
        this.log = logFile;
        try {
            scan = new Scanner(logFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * get the file name.
     * @return a string with the tfile name
     */
    public final String getName() {
        return this.log.getName();
    }

    /**
     *
     *
     * @return the actual line of the file
     */
    public final String getLine() {
        return this.line;
    }

    /**
     * moves the pointer to the next line.
     *
     * @return next line
     */
    public final String jumpline() {
        try {
        this.line = this.scan.nextLine();
        } catch (NoSuchElementException e) {
            System.out.println("error with file: "
                    + this.getName());
        }
        return this.line;
    }

    /**
     * search for the first line that contains a given string.
     *
     * @param target a regex string to be searched.
     * @return line found or null
     */
    public final String searchLineWithString(final String target) {
        while (scan.hasNextLine()) {
            this.line = this.scan.nextLine();
            if (line.matches(target)) {
                return this.line;
            }
        }
        return null;
    }


    /**
     * close the scanner.
     *
     */
    public final void close() {
        scan.close();
    }

    /**
     * returns the pointer to the first line of the file.
     *
     */
    public final void rewindFile() {
        scan.close();
        try {
            scan = new Scanner(this.log);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get if file is empty.
     * @return true if the file is empty
     */
    public final boolean isEmpty() {
       this.rewindFile();
       return !(this.scan.hasNext());
    }
}
