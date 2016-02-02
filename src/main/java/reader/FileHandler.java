package reader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import game.Game;


/**
 * creates a object that holds the root folder with all compressed logs file
 *
 */
public class FileHandler {

    private File folderPath ;
    private Game g;

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
        int over = 0;
        int part = 0;
        int overT = 0;
        int partT = 0;
        try{
            //creates a list with all compressed files
            File[] bzList = folderPath.listFiles();
            overT = bzList.length;
            if(bzList != null){
                for(File bz: bzList){
                    over++;
                    part = 0 ;
                    //clean the temp folder
                    FileUtils.cleanDirectory(new File(folderPath.getAbsoluteFile()+"/temp"));

                    //decompress the bz2 file into the temp folder
                    Process p = Runtime.getRuntime().exec(new String[]{"bash","-c","tar -jxf "+bz.getAbsoluteFile()+" -C "+folderPath.getAbsoluteFile()+"/temp"});

                    //wait for tar to finishe
                    p.waitFor();

                    //creates a list with all files in the temp folder
                    File dir = new File(folderPath.getAbsoluteFile()+"/temp");
                    File[] htmlList = dir.listFiles();
                    partT = htmlList.length;

                    //iterates over all files in the temp folder
                    if(htmlList != null){
                        for(File log: htmlList){
                            part++;
                            float o = ((float)over/overT)*100;
                            float pt = ((float)part/partT)*100;
                            progressBar((int)Math.floor(o),(int)Math.floor(pt));
                            g = new Game();
                            g.insertDateTime(log.getName());
                            FileReader f = new FileReader(log);
                            ReadGameHead r = new ReadGameHead(f , g);
                            f.close();

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
    private void progressBar( int overall, int partial){
        String over = "";
        String part = "";
        for(int x = 0 ; x < (overall/4); x++){
            over += "=";
        }

        for(int x = 0 ; x < (partial/4); x++){
            part += "=";
        }
        String progress = "\r overall [" + over +"] "+overall+"%  "+"partial [" + part +"] "+partial+"%";

            System.out.print(progress);
    }

}
