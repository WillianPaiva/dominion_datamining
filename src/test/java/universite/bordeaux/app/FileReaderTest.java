package universite.bordeaux.app;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import universite.bordeaux.app.ReadersAndParser.FileReader;

public class FileReaderTest extends TestCase{
	
	   	public FileReaderTest( String testName )
	    {
	        super( testName );
	    }

	    public static Test suite()
	    {
	        return new TestSuite( FileReaderTest.class );
	    }

	    public void testFileReaderOpening()
	    {
	    	FileReader reader = new FileReader(new File("testFile"));
	    	assert (reader != null);
	    }
	}
