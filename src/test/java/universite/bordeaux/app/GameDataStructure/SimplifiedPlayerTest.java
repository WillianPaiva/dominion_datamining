package universite.bordeaux.app.GameDataStructure;

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.bson.Document;
import universite.bordeaux.app.Constant.ConstantLog;

/**
 * test of SimplifiedPlayer class
 * @author mlfarfan
 *
 */
public class SimplifiedPlayerTest extends TestCase {

    private static final MongodStarter starter = MongodStarter.getDefaultInstance();

    private MongodExecutable _mongodExe;
    private MongodProcess _mongod;


    protected SimplifiedPlayer player;
    protected SimplifiedPlayer player2;
    public static final int BASE_ELO = 1000;

    /**
     * initialization 
     */
    @Override
    protected void setUp() throws Exception{

        _mongodExe = starter.prepare(new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(27020, Network.localhostIsIPv6()))
                .build());
        _mongod = _mongodExe.start();

        super.setUp();
        this.player = new SimplifiedPlayer("mlfarfan");
        this.player2 = new SimplifiedPlayer("ebayol");
    }


    /**
     * test of the methode toDoc key name.
     */
    public void testToDocName() {
        Document doc = this.player.toDoc();
        assertEquals("mlfarfan", doc.get(ConstantLog.NAME));
    }

    /**
     * test of the methode toDoc key elo.
     */
    public void testToDocElo() {
        Document doc = this.player2.toDoc();
        assertEquals(BASE_ELO, doc.get(ConstantLog.ELO));
    }

    /**
     * @return new TestSuite of SimplifiedPlayerTest class
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(SimplifiedPlayerTest.class);
        return suite;	
	}
			
	/**
	 * @param args main  
	 */
	public static void main (String[] args) {
			junit.textui.TestRunner.run(suite());
	}
}
