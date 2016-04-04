package universite.bordeaux.app.ReadersAndParser.Readers;

import java.io.File;
import java.util.ArrayList;

import org.bson.Document;

import universite.bordeaux.app.Constant.ConstantLog;
import universite.bordeaux.app.ReadersAndParser.CheckCard;
import universite.bordeaux.app.ReadersAndParser.LogReaderFactory;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author mlfarfan
 *
 */
public class LogReaderPlayerTest extends TestCase {
	
	protected LogReader lr1;
	protected LogReader lr2;
	protected LogReader lr3;
	protected Document doclog1;
	protected Document doclog2;
	protected Document doclog3;
	
	protected void setUp() throws Exception {
		
		File log = new File("testFolder/20121210/game-20121210-000005-8ef23689.html"); 
		lr1 = LogReaderFactory.createrReader(log);
		
		File log2 = new File("testFolder/20101110/game-20101110-000556-5f5e69d5.html"); 
		lr2 = LogReaderFactory.createrReader(log2);
		
		File log3 = new File("testFolder/20110110/game-20110110-235856-9bb3d73a.html");  
		lr3 = LogReaderFactory.createrReader(log3);
		
		doclog1 = lr1.getDoc();
		doclog2 = lr2.getDoc();		
		doclog3 = lr3.getDoc();		
		
	}
	
	public void testPlayerNotNull() {
		
		assertNotNull(doclog1.get(ConstantLog.PLAYERS));
		assertNotNull(doclog2.get(ConstantLog.PLAYERS));
		assertNotNull(doclog3.get(ConstantLog.PLAYERS));
		
	}
	
	public void testPlayerNumber() {
		
		ArrayList<Document> players1 = (ArrayList<Document>) doclog1.get(ConstantLog.PLAYERS);
		ArrayList<Document> players2 = (ArrayList<Document>) doclog2.get(ConstantLog.PLAYERS);
		ArrayList<Document> players3 = (ArrayList<Document>) doclog3.get(ConstantLog.PLAYERS);
		
		assertEquals(players1.size(), 2);
		assertEquals(players2.size(), 2);
		assertEquals(players3.size(), 3);
				
	}
	
	public void testPlayerNames() {
		
		ArrayList<Document> players1 = (ArrayList<Document>) doclog1.get(ConstantLog.PLAYERS);
		ArrayList<Document> players2 = (ArrayList<Document>) doclog2.get(ConstantLog.PLAYERS);
		ArrayList<Document> players3 = (ArrayList<Document>) doclog3.get(ConstantLog.PLAYERS);
		
		assertEquals(players1.get(0).get(ConstantLog.NAME), "Bazaar Trader");
		assertEquals(players2.get(1).get(ConstantLog.NAME), "fifi");
		assertEquals(players3.get(2).get(ConstantLog.NAME), "gravitation");
	}
	
	public void testPlayerPoints() {
		
		ArrayList<Document> players1 = (ArrayList<Document>) doclog1.get(ConstantLog.PLAYERS);
		ArrayList<Document> players2 = (ArrayList<Document>) doclog2.get(ConstantLog.PLAYERS);
		ArrayList<Document> players3 = (ArrayList<Document>) doclog3.get(ConstantLog.PLAYERS);
		
		assertEquals(players1.get(1).get(ConstantLog.POINTS), 39);
		assertEquals(players2.get(0).get(ConstantLog.POINTS), 36);
		assertEquals(players3.get(1).get(ConstantLog.POINTS), 25);
		
	}
	
	public void testPlayerTurns() {
		
		ArrayList<Document> players1 = (ArrayList<Document>) doclog1.get(ConstantLog.PLAYERS);
		ArrayList<Document> players2 = (ArrayList<Document>) doclog2.get(ConstantLog.PLAYERS);
		ArrayList<Document> players3 = (ArrayList<Document>) doclog3.get(ConstantLog.PLAYERS);
		
		assertEquals(players1.get(0).get(ConstantLog.TURNS), 28);
		assertEquals(players2.get(1).get(ConstantLog.TURNS), 16);
		assertEquals(players3.get(2).get(ConstantLog.TURNS), 8);
		
	}
	
