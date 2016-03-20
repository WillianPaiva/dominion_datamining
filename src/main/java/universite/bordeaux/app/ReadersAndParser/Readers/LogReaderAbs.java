package universite.bordeaux.app.ReadersAndParser.Readers;

import java.io.File;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.bson.Document;
import universite.bordeaux.app.ReadersAndParser.FileReader;

/**
 *
 * @author ktagnaouti
 */
public abstract class LogReaderAbs implements LogReader{
    private int version;
    private FileReader log;
    public LogReaderAbs(final String fileLog){
       this.log = new FileReader(new File(fileLog));
    }
    /**
   * parses the first line and grab the list of winners.
   *
   * @param this.log the FileReader object to be readed.
   * @return a list of winners present on the line
	 */
    private Document getWinners() {
        org.jsoup.nodes.Document doc;
        Document result = new Document();
        if (this.log.jumpline().contains("<title>")) {

            //creates a document with the first line of the log
            doc = Jsoup.parse(this.log.getLine());

            //get the game number
            String title = doc.title();
            String[] parts = title.split("#");
            // game.setGameNumber(Integer.parseInt(parts[1]));

            //find the match winner
            String[] win = doc.body().text().split("wins!");
            if (win[0].contains("rejoice in their shared victory!")) {
                String[] winners = win[0]
                        .split("rejoice in their shared victory!")[0]
                        .split("and");
                for (String x: winners) {
                    result.add(x.trim());
                }
            } else {
                result.add(win[0].trim());
                if (win[0].trim().equals("Game log")) {
                    throw new UnsupportedOperationException();
                }
            }
        }
        this.log.rewindFile();
        return null;
    }

  
}
