package universite.bordeaux.app.ReadersAndParser.Readers;

import org.bson.Document;


public interface LogReader {
    /**
     *
     * @return object Document having information logs
     */
    public int getVersion();
	public Document getDoc();
    public void close();
}
