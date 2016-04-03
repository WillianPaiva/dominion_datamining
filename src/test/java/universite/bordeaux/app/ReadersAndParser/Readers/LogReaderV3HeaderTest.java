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
public class LogReaderV3HeaderTest extends TestCase {
	
	protected LogReader lr1;
	protected LogReader lr2;
	protected Document doclog1;
	protected Document doclog2;
	
	protected void setUp() throws Exception {
		
		File log = new File("testFolder/20110110/game-20110110-235856-9bb3d73a.html");  
		lr1 = LogReaderFactory.createrReader(log);
		
		File log2 = new File("testFolder/20110110/game-20110110-000004-c8adb9a1.html");  
		lr2 = LogReaderFactory.createrReader(log2);
		
		doclog1 = lr1.getDoc();
		doclog2 = lr2.getDoc();		
		
	}
	
	public void testDateNotNullLog() {
		
		assertNotNull(doclog1.get(ConstantLog.DATE));
		assertNotNull(doclog2.get(ConstantLog.DATE));
		
	}
	
	public void testNameLog() {
	
		assertEquals(doclog1.get(ConstantLog.FILENAME), "game-20110110-235856-9bb3d73a.html");
		assertEquals(doclog2.get(ConstantLog.FILENAME), "game-20110110-000004-c8adb9a1.html");
	}
	
	public void testLogReaderVersion(){
		
		assertTrue(lr1 instanceof LogReaderV3 );
		assertTrue(lr2 instanceof LogReaderV3 );
		
	}
	
	public void testDocNotNull(){
				
		assertNotNull(doclog1);
		assertNotNull(doclog2);

	}
	
	public void testWinnersNotNull(){
		
		assertNotNull(doclog1.get(ConstantLog.WINNERS));
		assertNotNull(doclog2.get(ConstantLog.WINNERS));
		
	}
	
	public void testWinnersName(){
		
		ArrayList<String> winners = (ArrayList<String>) doclog1.get(ConstantLog.WINNERS);
		ArrayList<String> winners2 = (ArrayList<String>) doclog2.get(ConstantLog.WINNERS);
		
		assertEquals(winners.get(0), "Fuzzywombat");
		assertEquals(winners2.get(0), "cleanest");
		
	}
	
	public void testCardsGonneNotNull(){
		
		assertNotNull(doclog1.get(ConstantLog.CARDSGONNE));
		assertNotNull(doclog2.get(ConstantLog.CARDSGONNE));
		
	}
	
	public void testCardsGonneName(){
		
		ArrayList<String> cardsL1 = (ArrayList<String>) doclog1.get(ConstantLog.CARDSGONNE);
		ArrayList<String> cardsL2 = (ArrayList<String>) doclog2.get(ConstantLog.CARDSGONNE);
		
		assertEquals(cardsL1.size(), 1);
		assertEquals(cardsL2.size(), 0);
		
		String card1 = CheckCard.verifyCard("Provinces");
		assertEquals(cardsL1.get(0), card1);
	}

	public void testMarketNotNull(){
		
		assertNotNull(doclog1.get(ConstantLog.MARKET));
		assertNotNull(doclog2.get(ConstantLog.MARKET));
		
	}
	
	public void testMarketNames(){
		
		ArrayList<String> cardsL1 = (ArrayList<String>) doclog1.get(ConstantLog.MARKET);
		ArrayList<String> cardsL2 = (ArrayList<String>) doclog2.get(ConstantLog.MARKET);

		assertEquals(cardsL1.size(), 10);
		assertEquals(cardsL2.size(), 10);
		
		String card1 = CheckCard.verifyCard("Ambassador");
		String card2 = CheckCard.verifyCard("Grand Market");
		String card3 = CheckCard.verifyCard("Warehouse");
		
		String card4 = CheckCard.verifyCard("Conspirator");
		String card5 = CheckCard.verifyCard("Moat");
		String card6 = CheckCard.verifyCard("Vault");

		assertEquals(cardsL1.get(0), card1);
		assertEquals(cardsL1.get(4), card2);
		assertEquals(cardsL1.get(9), card3);
		assertEquals(cardsL2.get(0), card4);
		assertEquals(cardsL2.get(6), card5);
		assertEquals(cardsL2.get(9), card6);
		
	}
	
	public void testTrashNotNull() {
		
		assertNotNull(doclog1.get(ConstantLog.TRASH));
		assertNotNull(doclog2.get(ConstantLog.TRASH));
		
	}
	
	public void testTrashName(){
		
		Document docLog1 = (Document) doclog1.get(ConstantLog.TRASH);
		Document docLog2 = (Document) doclog2.get(ConstantLog.TRASH);
		
		assertEquals(docLog1.size(), 3);
		assertEquals(docLog2.size(), 0);
		
		assertEquals(docLog1.get(CheckCard.verifyCard("Estates")), 6);
		assertEquals(docLog1.get(CheckCard.verifyCard("Coppers")), 11);
		assertEquals(docLog1.get(CheckCard.verifyCard("Treasure Maps")), 2);
		
	}
	
	public static Test suite(){
		TestSuite suite = new TestSuite(LogReaderV3HeaderTest.class);
		return suite;	
	}
		
	public static void main (String[] args){
		junit.textui.TestRunner.run(suite());
	}
	
}
