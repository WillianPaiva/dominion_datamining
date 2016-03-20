package universite.bordeaux.app;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import universite.bordeaux.app.ReadersAndParser.LogHandler;

public class FileHandlerTest extends TestCase{
	
	public FileHandlerTest (String testName){
		super (testName);
	}

	public static Test suite(){
		return new TestSuite(FileHandlerTest.class);
	}
	
	public void testFileHandlerOpening(){
		LogHandler handler = new LogHandler(new File("testFolder"));
		assert (handler != null);
	}
}
