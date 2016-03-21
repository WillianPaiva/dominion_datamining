package universite.bordeaux.app.ReadersAndParser.Readers;

import javax.print.Doc;

public class LogReaderV1 extends LogReaderAbs{

	/** 
	 * @param fileLog name of the log file
	 */
	public LogReaderV1(String fileLog) {
		super(fileLog);
	}

	@Override
	public Doc getDoc() {
		return null;
	}

}
