package universite.bordeaux.app.ReadersAndParser;

import java.io.File;


public final class LogReaderFactory{
	
	private LogReaderFactory(){
		
	}

	public static LogReader createrReader(String logName){
		
		File log = new File(logName); 
		FileReader fr = new FileReader(log);
		
		return  null;
		
	}


}
