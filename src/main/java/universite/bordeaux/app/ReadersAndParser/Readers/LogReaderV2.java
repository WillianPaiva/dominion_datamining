package universite.bordeaux.app.ReadersAndParser.Readers;

import java.io.File;

import org.bson.Document;

import universite.bordeaux.app.GameDataStructure.Player;
import universite.bordeaux.app.GameDataStructure.PlayerItf;

public class LogReaderV2 extends LogReaderAbs implements LogReader{

    /**
     * @param fileLog name of the log file
     */
    public LogReaderV2(File fileLog) {
        super(fileLog);
        this.version = 2;
    }

    protected PlayerItf createPlayer(org.jsoup.nodes.Document doc) {
        String name = doc.select("b").text().split(":")[0];
        return new Player(name);
    }

    public Document getDoc(){
        return this.toDoc().append("version", this.version);
    }
}
