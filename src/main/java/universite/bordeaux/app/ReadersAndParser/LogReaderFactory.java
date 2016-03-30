package universite.bordeaux.app.ReadersAndParser;

import java.io.File;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import universite.bordeaux.app.ReadersAndParser.Readers.LogReader;
import universite.bordeaux.app.ReadersAndParser.Readers.LogReaderV1;
import universite.bordeaux.app.ReadersAndParser.Readers.LogReaderV2;
import universite.bordeaux.app.ReadersAndParser.Readers.LogReaderV3;


public final class LogReaderFactory{

  private LogReaderFactory(){

  }

  public static LogReader createrReader(File log){

      FileReader fr = new FileReader(log);
      if (fr.searchLineWithString("(.*)----------------------(.*)") == null){
          return null;
      }
      fr.jumpline();
      Document doc = Jsoup.parse(fr.jumpline());
      
      if (doc.select("b").text().contains("points")) {
    	  fr.close();
          return new LogReaderV2(log);
      } else if(fr.searchLineWithString(".+>:.resigned.*") != null) {
    	  fr.close();
    	  return new LogReaderV3(log);
      } else {
    	  fr.close();
          return new LogReaderV1(log);
      }

  }


}
