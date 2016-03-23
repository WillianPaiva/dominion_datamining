package universite.bordeaux.app.ReadersAndParser;

import org.apache.commons.io.FileUtils;
import universite.bordeaux.app.GameDataStructure.Match;
import universite.bordeaux.app.GameDataStructure.MatchItf;
import universite.bordeaux.app.Mongo.MongoConection;
import universite.bordeaux.app.ReadersAndParser.Readers.LogReader;
import universite.bordeaux.app.colors.ColorsTemplate;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;



/**
 * creates a object that holds the root folder with all compressed logs file.
 *
 */
public class LogHandler {

    /**
     * starting value for the progrees bar.
     */
    public static final int ZERO = 0;
    /**
     * constant to 100 to calculate the progress bar.
     */
    public static final int HUNDRED = 100;

    /**
     * the File with the path of the folder with the logs.
     */
    private final File folderPath;

    /**
     * file queue with the compressed logs to be parsed.
     */
    private  Queue<File> queue = new LinkedList<>();

    /**
     *number of files parsed.
     */
    private int filesParsed = ZERO;

    /**
     * total number of files to be parsed.
     */
    private int totalFilesToBeParsed = ZERO;


    /**
     * @param folder path of the folder to use with the FileHandler
     */
    public LogHandler(final File folder) {
        this.folderPath = folder;
    }

    /**
     * iterates between all compressed logs and decompress parse and
     * delete decompressed logs.
     * @param numThreads number of threads to use for the parsing
     */
    public final void runParser(final int numThreads) {
        System.out.println(ColorsTemplate.ANSI_CYAN
                + "Strating the Parser it can take a long time..."
                + ColorsTemplate.ANSI_RESET);

        //creates a list with all compressed files
        File[] bzList = folderPath.listFiles();
        totalFilesToBeParsed = bzList.length;
        if (bzList != null) {
            //populate the queue
            populateQueue(bzList);

            //create a pool of threads and run.
            Thread[] t = new Thread[numThreads];
            for (int x = 0; x < numThreads; x++) {
                t[x] = new Thread(() -> {
                    while (queue.peek() != null) {
                        File f = queue.poll();
                        process(f);
                    }
                });
                t[x].start();
            }
            for (Thread x: t) {
                try {
                    x.join();
                } catch (InterruptedException e) {
                    universite.bordeaux.app.Logging.ErrorLogger.getInstance()
                            .logError(e.toString());
                    //System.out.println(e);
                }
            }
        } else {
            System.out.println("wrong directory");
        }
    }

    /**
     * populates the task queue.
     * @param bzList list of files to insert
     */
    private void populateQueue(final File[] bzList) {
        for (File bz: bzList) {
            this.queue.offer(bz);
        }
    }

    /**
     * parsing one file.
     * @param f File to be parsed
     */
    private void process(final File f) {
        try {
            String foldername = f.getName().replace(".tar.bz2", "");

            File folder = new File(folderPath.getAbsoluteFile()
                    + "/"
                    + foldername);

            if (!folder.exists()) {
                folder.mkdir();
            }

            //decompress the bz2 file into the temp folder
            Process p = null;

            try {
                p = Runtime.getRuntime().exec(new String[]{"bash",
                        "-c",
                        "tar -jxf "
                                + f.getAbsoluteFile()
                                + " -C "
                                + folder.getAbsoluteFile()});
            } catch (IOException e) {
                e.printStackTrace();
            }

            //wait for tar to finish
            try {
                p.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //creates a list with all files in the temp folder
            File[] htmlList = folder.listFiles();

            //iterates over all files in the temp folder
            if (htmlList != null) {
                for (File log: htmlList) {
                    LogReader temp = LogReaderFactory.createrReader(log);
                    MongoConection.insertMatch(temp.getDoc());
                    temp.close();
                    progressBar(Math.round(((float) filesParsed
                                    / (float) totalFilesToBeParsed)
                                    * HUNDRED),
                            log.getName());

                }
                progressBar(Math.round(((float) filesParsed
                                / (float) totalFilesToBeParsed)
                                * HUNDRED),
                        "finished");
            } else {
                System.out.println("no files in temp");
            }
            FileUtils.deleteDirectory(folder);
        } catch (IOException e) {
            universite.bordeaux.app.Logging.ErrorLogger
                    .getInstance().logError(e.toString());
        }
        filesParsed++;
    }


    /**
     * Displays a progress bar in the prompt, every time a file is parsed,
     * this method is called and the bar progresses.
     * @param overall total number of files to be parsed
     * @param fileName String containing the last parsed file
     */
    public final void progressBar(final int overall, final String fileName) {
        String total = "";

        for (int x = 0; x < (overall / 2); x++) {
            total += "=";
        }
        for (int x = 0; x < ((HUNDRED - overall) / 2); x++) {
            total += "_";
        }



        String progress = "\r"
                + ColorsTemplate.ANSI_BLUE
                + "overall"
                + ColorsTemplate.ANSI_RESET
                + " ["
                + ColorsTemplate.ANSI_GREEN
                + total
                + ColorsTemplate.ANSI_RESET
                + "] "
                + overall
                + "%  "
                + ColorsTemplate.ANSI_BLUE
                + "Parsing: "
                + ColorsTemplate.ANSI_RESET
                + " ["
                + ColorsTemplate.ANSI_GREEN
                + fileName
                + ColorsTemplate.ANSI_RESET
                + "] ";

        System.out.print("\r                       "
                + "                              "
                + "                              "
                + "                              ");
        System.out.print(progress);
    }

}
