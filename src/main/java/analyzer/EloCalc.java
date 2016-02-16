package analyzer;

public class EloCalc {
	void processElo(String[] winners, String[] players){
		if (winners.length == 1 & players.length == 2){//implementation for 1v1
			for(int i = 0; i < players.length; i++){
				String oponent;
				if (i < players.length-1)
					oponent = players[i+1];
				else
					oponent = players[0];
				double expectedScore = 1/(1 + Math.pow( 10, ( (getElo(oponent)-getElo(players[i]) )/400)));
				if (players[i] == winners[0]){
					setElo (players[i], getElo(players[i] + 32 * (1 - expectedScore)));
				}
				else
					setElo (players[i], getElo(players[i] + 32 * (0 - expectedScore)));
			}
		}
	}
	
	int getElo(String player){
		return 0;
	}
	void setElo(String player, double newScore){
		
	}
}
