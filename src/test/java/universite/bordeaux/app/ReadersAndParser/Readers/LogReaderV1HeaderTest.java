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
public class LogReaderV1HeaderTest extends TestCase {
	
	protected LogReader lr1;
	protected LogReader lr2;
	protected Document doclog1;
	protected Document doclog2;
	
	protected void setUp() throws Exception {
		
		File log = new File("testFolder/20121210/game-20121210-000100-ed802b83.html"); 
		lr1 = LogReaderFactory.createrReader(log);
		
		File log2 = new File("testFolder/20121210/game-20121210-000005-8ef23689.html"); 
		lr2 = LogReaderFactory.createrReader(log2);
		
		doclog1 = lr1.getDoc();
		doclog2 = lr2.getDoc();		
		
	}
	
	public void testDateNotNullLog() {
		
		assertNotNull(doclog1.get(ConstantLog.DATE));
		assertNotNull(doclog2.get(ConstantLog.DATE));
		
	}
	
	public void testNameLog() {
	
		assertEquals(doclog1.get(ConstantLog.FILENAME), "game-20121210-000100-ed802b83.html");
		assertEquals(doclog2.get(ConstantLog.FILENAME), "game-20121210-000005-8ef23689.html");
	}
	
	public void testLogReaderVersion(){
		
		assertTrue(lr1 instanceof LogReaderV1 );
		assertTrue(lr2 instanceof LogReaderV1 );
		
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
		assertEquals(winners.get(0), "guest2");
		
	}
	
	public void testCardsGonneNotNull(){
		
		assertNotNull(doclog1.get(ConstantLog.CARDSGONNE));
		assertNotNull(doclog2.get(ConstantLog.CARDSGONNE));
		
	}
	
	public void testCardsGonneName(){
		
		ArrayList<String> cardsL1 = (ArrayList<String>) doclog1.get(ConstantLog.CARDSGONNE);
		ArrayList<String> cardsL2 = (ArrayList<String>) doclog2.get(ConstantLog.CARDSGONNE);
		
		assertEquals(cardsL1.size(), 1);
		assertEquals(cardsL2.size(), 3);
		
		String card1 = CheckCard.verifyCard("Provinces");
		assertEquals(cardsL1.get(0), card1);
		
		String card2 = CheckCard.verifyCard("Develop");
		String card3 = CheckCard.verifyCard("Bazaar");
		String card4 = CheckCard.verifyCard("Expand");
		
		assertEquals(cardsL2.get(0), card2);
		assertEquals(cardsL2.get(1), card3);
		assertEquals(cardsL2.get(2), card4);
		
	}
	
	public void testMarketNotNull(){
		
		assertNotNull(doclog1.get(ConstantLog.MARKET));
		assertNotNull(doclog2.get(ConstantLog.MARKET));
		
	}
	
	public void testMarketNames(){
		
		ArrayList<String> cardsL1 = (ArrayList<String>) doclog1.get(ConstantLog.MARKET);
		ArrayList<String> cardsL2 = (ArrayList<String>) doclog2.get(ConstantLog.MARKET);
		
		assertEquals(cardsL1.size(), 11);
		assertEquals(cardsL2.size(), 13);
		
		String card1 = CheckCard.verifyCard("Apothecary");
		String card2 = CheckCard.verifyCard("Fishing Village");
		String card3 = CheckCard.verifyCard("Talisman");
		
		String card4 = CheckCard.verifyCard("Ambassador");
		String card5 = CheckCard.verifyCard("Jack of All Trades");
		String card6 = CheckCard.verifyCard("Trade Route");

		assertEquals(cardsL1.get(0), card1);
		assertEquals(cardsL1.get(5), card2);
		assertEquals(cardsL1.get(10), card3);
		assertEquals(cardsL2.get(0), card4);
		assertEquals(cardsL2.get(8), card5);
		assertEquals(cardsL2.get(12), card6);
		
	}
	
	public void testTrashNotNull() {
		
		assertNotNull(doclog1.get(ConstantLog.TRASH));
		assertNotNull(doclog2.get(ConstantLog.TRASH));
		
	}
	
	public void testTrashName(){
		
		Document docLog1 = (Document) doclog1.get(ConstantLog.TRASH);
		Document docLog2 = (Document) doclog2.get(ConstantLog.TRASH);
		
		assertEquals(docLog1.size(), 5);
		assertEquals(docLog2.size(), 10);
		
		assertEquals(docLog1.get(CheckCard.verifyCard("Chapel")), 1);
		assertEquals(docLog1.get(CheckCard.verifyCard("Coppers")), 7);
		
		assertEquals(docLog2.get(CheckCard.verifyCard("Expand")), 1);
		assertEquals(docLog2.get(CheckCard.verifyCard("Coppers")), 8);
		
	}
	
	public static Test suite(){
		TestSuite suite = new TestSuite(LogReaderV1HeaderTest.class);
		return suite;	
	}
		
	public static void main (String[] args){
		junit.textui.TestRunner.run(suite());
	}
	
}
