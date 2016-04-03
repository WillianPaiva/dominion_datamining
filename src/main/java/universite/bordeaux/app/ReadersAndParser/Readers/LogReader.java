package universite.bordeaux.app.ReadersAndParser.Readers;

import org.bson.Document;


/**
 * @author mlfarfan
 * interface for the object log Reader 
 *
 */
public interface LogReader {
    /**
     * @return the version number of the log
     */
    public int getVersion();
    
	/**
	 * @return object Document having information logs
	 */
	public Document getDoc();
	
    /**
     * close the file log 
     */
    public void close();
}