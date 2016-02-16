package universite.bordeaux.app.elo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.lang.Math;

public final class Elo {

    private Elo(){}

	/**
   * calculates the elo of a match
	 *
   * @param winners list of the winners on the match
   * @param players map of the players on the match with the actual elo
   * @return a map of players with the new elo
	 */
    public static HashMap<String,Integer> Calculate(ArrayList<String> winners, HashMap<String,Integer>players){
        double eloPool = 0;

        //calculates the total elo on the match (the eloPool)
        HashMap<String,Integer> temp = new HashMap<String,Integer>();
        for(Map.Entry<String,Integer> entry: players.entrySet()){
            eloPool += Math.pow(10,entry.getValue()/400);
        }


        //calculates each players elo
        for(Map.Entry<String,Integer> entry: players.entrySet()){
            if(winners.contains(entry.getKey())){
                temp.put(entry.getKey(),(int)(entry.getValue()+((32*(1-(Math.pow(10,entry.getValue()/400)/eloPool)))/winners.size())));
            }else{
                int elo = (int)(entry.getValue()+(32*(0-(Math.pow(10,entry.getValue()/400)/eloPool))));
                elo = (elo >= 0)? elo : 0 ;
                temp.put(entry.getKey(),elo);
            }

        }
        return temp;
    }


}
