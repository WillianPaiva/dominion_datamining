package universite.bordeaux.app.elo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.lang.Math;

public final class Elo {

    private Elo(){}

    public static HashMap<String,Integer> Calculate(ArrayList<String> winners, HashMap<String,Integer>players){
        double eloPool = 0;
        HashMap<String,Integer> temp = new HashMap<String,Integer>();
        for(Map.Entry<String,Integer> entry: players.entrySet()){
            eloPool += Math.pow(10,entry.getValue()/400);
        }
        for(Map.Entry<String,Integer> entry: players.entrySet()){
            if(winners.contains(entry.getKey())){
                temp.put(entry.getKey(),(int)(entry.getValue()+((32*(1-(Math.pow(10,entry.getValue()/400)/eloPool)))/winners.size())));
            }else{
                double factor = 1+((double)winners.size()-1)/((double)players.size()-(double)winners.size());
                int elo = (int)(entry.getValue()+((32*(0-(Math.pow(10,entry.getValue()/400)/eloPool)))*factor));
                elo = (elo >= 0)? elo : 0 ;
                temp.put(entry.getKey(),elo);
            }

        }
        return temp;
    }


}
