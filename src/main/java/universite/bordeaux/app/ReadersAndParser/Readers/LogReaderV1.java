package universite.bordeaux.app.ReadersAndParser.Readers;

import org.bson.Document;

public class LogReaderV1 extends LogReaderAbs implements LogReader{

  /**
	 * @param fileLog name of the log file
	 */
	public LogReaderV1(String fileLog) {
    super(fileLog);
    this.version = 1;
	}

	@Override
	public Document getDoc() {
		// TODO Auto-generated method stub
		return null;
	}

}
