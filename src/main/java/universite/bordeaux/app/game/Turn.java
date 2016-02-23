package universite.bordeaux.app.game;
import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;

public class Turn {
    private class Play {
        public String type ;
        public HashMap<String,Integer> move;
        public ArrayList<Play> followingPlays = new ArrayList<Play>();

        public Play(String type, HashMap<String,Integer> move){
            this.type = type ;
            this.move = move;
        }
        public Document toDoc(){
            return new Document("type",type)
                .append("cards", hashtodoc(move))
                .append("following",mapFollowing());
        }

        private ArrayList<Document> mapFollowing(){
            ArrayList<Document> temp = new ArrayList<Document>();
            if(!followingPlays.isEmpty()){
                for(Play p: followingPlays){
                    temp.add(p.toDoc());
                }
            }
            return temp;
        }

        private Document hashtodoc(HashMap<String,Integer> map){
            Document temp = new Document();
            for(String x: map.keySet()){
                temp.append(x, map.get(x));
            }
            return temp;
        }
    }

    private class PlayerTurn {
        public String playerName ;
        public ArrayList<Play> plays;

        public PlayerTurn(String playerName ){
            this.playerName = playerName ;
            this.plays = new ArrayList<Play>();
        }

        public Document toDoc(){
            return new Document("name",playerName).append("plays", mapPlays());
        }
        private ArrayList<Document> mapPlays(){
            ArrayList<Document> temp = new ArrayList<Document>();
            if(!plays.isEmpty()){
                for(Play p: plays){
                    temp.add(p.toDoc());
                }
            }
            return temp;
        }
    }



    public int number ;
    private ArrayList<PlayerTurn> turns = new ArrayList<PlayerTurn>();



    public Turn(int number){
        this.number = number ;
    }

    private PlayerTurn getPlayerTurn(String playerName){
        for(PlayerTurn p: turns){
            if(p.playerName == playerName){
                return p;
            }
        }
        return null;
    }

    public void insertMove(String playerName , int level,String type, HashMap<String,Integer> move){
        PlayerTurn playerturn = getPlayerTurn(playerName);
        if(playerturn == null){
            playerturn = new PlayerTurn(playerName);
            playerturn.plays.add(new Play(type,move));
            this.turns.add(playerturn);
        }else{
            if(level == 0){
                playerturn.plays.add(new Play(type,move));
            }else{
                insertPlay(level, type, move, playerturn.plays.get(playerturn.plays.size()-1));
            }
        }
    }

    private Play insertPlay(int level , String type, HashMap<String,Integer> move, Play play){
        if(level == 0){
            return new Play(type, move);
        }else if(level == 1){
            play.followingPlays.add(insertPlay(level-1, type, move,  play));
        }else{
            if(play.followingPlays.isEmpty()){
                System.out.println("THIS SHIT IS EMPTY "+level);
                return play;
            }
            insertPlay(level-1, type, move, play.followingPlays.get(play.followingPlays.size() -1));
        }
        return play;
    }


    public Document toDoc(){
        return new Document("turn",this.number).append("playersMove",mapPlayerTurn());
    }

    private ArrayList<Document> mapPlayerTurn(){
        ArrayList<Document> temp = new ArrayList<Document>();
        if(!turns.isEmpty()){
            for(PlayerTurn p: turns){
                temp.add(p.toDoc());
            }
        }
        return temp;
    }
}
