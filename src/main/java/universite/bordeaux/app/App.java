package universite.bordeaux.app;
import java.io.File;
import reader.FileHandler;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        File child = new File("sampleLogs/");
        FileHandler test = new FileHandler(child);
        test.runParser();
    }
}
