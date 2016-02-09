package universite.bordeaux.app;

import java.io.File;

import universite.bordeaux.app.mapper.MongoMapper;
import universite.bordeaux.app.reader.FileHandler;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        File child = new File("sampleLogs/");
        FileHandler test = new FileHandler(child);
        test.runParser();
    }
}
