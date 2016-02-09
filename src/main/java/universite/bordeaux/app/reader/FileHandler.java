package universite.bordeaux.app.reader;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.io.FileUtils;

import universite.bordeaux.app.game.Game;
import universite.bordeaux.app.mapper.MongoMapper;



/**
 * creates a object that holds the root folder with all compressed logs file
 *
 */
public class FileHandler {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    private File folderPath ;
    public  Queue <File> queue = new LinkedList<File>();
    private int over = 0;
    private int overT = 0;

	/**
	 *
	 *
   */
    public FileHandler(File folderPath){
        this.folderPath = folderPath ;
    }


	/**
   * iterates between all compressed logs and decompress parse and delete decompressed logs
   *
	 */
    public void runParser(){
            System.out.println(ANSI_CYAN + "Strating the Parser it can take a long time..." + ANSI_RESET);
            //creates a list with all compressed files
            File[] bzList = folderPath.listFiles();
            overT = bzList.length;
            if(bzList != null){
                for(File bz: bzList){
                    this.queue.offer(bz);
                }
                for(int x = 0; x < 5; x++ ){
                    Thread t = new Thread(new Runnable(){
                            public void run(){
                                while(queue.peek() != null){
                                    File t= queue.poll();
                                    process(t);
                                }
                            }
                        });
                    t.start();
                }
            }else{
                System.out.println("wrong directory");
            }
    }

    private void process(File f){
        MongoMapper t = new MongoMapper();
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
                    Game g = new Game();
                    g.insertDateTime(log.getName());
                    FileReader fr = new FileReader(log);
                    ReadGameHead r = new ReadGameHead(fr , g);
                    r.startParser();
                    t.insertTodb(g);
                    fr.close();
                    progressBar(Math.round(((float)over/(float)overT)*100),log.getName());

                }
            }else{
                System.out.println("no files in temp");
            }
            FileUtils.deleteDirectory(folder);
        }catch(IOException  | InterruptedException e){
            System.out.println(e);
        }
        over++;
    }


    public void progressBar( int overall, String parsing){
        String over = "";
        for(int x = 0 ; x < (overall/2); x++){
            over += "=";
        }
        for(int x = 0 ; x < ((100-overall)/2); x++){
            over += "_";
        }

        String progress = "\r"+ANSI_BLUE+ "overall"+ANSI_RESET+" [" +ANSI_GREEN +  over + ANSI_RESET +"] "+overall+"%  "+ANSI_BLUE+"Parsing: "+ANSI_RESET+" [" + ANSI_GREEN + parsing + ANSI_RESET +"] ";

        System.out.print("\r                                                                                  ");
        System.out.print(progress);
    }

}
