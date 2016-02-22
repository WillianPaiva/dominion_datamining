package universite.bordeaux.app.game;
import java.util.ArrayList;
import java.util.HashMap;

public class Turn {
    private class Play {
        public String type ;
        public HashMap<String,Integer> move;
        public ArrayList<Play> followingPlays = new ArrayList<Play>();

        public Play(String type, HashMap<String,Integer> move){
            this.type = type ;
            this.move = move;
        }
    }

    private class PlayerTurn {
        public String playerName ;
        public ArrayList<Play> plays = new ArrayList<Play>();

        public PlayerTurn(String playerName ){
            this.playerName = playerName ;
        }
    }



    private int number ;
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
        if(playerturn != null){
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
            insertPlay(level-1, type, move, play.followingPlays.get(play.followingPlays.size() -1));
        }
        return play;
    }


}
