/**
 * 
 */
package universite.bordeaux.app.GameDataStructure;


import java.util.HashMap;
import java.util.Map;
import org.bson.Document;
import universite.bordeaux.app.Constant.ConstantLog;
import universite.bordeaux.app.ReadersAndParser.HeaderParserTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * test of PlayerTest class
 * @author mlfarfan
 *
 */
public class PlayerTest extends TestCase {




	protected Player p1;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() {

        this.p1 = new Player("mlfarfan");
	}
	
	public void testGetPlayerName() {
		assertEquals("mlfarfan", this.p1.getPlayerName());
	}
	
	public void testInsertVictoryCard() {
		this.p1.insertVictoryCard(5, "estate");
		HashMap<String, Integer> temp = new HashMap<>();
		Document doc = (Document) p1.toDoc().get(ConstantLog.VICTOYCARDS);
        for (Map.Entry<String, Object> x: doc.entrySet()) {
            temp.put(x.getKey(), (int) x.getValue());
        }
		assertTrue(temp.containsKey("estate"));
		assertTrue(5 == temp.get("estate"));
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite(PlayerTest.class);
		return suite;	
	}
		
	public static void main (String[] args) {
		junit.textui.TestRunner.run(suite());
	}

}