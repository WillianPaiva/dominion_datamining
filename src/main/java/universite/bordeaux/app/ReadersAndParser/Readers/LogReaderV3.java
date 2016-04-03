package universite.bordeaux.app.ReadersAndParser.Readers;

import java.io.File;

import org.bson.Document;

import universite.bordeaux.app.GameDataStructure.PlayerItf;

/**
 * @author mlfarfan
 * this version contains one or mores players who left the game
 * only have information pertaining to the number of turns and the oppening
 */
public class LogReaderV3 extends LogReaderAbs implements LogReader {

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
		
		
		if (!doc.text().contains("resigned")) {
			String[] firstBreak;

			if (doc.text().contains(" points ")) {
	        	firstBreak = doc.text().split(" points ");
	        } else {
	        	firstBreak = doc.text().split(" point ");
	        }
	        

	        //set the player points
	        String[] p = firstBreak[0].split(":");
	        String temp = p[p.length - 1].replace(" ", "");
	        
	        try {
                pl.setPoints(Integer.parseInt(temp));
            } catch (NumberFormatException e) {
                pl.setPoints(0);
            }
	        
	        String list = firstBreak[1].split(";")[0];
            list = list.substring(1, list.length() - 1)
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
		} else {		
			pl.setPoints(0);
			String turns = doc.text();
			if (pl.getPlayerName().contains(";")) {
				turns = turns.replaceAll(pl.getPlayerName(), " ");
			}
			pl.setTurns(Integer.parseInt(turns
                    .split(";")[1]
                    .replace(" turns", "")
                    .replace(" ", "")));
			
		}
		
	}

}