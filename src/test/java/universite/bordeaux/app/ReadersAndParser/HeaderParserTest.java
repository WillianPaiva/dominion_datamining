package universite.bordeaux.app.ReadersAndParser;

import java.io.File;
import java.util.ArrayList;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

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
		assertTrue(wins.size()>0);
    }
    
    public void testCardsGoneNotNull(){
    	assertNotNull(HeaderParser.getCardsGone(fr1));
    	assertNotNull(HeaderParser.getCardsGone(fr2));
    }
    
    //modifier un log pour ajourter 2 cardsGone
	public void testCardsGoneSize(){
		assertEquals(HeaderParser.getCardsGone(fr1).size(), 1);
		assertEquals(HeaderParser.getCardsGone(fr2).size(), 1);
	}
    
	public void testNameCardsGone(){
		assertEquals(HeaderParser.getCardsGone(fr1).get(0), "Provinces");
		assertEquals(HeaderParser.getCardsGone(fr2).get(0), "Colonies");
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
	
	//add test de player
	//add test de erreur
	
    public static Test suite(){
		TestSuite suite = new TestSuite(HeaderParserTest.class);
		return suite;	
	}
		
	public static void main (String[] args){
		junit.textui.TestRunner.run(suite());
	}
}
