package universite.bordeaux.app.GameDataStructure;

import org.bson.Document;
import org.bson.types.ObjectId;
import universite.bordeaux.app.Mongo.MongoConection;
import universite.bordeaux.app.Logging.ErrorLogger;
import universite.bordeaux.app.ReadersAndParser.FileReader;
import universite.bordeaux.app.ReadersAndParser.HeaderParser;
import universite.bordeaux.app.ReadersAndParser.LogParser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * implementation of the class match.
 * @author Willian Ver Valen Paiva
 */
public class Match implements MatchItf {

    /**
     * if the log has errors, set this flag to false.
     */
    private boolean flagFail = true;
    /**
     * the id generated automatically by the database.
     */
    private ObjectId id = null;

    /**
     * the list of winners.
     */
    private ArrayList<String> winners;

    /**
     * list of cards which the pile has finished.
     */
    private ArrayList<String> cardsGone;

    /**
     * list of cards available on the market.
     */
    private ArrayList<String> market;

    /**
     * list of players on the match.
     */
    private ArrayList<PlayerItf> players;

    /**
     * list of cards on the trash at the finish of the game.
     */
    private HashMap<String, Integer> trash;

    /**
     * date and time in which the game was played.
     */
    private Date dateTime;

    /**
     * the deference between the highest and lowest elo in the match.
     */
    private int eloGap = 0;

    /**
     * the GameTurn of the match that is the list with all the
     * turns forming the complete log of the match.
     */
    private ArrayList<GameTurn> log;

    /**
     * the name of the file that was parsed.
     * (useful for debugging)
     */
    private String filename;


    /**
     * create the object Match by reading the FileReader
     * and parsing the log file.
     *
     * @param reader the FileReader of the log
     */
    public Match(final FileReader reader) {
        try {
            this.filename = reader.getName();
            this.winners = HeaderParser.getWinners(reader);
            this.cardsGone = HeaderParser.getCardsGone(reader);
            this.market = HeaderParser.getMarket(reader);
            this.players = HeaderParser.getPlayers(reader);
            this.trash = HeaderParser.getTrash(reader);
            this.dateTime = setDateTime(reader.getName());
            this.log = LogParser.getGameLog(reader);
        } catch (Exception e) {
            ErrorLogger.getInstance()
                    .logError("\n" + e.toString() + "\n" + reader.getName());
            this.flagFail = false;
        }
    }

    /**
     * create the object Game based on a Document.
     *
     * @param doc Document to be read and parsed to a Game object
     */
    public Match(final Document doc) {
        this.filename = doc.get("filename", String.class);
        this.winners = doc.get("winners", ArrayList.class);
        this.cardsGone = doc.get("cardsgonne", ArrayList.class);
        this.market = doc.get("market", ArrayList.class);
        this.trash = docToHash(doc.get("trash", Document.class));
        this.dateTime = doc.get("date", Date.class);
        this.players = new ArrayList<>();
        this.id = doc.get("_id", ObjectId.class);
        this.eloGap = doc.get("eloGap", Integer.class);
        for (Object d: doc.get("players", ArrayList.class)) {
            players.add(new Player((Document) d));
        }

    }


    /**
     * parser the object Game to Document format.
     *
     * @return the Document representation of the object Game
     */
    @Override
    public final Document toDoc() {
        return new Document()
            .append("date", this.dateTime)
            .append("filename", this.filename)
            .append("eloGap", this.eloGap)
            .append("winners", this.winners)
            .append("cardsgonne", this.cardsGone)
            .append("market", this.market)
            .append("trash", hashToDoc(this.trash))
            .append("players", players(this.players))
            .append("log", logToDoc());

    }

    /**
     * creates an arraylist with all the turns on
     * document format.
     * @return an arraylist of Documents
     */
    private ArrayList<Document> logToDoc() {
        ArrayList<Document> temp = new ArrayList<>();
        if (!log.isEmpty()) {
            temp.addAll(log.stream().map(GameTurn::toDoc)
                    .collect(Collectors.toList()));
        }
        return temp;
    }
    /**
     * save the object Game on the mongoDB database if the object doesn't exist.
     * if object exists on the database it updates the object
     *
     */
    @Override
    public final void save() {
        if (flagFail) {
            if (this.id == null) {
                this.id = MongoConection.insertMatch(this.toDoc());
                SimplifiedPlayer temp;
                for (PlayerItf p: this.players) {
                    temp = new SimplifiedPlayer(p.getPlayerName());
                    temp.save();
                }
            } else {
                MongoConection.updateMatch(new Document("_id", this.id),
                        new Document("$set", this.toDoc()));
            }
        }
    }


    /**
     * creates an list Documents from a list of players.
     *
     * @param p list of to be converted
     * @return list of documents
     */
    private ArrayList<Document> players(final ArrayList<PlayerItf> p) {
        return p.stream().map(PlayerItf::toDoc)
                .collect(Collectors.toCollection(ArrayList::new));
    }


    /**
     * creates a map from a  document.
     *
     * @param doc document to be converted
     * @return returns a map of with the documents data
     */
    private HashMap<String, Integer> docToHash(final Document doc) {
        HashMap<String, Integer> temp = new HashMap<>();
        for (Map.Entry<String, Object> x: doc.entrySet()) {
            temp.put(x.getKey(), (int) x.getValue());
        }
        return temp;

    }

    /**
     * creates a Document from a map.
     *
     * @param map the map to create the document
     * @return Document with the map data
     */
    private Document hashToDoc(final HashMap<String, Integer> map) {
        Document temp = new Document();
        for (String x: map.keySet()) {
            temp.append(x, map.get(x));
        }
        return temp;
    }





    /**
     * insert the date and time of the game.
     *
     * @param s name of the file (log)
     * @return a date object
     */
    private Date setDateTime(final String s) {
        // game-20101015-000153-1034d2ce.html
        Date temp = new Date();
        String[] date = s.split("-");
        DateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'",
                                                Locale.ENGLISH);
        try {
            temp = format.parse(date[1] + "T" + date[2] + "Z");
        } catch (ParseException e) {
            System.out.println(e);
        }
        return temp;
    }




    @Override
    public final String toString() {
        return winners.toString()
                + " "
                + cardsGone.size()
                + " "
                + market.size()
                + " "
                + players.toString()
                + " "
                + trash.toString();
    }

}
