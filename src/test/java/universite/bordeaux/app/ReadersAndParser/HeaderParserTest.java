package universite.bordeaux.app.ReadersAndParser;

import java.io.File;
import java.util.ArrayList;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.bson.Document;

public class HeaderParserTest extends TestCase {
	protected FileReader fr1;
	protected FileReader fr2;

	protected void setUp(){
		File log = new File("testFolder/20121210/game-20121210-000100-ed802b83.html"); 
		fr1 = new FileReader(log);	
		
		File log2 = new File("testFolder/20121210/game-20121210-000005-8ef23689.html"); 
		fr2 = new FileReader(log2);	
	}
	
	public void testGetFile(){
		assertNotNull(fr2);
	}
	
	public void testFileNotEmpty(){
		assertTrue(!fr2.isEmpty());
	}
	
	public void testWinnnersNotNull(){
		assertNotNull(HeaderParser.getWinners(fr1));
	}
	
	public void testWinnersNoEmply(){
		ArrayList<String> wins = HeaderParser.getWinners(fr1);
		assertEquals(wins.size(),1);
	}
	
	//modifier html pour ajouter 2 winners
    public void testTwoWinners(){
    	ArrayList<String> wins = HeaderParser.getWinners(fr2);
    	//System.out.println(wins);
		assertTrue(wins.size()>1); // il valide bcp de text fixe
    }
    
    public void testCardsGoneNotNull(){
    	assertNotNull(HeaderParser.getCardsGone(fr1));
    	assertNotNull(HeaderParser.getCardsGone(fr2));
    }
    
    //modifier un log pour ajourter 2 cardsGone
	public void testCardsGoneSize(){
		assertEquals(HeaderParser.getCardsGone(fr1).size(), 1);
		assertEquals(HeaderParser.getCardsGone(fr2).size(), 3);
	}
    
	public void testNameCardsGone(){
		assertEquals(HeaderParser.getCardsGone(fr1).get(0), "Provinces");
		assertEquals(HeaderParser.getCardsGone(fr2).get(0), "Develop");
		assertEquals(HeaderParser.getCardsGone(fr2).get(2), "Expand");
	}
	
	public void testTrashNotNull(){
		assertNotNull(HeaderParser.getTrash(fr1));
		assertNotNull(HeaderParser.getTrash(fr2));
	}
	
	public void testTrashSize(){
		assertEquals(HeaderParser.getTrash(fr1).size(), 5);
		assertEquals(HeaderParser.getTrash(fr2).size(), 10);
	}
	
	public void testTrashNameNumber(){
		assertEquals(HeaderParser.getTrash(fr2).get("Estates").intValue(), 4); //numbre
		assertEquals(HeaderParser.getTrash(fr2).get("Apothecary").intValue(), 1);  //un
		assertEquals(HeaderParser.getTrash(fr2).get("Platinum").intValue(), 1); //a
	}
	
	public void testMarketNotNull(){
		assertNotNull(HeaderParser.getMarket(fr1));
		assertNotNull(HeaderParser.getMarket(fr2));
	}
	
	public void testMarketSize(){
		assertEquals(HeaderParser.getMarket(fr1).size(), 11);
		assertEquals(HeaderParser.getMarket(fr2).size(), 13);
	}
	
	public void testMarketName(){
		assertEquals(HeaderParser.getMarket(fr1).get(5), "Fishing Village");
		assertEquals(HeaderParser.getMarket(fr1).get(10), "Talisman");
	}
	
	public void testPlayerNotNull(){
		assertNotNull(HeaderParser.getPlayers(fr1));
		assertNotNull(HeaderParser.getPlayers(fr2));
	}
	
	public void testPlayerSize(){
		assertEquals(HeaderParser.getPlayers(fr1).size(), 1);
		assertEquals(HeaderParser.getPlayers(fr2).size(), 2);
	}
	
	public void testPlayerNames(){
		assertEquals(HeaderParser.getPlayers(fr1).get(0).getPlayerName(), "guest2");
		assertEquals(HeaderParser.getPlayers(fr2).get(1).getPlayerName(), "SN4X0R");
	}
	
