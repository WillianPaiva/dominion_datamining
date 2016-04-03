package universite.bordeaux.app.ReadersAndParser.Readers;

import java.io.File;

import org.bson.Document;

/**
 * @author mlfarfan
 * LogReaderV1 the version log most general
 */
public class LogReaderV1 extends LogReaderAbs implements LogReader {
	
	 /** 
	 * @see Readers.LogReader#getVersion()
	 */
	public int getVersion() {
		 return this.version;
	 }

    /**
     * @param fileLog name of the log file
     */
    public LogReaderV1(File fileLog) {
        super(fileLog);
        this.version = 1;
    }

    /**
     * generates a Document compatible with mongodb based on the log.
     * @return a Document
     */
    public final Document getDoc() {
        return this.toDoc().append("version", this.version);
    }
}