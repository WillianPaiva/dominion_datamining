package universite.bordeaux.app.ReadersAndParser;

import java.io.File;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import universite.bordeaux.app.ReadersAndParser.Readers.LogReader;
import universite.bordeaux.app.ReadersAndParser.Readers.LogReaderV1;
import universite.bordeaux.app.ReadersAndParser.Readers.LogReaderV2;


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
      fr.close();

      if (doc.select("b").text().contains("points")) {
          return new LogReaderV2(log);
      } else {
          return new LogReaderV1(log);
      }

  }


}
