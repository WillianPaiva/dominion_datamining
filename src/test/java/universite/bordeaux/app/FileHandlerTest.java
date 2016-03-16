package universite.bordeaux.app;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import universite.bordeaux.app.ReadersAndParser.FileHandler;

public class FileHandlerTest extends TestCase{
	
	public FileHandlerTest (String testName){
		super (testName);
	}

	public static Test suite(){
		return new TestSuite(FileHandlerTest.class);
	}
	
	public void testFileHandlerOpening(){
		FileHandler handler = new FileHandler(new File("testFolder"));
		assert (handler != null);
	}
}
