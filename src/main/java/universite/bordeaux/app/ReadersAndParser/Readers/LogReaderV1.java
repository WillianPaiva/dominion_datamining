package universite.bordeaux.app.ReadersAndParser.Readers;

import java.io.File;

import org.bson.Document;

public class LogReaderV1 extends LogReaderAbs implements LogReader{

  /**
	 * @param fileLog name of the log file
	 */
  public LogReaderV1(File fileLog) {
    super(fileLog);
    this.version = 1;
	}

    public Document getDoc(){
        return this.toDoc().append("version", this.version);
    }
}
