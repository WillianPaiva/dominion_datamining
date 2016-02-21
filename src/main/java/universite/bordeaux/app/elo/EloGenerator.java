package universite.bordeaux.app.elo;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

import universite.bordeaux.app.game.Game;
import universite.bordeaux.app.game.GameItf;
import universite.bordeaux.app.mapper.MongoMapper;

public final class EloGenerator {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    private static long total = MongoMapper.count();
    private static long part;
    private EloGenerator(){

    }

    public static void Generate(){
        part =0;
        // MongoMapper.createIndex("date", "logs");
        FindIterable<Document> it = MongoMapper.findGamesByDate();
        it.forEach(new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    GameItf g = new Game(document);
                    g.GenerateElo();
                    part++;
                    progressBar(Math.round(((double)part/(double)total)*100),g.getId().toString());

                }
            });

    }

    private static void progressBar( long overall, String parsing){
        String over = "";
        for(int x = 0 ; x < (overall/2); x++){
            over += "=";
        }
        for(int x = 0 ; x < ((100-overall)/2); x++){
            over += "_";
        }

        String progress = "\r"+ANSI_BLUE+ "progress"+ANSI_RESET+" [" +ANSI_GREEN +  over + ANSI_RESET +"] "+overall+"%  "+ANSI_BLUE+"Parsing: "+ANSI_RESET+" [" + ANSI_GREEN + parsing + ANSI_RESET +"] ";

        System.out.print("\r                                                                                  ");
        System.out.print(progress);
    }


}
