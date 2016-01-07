package reader;
import java.io.*;
import java.lang.Runtime;
import java.lang.InterruptedException;
import org.apache.commons.io.FileUtils;

/**
 * creates a object that holds the root folder with all compressed logs file
 *
 */
public class FileHandler {

    private File folderPath ;

	/**
	 *
	 *
   * @param folderPath The file path of the root of the compressed logs
	 */
    public FileHandler(File folderPath){
        this.folderPath = folderPath ;
    }


	/**
   * iterates between all compressed logs and decompress parse and delete decompressed logs
   *
	 */
    public void runParser(){
        try{
            //creates a list with all compressed files
            File[] bzList = folderPath.listFiles();
            if(bzList != null){
                for(File bz: bzList){
                    //clean the temp folder
                    FileUtils.cleanDirectory(new File(folderPath.getAbsoluteFile()+"/temp"));

                    //decompress the bz2 file into the temp folder
                    Process p = Runtime.getRuntime().exec(new String[]{"bash","-c","tar -jxf "+bz.getAbsoluteFile()+" -C "+folderPath.getAbsoluteFile()+"/temp"});

                    //wait for tar to finishe
                    p.waitFor();

                    //creates a list with all files in the temp folder
                    File dir = new File(folderPath.getAbsoluteFile()+"/temp");
                    File[] htmlList = dir.listFiles();

                    //iterates over all files in the temp folder
                    if(htmlList != null){
                        for(File log: htmlList){
                            //TODO call the parser function with the log
                        }
                    }else{
                        System.out.println("no files in temp");
                    }

                }
            }else{
                System.out.println("wrong directory");
            }
        } catch(IOException | InterruptedException e){
            e.printStackTrace();
        }
    }

}
