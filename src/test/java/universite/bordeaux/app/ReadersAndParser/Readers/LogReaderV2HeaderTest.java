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
public class LogReaderV2HeaderTest extends TestCase {
	
	protected LogReader lr1;
	protected LogReader lr2;
	protected Document doclog1;
	protected Document doclog2;
	
	protected void setUp() throws Exception {
		
		File log = new File("testFolder/20101110/game-20101110-000556-5f5e69d5.html"); 
		lr1 = LogReaderFactory.createrReader(log);
		
		File log2 = new File("testFolder/20101210/game-20101210-110027-405f636c.html"); 
		lr2 = LogReaderFactory.createrReader(log2);
		
		doclog1 = lr1.getDoc();
		doclog2 = lr2.getDoc();		
		
	}
	
	public void testDateNotNullLog() {
		
		assertNotNull(doclog1.get(ConstantLog.DATE));
		assertNotNull(doclog2.get(ConstantLog.DATE));
		
	}
	
	public void testNameLog() {
	
		assertEquals(doclog1.get(ConstantLog.FILENAME), "game-20101110-000556-5f5e69d5.html");
		assertEquals(doclog2.get(ConstantLog.FILENAME), "game-20101210-110027-405f636c.html");
	}
	
	public void testLogReaderVersion(){
		
		assertTrue(lr1 instanceof LogReaderV2 );
		assertTrue(lr2 instanceof LogReaderV2 );
		
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
		
		ArrayList<String> winners = (ArrayList<String>) doclog2.get(ConstantLog.WINNERS);
		assertEquals(winners.get(0), "foobar");
		
		winners = (ArrayList<String>) doclog1.get(ConstantLog.WINNERS);
		assertEquals(winners.get(0), "Goldenmean");
		
	}
	
	public void testCardsGonneNotNull(){
		
		assertNotNull(doclog1.get(ConstantLog.CARDSGONNE));
		assertNotNull(doclog2.get(ConstantLog.CARDSGONNE));
		
	}
	
	public void testCardsGonneName(){
		
		ArrayList<String> cardsL1 = (ArrayList<String>) doclog1.get(ConstantLog.CARDSGONNE);
		ArrayList<String> cardsL2 = (ArrayList<String>) doclog2.get(ConstantLog.CARDSGONNE);
		
		assertEquals(cardsL1.size(), 1);
		assertEquals(cardsL2.size(), 1);
		
		String card1 = CheckCard.verifyCard("Provinces");
		assertEquals(cardsL1.get(0), card1);
				
		assertEquals(cardsL2.get(0), card1);
	
	}
	
	public void testMarketNotNull(){
		
		assertNotNull(doclog1.get(ConstantLog.MARKET));
		assertNotNull(doclog2.get(ConstantLog.MARKET));
		
	}
	
	public void testMarketNames(){
		
		ArrayList<String> cardsL1 = (ArrayList<String>) doclog1.get(ConstantLog.MARKET);
		ArrayList<String> cardsL2 = (ArrayList<String>) doclog2.get(ConstantLog.MARKET);
		
		assertEquals(cardsL1.size(), 10);
		assertEquals(cardsL2.size(), 11);
		
		String card1 = CheckCard.verifyCard("Chancellor");
		String card2 = CheckCard.verifyCard("Merchant Ship");
		String card3 = CheckCard.verifyCard("Wishing Well");
		
		String card4 = CheckCard.verifyCard("Apothecary");
		String card5 = CheckCard.verifyCard("Scrying Pool");
		String card6 = CheckCard.verifyCard("Tribute");

		assertEquals(cardsL1.get(0), card1);
		assertEquals(cardsL1.get(4), card2);
		assertEquals(cardsL1.get(9), card3);
		assertEquals(cardsL2.get(0), card4);
		assertEquals(cardsL2.get(7), card5);
		assertEquals(cardsL2.get(10), card6);
		
	}
	
	public void testTrashNotNull() {
		
		assertNotNull(doclog1.get(ConstantLog.TRASH));
		assertNotNull(doclog2.get(ConstantLog.TRASH));
		
	}
	
	public void testTrashName(){
		
		Document docLog1 = (Document) doclog1.get(ConstantLog.TRASH);
		Document docLog2 = (Document) doclog2.get(ConstantLog.TRASH);
		
		assertEquals(docLog1.size(), 0);
		assertEquals(docLog2.size(), 2);
				
		assertEquals(docLog2.get(CheckCard.verifyCard("Estate")), 1);
		assertEquals(docLog2.get(CheckCard.verifyCard("Golds")), 2);
		
	}
	
	
	public static Test suite(){
		TestSuite suite = new TestSuite(LogReaderV2HeaderTest.class);
		return suite;	
	}
		
	public static void main (String[] args){
		junit.textui.TestRunner.run(suite());
	}
	
}