	public void testPlayerCardVictory() {
		
		ArrayList<Document> players1 = (ArrayList<Document>) doclog1.get(ConstantLog.PLAYERS);
		ArrayList<Document> players2 = (ArrayList<Document>) doclog2.get(ConstantLog.PLAYERS);
		ArrayList<Document> players3 = (ArrayList<Document>) doclog3.get(ConstantLog.PLAYERS);
		
		Document victoryCards1 = (Document) players1.get(1).get(ConstantLog.VICTOYCARDS);
		Document victoryCards2 = (Document) players2.get(1).get(ConstantLog.VICTOYCARDS);
		Document victoryCards3 = (Document) players3.get(0).get(ConstantLog.VICTOYCARDS);
		
		assertNotNull(victoryCards1);
		assertNotNull(victoryCards2);
		assertNotNull(victoryCards3);

		assertEquals(victoryCards1.size(), 1);
		assertEquals(victoryCards2.size(), 3);
		assertEquals(victoryCards3.size(), 3);
		
		//probleme Colonies Duchies	
		
		assertEquals(victoryCards1.get(CheckCard.verifyCard("Province")), 1);
		assertEquals(victoryCards2.get(CheckCard.verifyCard("Great Hall")), 1);
		assertEquals(victoryCards3.get(CheckCard.verifyCard("Duchy")), 1);
		
	}
	
	public void testPlayercardsDeck(){
		
		ArrayList<Document> players1 = (ArrayList<Document>) doclog1.get(ConstantLog.PLAYERS);
		ArrayList<Document> players2 = (ArrayList<Document>) doclog2.get(ConstantLog.PLAYERS);
		ArrayList<Document> players3 = (ArrayList<Document>) doclog3.get(ConstantLog.PLAYERS);
		
		Document deckCards1 = (Document) players1.get(0).get(ConstantLog.DECK);
		Document deckCards2 = (Document) players2.get(0).get(ConstantLog.DECK);
		Document deckCards3 = (Document) players3.get(1).get(ConstantLog.DECK);
		
		assertNotNull(deckCards1);
		assertNotNull(deckCards2);
		assertNotNull(deckCards3);
		
		assertEquals(deckCards1.size(), 11);
		assertEquals(deckCards2.size(), 11);
		assertEquals(deckCards3.size(), 13);
		
		assertEquals(deckCards1.get(CheckCard.verifyCard("Bishops")), 2);
		assertEquals(deckCards2.get(CheckCard.verifyCard("Merchant Ship")), 1);
		assertEquals(deckCards3.get(CheckCard.verifyCard("Coppers")), 6);
		
	}
	
	public void testPlayerCardsFirstHand() {
		
		ArrayList<Document> players1 = (ArrayList<Document>) doclog1.get(ConstantLog.PLAYERS);
		ArrayList<Document> players2 = (ArrayList<Document>) doclog2.get(ConstantLog.PLAYERS);
		ArrayList<Document> players3 = (ArrayList<Document>) doclog3.get(ConstantLog.PLAYERS);
		
		Document firstHand1 = (Document) players1.get(0).get(ConstantLog.FIRSTHAND);
		Document firstHand2 = (Document) players2.get(1).get(ConstantLog.FIRSTHAND);
		Document firstHand3 = (Document) players3.get(2).get(ConstantLog.FIRSTHAND);
		
		assertNotNull(firstHand1);
		assertNotNull(firstHand2);
		assertNotNull(firstHand3);
		
		assertEquals(firstHand1.size(), 2);
		assertEquals(firstHand2.size(), 1);
		assertEquals(firstHand3.size(), 2);
		
		assertEquals(firstHand1.get(CheckCard.verifyCard("Coppers")), 3);
		assertEquals(firstHand2.get(CheckCard.verifyCard("Coppers")), 5);
		assertEquals(firstHand3.get(CheckCard.verifyCard("Estates")), 2);
		
	}
	
	public void testPlayerCardsOpening() {
		
		ArrayList<Document> players1 = (ArrayList<Document>) doclog1.get(ConstantLog.PLAYERS);
		ArrayList<Document> players2 = (ArrayList<Document>) doclog2.get(ConstantLog.PLAYERS);
		ArrayList<Document> players3 = (ArrayList<Document>) doclog3.get(ConstantLog.PLAYERS);
		
		ArrayList<String> opening1 =  (ArrayList<String>) players1.get(1).get(ConstantLog.OPENING);
		ArrayList<String> opening2 =  (ArrayList<String>) players2.get(0).get(ConstantLog.OPENING);
		ArrayList<String> opening3 =  (ArrayList<String>) players3.get(2).get(ConstantLog.OPENING);
		
		assertNotNull(opening1);
		assertNotNull(opening2);
		assertNotNull(opening3);
		
		//System.out.println(opening1);
		assertEquals(opening1.size(), 2);
		assertEquals(opening2.size(), 2);
		assertEquals(opening3.size(), 2);
		
		System.out.println(opening3);
		assertEquals(opening1.get(0), CheckCard.verifyCard("Potion"));		
		assertEquals(opening2.get(0), CheckCard.verifyCard("Silver"));		
		assertEquals(opening3.get(0), CheckCard.verifyCard("Chapel"));		
	}
	
	public static Test suite(){
		TestSuite suite = new TestSuite(LogReaderPlayerTest.class);
		return suite;	
	}
		
	public static void main (String[] args){
		junit.textui.TestRunner.run(suite());
	}
	
}
