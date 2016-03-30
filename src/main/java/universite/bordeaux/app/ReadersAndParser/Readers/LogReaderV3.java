package universite.bordeaux.app.ReadersAndParser.Readers;

import java.io.File;

import org.bson.Document;

import universite.bordeaux.app.GameDataStructure.PlayerItf;

public class LogReaderV3 extends LogReaderAbs implements LogReader{

	public LogReaderV3(File fileLog) {
		super(fileLog);
        this.version = 3;
	}

	@Override
	public int getVersion() {
		 return this.version;
	}

	@Override
	public Document getDoc() {
		return this.toDoc().append("version", this.version);
	}
	
	/** 
	 * log with resigned in one or more players, <b>#3 gravitation</b>: resigned (1st); 8 turns
	 * the function adds turns, victory cards and points to the object PlayerItf
	 * @param PlayerItf pl, org.jsoup.nodes.Document doc
	 */
	protected final void getPointsVcTurnPlayer(PlayerItf pl, org.jsoup.nodes.Document doc) {
		String[] firstBreak;

        firstBreak = doc.text().split("points");

        //set the player points
        String[] p = firstBreak[0].split(":");
        String temp = p[p.length - 1].replace(" ", "");

		//IF resigned
		if (temp.contains("resigned")) {
            pl.setPoints(0);
        } else {
            try {
                pl.setPoints(Integer.parseInt(temp));
            } catch (NumberFormatException e) {
                pl.setPoints(0);
            }
        }

		if (!doc.text().contains("resigned")) {
            String list = firstBreak[1].split(";")[0];
            list = list.substring(2, list.length() - 1)
                .replace("and", "");
            if (!list.contains("nothing")) {
                String[] victoryCards = list.split(",");
                pl.setVictoryCard(getCards(victoryCards,
                                           SEARCH_TRESEHOLD));
                //insert the victorycards on the player object
            }
            pl.setTurns(Integer.parseInt(firstBreak[1]
                                         .split(";")[1]
                                         .replace(" turns", "")
                                         .replace(" ", "")));
        }else {
            pl.setTurns(Integer.parseInt(doc.text()
                                         .split(";")[1]
                                         .replace(" turns", "")
                                         .replace(" ", "")));
        }
		
	}

}
