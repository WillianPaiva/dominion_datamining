package universite.bordeaux.app.GameDataStructure;

import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 *
 * @author Willian Ver Valen Paiva
 */
public class GameTurn {

    /**
     *a private internal class to better define a movie.
     */
    private class Play {
        /**
         * defines the type of the action.
         */
        public String type;
        /**
         * the list of cards.
         */
        public HashMap<String, Integer> cards;
         /**
         * the list of plays generated by this action.
         */
        public ArrayList<Play> followingPlays;

        /**
         * play constructor.
         * @param tp the action type.
         * @param cd list of cards used in the action.
         */
        Play(final String  tp, final HashMap<String, Integer> cd) {
            this.followingPlays = new ArrayList<>();
            this.type = tp;
            this.cards = cd;
        }
        /**
         * create a document based on the play.
         * @return return the document version of play
         */
        public Document toDoc() {
            Document temp;
            temp = new Document("type", type)
                    .append("cards", hashtodoc(cards));
            if (!followingPlays.isEmpty()) {
                temp.append("following", mapFollowing());
            }
            return temp;
        }
        /**
         *
         * @return a list with doc versions of all the plays on
         * the following plays list.
         */
        private ArrayList<Document> mapFollowing() {
            ArrayList<Document> temp = new ArrayList<>();
            if (!followingPlays.isEmpty()) {
                for (Play p: followingPlays) {
                    temp.add(p.toDoc());
                }
            }
            return temp;
        }

        /**
         * creates a document based on a hashmap of cards ans quantity.
         * @param map the hash map with the cards.
         * @return a document .
         */
        private Document hashtodoc(final HashMap<String, Integer> map) {
            Document temp = new Document();
            for (String x: map.keySet()) {
                temp.append(x, map.get(x));
            }
            return temp;
        }
    }

    /**
     * a internal class to define a turn of a player .
     */
    private class PlayerTurn {
        /**
         * the name of the player.
         */
        public String playerName;
        /**
         * the list of movies executed by the player this turn.
         */
        public ArrayList<Play> plays;

        /**
         *  player turn constructor.
         * @param name .
         */
        PlayerTurn(final String name) {
            this.playerName = name;
            this.plays = new ArrayList<>();
        }

        /**
         * creates a document version of playerTurn.
         * @return a document.
         */
        public Document toDoc() {
            return new Document("name", playerName).append("plays", mapPlays());
        }

        /**
         * creates a list of documents based on the list of plays
         * made by the player this turn.
         * @return a list of Documents.
         */
        private ArrayList<Document> mapPlays() {
            ArrayList<Document> temp = new ArrayList<>();
            if (!plays.isEmpty()) {
                for (Play p: plays) {
                    temp.add(p.toDoc());
                }
            }
            return temp;
        }
    }


    /**
     * the number of the turn.
     */
    private int number;

    /**
     * the list of with the turn of each player on the match.
     */
    private final ArrayList<PlayerTurn> turns = new ArrayList<>();


    /**
     * create a new turn.
     * @param nb the number of the turn to be create.
     */
    public GameTurn(final int nb) {
        this.number = nb;
    }

    /**
     * find if player already have played this turn.
     * @param playerName the player to search.
     * @return PlayerTurn of the player if exists else returns null.
     */
    private PlayerTurn getPlayerTurn(final String playerName) {
        for (PlayerTurn p: turns) {
            if (p.playerName.equals(playerName)) {
                return p;
            }
        }
        return null;
    }

    /**
     * insert a the move on the list of turns.
     * @param playerName the name of the player tha made the move.
     * @param level the depth of the move in case it is a action
     *              started as a consequence of another move.
     * @param type the type of move made.
     * @param cards the list of cards and quantity used on the move.
     */
    public final void insertMove(final String playerName,
                           final int level,
                           final String type,
                           final HashMap<String, Integer> cards) {
        PlayerTurn playerturn = getPlayerTurn(playerName);
        if (playerturn == null) {
            playerturn = new PlayerTurn(playerName);
            playerturn.plays.add(new Play(type, cards));
            this.turns.add(playerturn);
        } else {
            if (level == 0) {
                playerturn.plays.add(new Play(type, cards));
            } else {
                insertFollowingPlay(level,
                        type,
                        cards,
                        playerturn.plays.get(playerturn.plays.size() - 1));
            }
        }
    }

    /**
     * searches for the previous play and and append
     * the new play on the following plays list.
     * @param level depth of the play.
     * @param type the type of the move.
     * @param cards list of cards.
     * @param play previous play.
     * @return a Play with the new following play appended.
     */
    private Play insertFollowingPlay(final int level,
                                     final String type,
                                     final HashMap<String, Integer> cards,
                                     final Play play) {
        switch (level) {
            case 0:
                return new Play(type, cards);
            case 1:
                play.followingPlays.add(new Play(type, cards));
                break;
            default:
                if (play.followingPlays.isEmpty()) {
                    play.followingPlays.add(new Play(type, cards));
                }
                insertFollowingPlay(level - 1,
                        type,
                        cards,
                        play.followingPlays.get(
                                play.followingPlays.size() - 1));
                break;
        }
        return play;
    }


    /**
     * creates the document version of the object GameTurn.
     * @return a Document.
     */
    public final Document toDoc() {
        return new Document("turn", this.number)
                .append("playersMove", mapPlayerTurn());
    }

    /**
     * creates a list of documents based on the list of playerturns.
     * @return a list of Documents
     */
    private ArrayList<Document> mapPlayerTurn() {
        ArrayList<Document> temp = new ArrayList<>();
        if (!turns.isEmpty()) {
            temp.addAll(turns.stream().map(PlayerTurn::toDoc)
                    .collect(Collectors.toList()));
        }
        return temp;
    }
}