	public void testPlayerPoints(){
		assertEquals(HeaderParser.getPlayers(fr1).get(0).toDoc().get("points"), 58);
		assertEquals(HeaderParser.getPlayers(fr2).get(0).toDoc().get("points"), 84);
		assertEquals(HeaderParser.getPlayers(fr2).get(1).toDoc().get("points"), 39);		
	}
	
	public void testPlayerTurns(){
		assertEquals(HeaderParser.getPlayers(fr1).get(0).toDoc().get("turns"), 26);
		assertEquals(HeaderParser.getPlayers(fr2).get(0).toDoc().get("turns"), 
				     HeaderParser.getPlayers(fr2).get(1).toDoc().get("turns"));
	}
	
	public void testPlayerVictoryCardsNotNull(){
		assertNotNull(HeaderParser.getPlayers(fr1).get(0).toDoc().get("victorycards"));
		
		assertNotNull(HeaderParser.getPlayers(fr2).get(0).toDoc().get("victorycards"));
		assertNotNull(HeaderParser.getPlayers(fr2).get(1).toDoc().get("victorycards"));
	}
	
	public void testPlayerVictoryCardsInfo(){
		Document docPlayer = (Document) HeaderParser.getPlayers(fr1).get(0).toDoc().get("victorycards");
		
		assertEquals(docPlayer.get("Provinces"), 8);
		assertEquals(docPlayer.get("Duchies"), 2); //5
		assertEquals(docPlayer.get("Farmlands"), 2); //Erreur, il a change le nom a Farmls
		
		assertEquals(docPlayer.size(), 3);
	    
	}
	
	public void testPlayerVictoryCardsInfo2(){
		Document docPlayer1 = (Document) HeaderParser.getPlayers(fr2).get(0).toDoc().get("victorycards");

		assertEquals(docPlayer1.get("Colonies"), 6); //10
		assertEquals(docPlayer1.get("Provinces"), 3); //8 
		assertEquals(docPlayer1.get("Estate"), 1); //2
		assertEquals(docPlayer1.size(), 3);  //Erreur, il a ajoute le 4eme element sur un nom bizarre 
		
		//Document{{Provinces=3, Colonies=6, Estate=1, ▼=5}}
	}
	
	public void testPlayerVictoryCardsInfo3(){
		Document docPlayer2 = (Document) HeaderParser.getPlayers(fr2).get(1).toDoc().get("victorycards");
        
		assertEquals(docPlayer2.get("Colonies"), 2); //10
		assertEquals(docPlayer2.get("Province"), 1); //8 
		assertEquals(docPlayer2.get("Duchies"), 2); //2
		assertEquals(docPlayer2.size(), 3); //Erreur, il a ajoute le 4eme element sur un nom bizarre 
		
		//Document{{Duchies=2, Colonies=2, ▼=7, Province=1}}
	}
	
	public void testPlayerOpeningNotNull(){
		assertNotNull(HeaderParser.getPlayers(fr1).get(0).toDoc().get("opening"));
		assertNotNull(HeaderParser.getPlayers(fr2).get(0).toDoc().get("opening"));
		assertNotNull(HeaderParser.getPlayers(fr2).get(1).toDoc().get("opening"));
	}
	
	public void testPlayerOpeningSize(){
		ArrayList<String> openingPlayer1 = (ArrayList<String>)HeaderParser.getPlayers(fr1).get(0).toDoc().get("opening");
		ArrayList<String> openingPlayer2 = (ArrayList<String>)HeaderParser.getPlayers(fr2).get(0).toDoc().get("opening");
		ArrayList<String> openingPlayer3 = (ArrayList<String>)HeaderParser.getPlayers(fr2).get(1).toDoc().get("opening");
				
		assertEquals(openingPlayer1.size(), 2);
		assertEquals(openingPlayer2.size(), 2);
		assertEquals(openingPlayer3.size(), 2);
	}
	
