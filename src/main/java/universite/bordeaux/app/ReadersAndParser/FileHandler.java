package universite.bordeaux.app.ReadersAndParser;

import org.apache.commons.io.FileUtils;
import universite.bordeaux.app.GameDataStructure.Match;
import universite.bordeaux.app.GameDataStructure.MatchItf;
import universite.bordeaux.app.colors.ColorsTemplate;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;



/**
 * creates a object that holds the root folder with all compressed logs file
 *
 */
public class FileHandler {
    private final File folderPath ;
    public  Queue <File> queue = new LinkedList<>();
    private int over = 0;
    private int overT = 0;


    /**
     * @param folderPath path of the folder to use with the FileHandler
     */
    public FileHandler(File folderPath){
        this.folderPath = folderPath ;
    }

    /**
     * iterates between all compressed logs and decompress parse and delete decompressed logs
     * @param numThreads number of threads to use for the parsing
     */
    public void runParser(int numThreads){
            System.out.println(ColorsTemplate.ANSI_CYAN + "Strating the Parser it can take a long time..." + ColorsTemplate.ANSI_RESET);
            //creates a list with all compressed files
            File[] bzList = folderPath.listFiles();
            overT = bzList.length;
            if(bzList != null){
                for(File bz: bzList){
                    this.queue.offer(bz);
                }
                Thread[] t = new Thread[numThreads];
                for(int x = 0; x < numThreads; x++ ){
                    t[x] = new Thread(new Runnable(){
                            @Override
                            public void run(){
                                while(queue.peek() != null){
                                    File f= queue.poll();
                                    process(f);
                                }
                            }
                        });
                    t[x].start();
                }
                for(Thread x: t){
                    try{
                        x.join();
                    }catch(InterruptedException e){
                        universite.bordeaux.app.Logging.ErrorLogger.getInstance().logError(e.toString());
                        //System.out.println(e);
                    }
                }
            }else{
                System.out.println("wrong directory");
            }
    }

    /**
     * parsing one file
     * @param f File to be parsed
     */
    private void process(File f){
        try{
            String foldername = f.getName().replace(".tar.bz2","");
            File folder = new File(folderPath.getAbsoluteFile()+"/"+foldername);
            if(!folder.exists()){
                folder.mkdir();
            }

            //decompress the bz2 file into the temp folder
            Process p = Runtime.getRuntime().exec(new String[]{"bash","-c","tar -jxf "+f.getAbsoluteFile()+" -C "+folder.getAbsoluteFile()});

            //wait for tar to finishe
            p.waitFor();

            //creates a list with all files in the temp folder
            File[] htmlList = folder.listFiles();

            //iterates over all files in the temp folder
            if(htmlList != null){
                for(File log: htmlList){
                    FileReader fr = new FileReader(log);
                    if (!fr.isEmpty()){
                        MatchItf g = new Match(fr);
                        g.save();
                    }
                    fr.close();
                    progressBar(Math.round(((float)over/(float)overT)*100),log.getName());

                }
                progressBar(Math.round(((float)over/(float)overT)*100),"finidhed");
            }else{
                System.out.println("no files in temp");
            }
            FileUtils.deleteDirectory(folder);
        }catch(IOException  | InterruptedException e){
            universite.bordeaux.app.Logging.ErrorLogger.getInstance().logError(e.toString());
            //System.out.println(e);
        }
        over++;
    }


    /**
     * Displays a progress bar in the prompt, every time a file is parsed, this method is called and the bar progresses
     * @param overall total number of files to be parsed
     * @param fileName String containing the last parsed file
     */
    public void progressBar( int overall, String fileName){
        String total = "";
        for(int x = 0 ; x < (overall/2); x++){
            total += "=";
        }
        for(int x = 0 ; x < ((100-overall)/2); x++){
            total += "_";
        }

        String progress = "\r"+ColorsTemplate.ANSI_BLUE+ "overall"+ColorsTemplate.ANSI_RESET+" [" +ColorsTemplate.ANSI_GREEN +  total + ColorsTemplate.ANSI_RESET +"] "+overall+"%  "+ColorsTemplate.ANSI_BLUE+"Parsing: "+ColorsTemplate.ANSI_RESET+" [" + ColorsTemplate.ANSI_GREEN + fileName + ColorsTemplate.ANSI_RESET +"] ";

        System.out.print("\r                                                                                  ");
        System.out.print(progress);
    }

}
