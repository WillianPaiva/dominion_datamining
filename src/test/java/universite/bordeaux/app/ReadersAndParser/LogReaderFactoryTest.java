package universite.bordeaux.app.ReadersAndParser;

import java.io.File;
import java.util.ArrayList;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.bson.Document;

import universite.bordeaux.app.Constant.ConstantLog;
import universite.bordeaux.app.ReadersAndParser.Readers.LogReader;
import universite.bordeaux.app.ReadersAndParser.Readers.LogReaderAbs;

public class LogReaderFactoryTest extends TestCase {
	protected LogReader lr1;
	protected LogReader lr2;
	protected LogReader lr3;
	protected LogReader lr4;
	protected LogReader lr5;
	protected LogReader lr6;
	

	protected void setUp(){
		File log = new File("testFolder/20121210/game-20121210-000100-ed802b83.html"); 
		lr1 = LogReaderFactory.createrReader(log);
		
		File log2 = new File("testFolder/20121210/game-20121210-000005-8ef23689.html"); 
		lr2 = LogReaderFactory.createrReader(log2);
		
		File log3 = new File("testFolder/20101110/game-20101110-000556-5f5e69d5.html"); 
		lr3 = LogReaderFactory.createrReader(log3);
		
		File log4 = new File("testFolder/20101210/game-20101210-110027-405f636c.html"); 
		lr4 = LogReaderFactory.createrReader(log4);
		
		File log5 = new File("testFolder/20110110/game-20110110-235856-9bb3d73a.html"); 
		lr5 = LogReaderFactory.createrReader(log5);
		
		File log6 = new File("testFolder/20110110/game-20110110-000004-c8adb9a1.html"); 
		lr6 = LogReaderFactory.createrReader(log6);
		
	}
	
	public void testGetFile(){
		assertNotNull(lr1);
		assertNotNull(lr2);
		assertNotNull(lr3);
		assertNotNull(lr4);
		assertNotNull(lr5);
		assertNotNull(lr6);
		
	}
	
	public void testVersion(){
		assertEquals(lr1.getVersion(), 1);
		assertEquals(lr2.getVersion(), 1);
		assertEquals(lr3.getVersion(), 2);
		assertEquals(lr4.getVersion(), 2); //points in the tag <b>
		assertEquals(lr5.getVersion(), 3); //with wins, player resigned
		assertEquals(lr6.getVersion(), 3); //wins resigned
	}
	
	public void testDocNotNull(){
		
		assertNotNull(lr1.getDoc());
		assertNotNull(lr2.getDoc());
		assertNotNull(lr3.getDoc());
		assertNotNull(lr4.getDoc());
		assertNotNull(lr5.getDoc());
		assertNotNull(lr6.getDoc());
	}
	
    public static Test suite(){
		TestSuite suite = new TestSuite(LogReaderFactoryTest.class);
		return suite;	
	}
		
	public static void main (String[] args){
		junit.textui.TestRunner.run(suite());
	}
}