	public void testPlayerOpeningInfo(){
		ArrayList<String> openingPlayer1 = (ArrayList<String>) HeaderParser.getPlayers(fr1).get(0).toDoc().get("opening");
		ArrayList<String> openingPlayer2 = (ArrayList<String>)HeaderParser.getPlayers(fr2).get(0).toDoc().get("opening");
		
        assertEquals(openingPlayer1.get(1), "Moneylender");
        assertEquals(openingPlayer2.get(1), "Jack of All Trades");
	}
	
	public void testPlayerDeckNotNull(){
		assertNotNull(HeaderParser.getPlayers(fr1).get(0).toDoc().get("deck"));
		assertNotNull(HeaderParser.getPlayers(fr2).get(0).toDoc().get("deck"));
		assertNotNull(HeaderParser.getPlayers(fr2).get(1).toDoc().get("deck"));
	}
	
	public void testPlayerDeckSize(){
		Document deckPlayer1 = (Document) HeaderParser.getPlayers(fr1).get(0).toDoc().get("deck");
		Document deckPlayer2 = (Document) HeaderParser.getPlayers(fr2).get(0).toDoc().get("deck");
		Document deckPlayer3 = (Document) HeaderParser.getPlayers(fr2).get(1).toDoc().get("deck");
		
		assertEquals(deckPlayer1.size(), 7);
		assertEquals(deckPlayer2.size(), 12); //erreur, il est vide
		assertEquals(deckPlayer3.size(), 13);
	}
	
	public void testPlayerDeckNames(){
		Document deckPlayer1 = (Document) HeaderParser.getPlayers(fr1).get(0).toDoc().get("deck");
		Document deckPlayer3 = (Document) HeaderParser.getPlayers(fr2).get(1).toDoc().get("deck");
		
		
		assertEquals(deckPlayer1.get("Provinces"), 8);
		assertEquals(deckPlayer3.get("Bazaars"), 7);
		assertEquals(deckPlayer3.get("Colonies"), 2);
		
		assertEquals(deckPlayer3.get("Trade Routes"), 2); //erreur, il coupe le nom Trade
		assertEquals(deckPlayer1.get("Fishing Villages"), 2);  //erreur, il coupe le nom Fishing
			
	}
	
	public void testPlayerFirstHandNotNull(){
		assertNotNull(HeaderParser.getPlayers(fr1).get(0).toDoc().get("firsthand"));
		assertNotNull(HeaderParser.getPlayers(fr2).get(0).toDoc().get("firsthand"));
		assertNotNull(HeaderParser.getPlayers(fr2).get(1).toDoc().get("firsthand"));
	}
	
	public void testPlayerFirstHandSize(){
		Document d1 = (Document) HeaderParser.getPlayers(fr1).get(0).toDoc().get("firsthand");
		Document d2 = (Document) HeaderParser.getPlayers(fr2).get(0).toDoc().get("firsthand");
		Document d3 = (Document) HeaderParser.getPlayers(fr2).get(1).toDoc().get("firsthand");
		
		assertEquals(d1.size(), 2);
		assertEquals(d2.size(), 2);
		assertEquals(d3.size(), 2);
	}
	
	public void testPlayerFirstHandName(){
		Document d1 = (Document) HeaderParser.getPlayers(fr1).get(0).toDoc().get("firsthand");
		Document d2 = (Document) HeaderParser.getPlayers(fr2).get(0).toDoc().get("firsthand");
		Document d3 = (Document) HeaderParser.getPlayers(fr2).get(1).toDoc().get("firsthand");
		
		assertEquals(d1.get("Estates"), 3);
		assertEquals(d2.get("Coppers"), 3);
		assertEquals(d3.get("Estate"), 1);
	}
	
	
	//add test de erreur
	
    public static Test suite(){
		TestSuite suite = new TestSuite(HeaderParserTest.class);
		return suite;	
	}
		
	public static void main (String[] args){
		junit.textui.TestRunner.run(suite());
	}
}
