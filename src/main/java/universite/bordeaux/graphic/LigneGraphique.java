package universite.bordeaux.graphic;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class LigneGraphique {
	
	private LinkedHashMap<String,Double> points;
	private String lineLabel;
	private static int SERIAL_COUNTER = 1;
	private final int id = SERIAL_COUNTER++;
	
	public LigneGraphique(){
		this.points = new LinkedHashMap<String,Double>();
	}
	
	public int getId() {
		return id;
	}
	
	public LinkedHashMap<String, Double> getPoints() {
		return points;
	}
	
	public String getLineLabel() {
		return lineLabel;
	}
	
	public void setLineLabel(String lineLabel) {
		this.lineLabel = lineLabel;
	}
	
	public void addpoint(String label, Double val){
		this.points.put(label, val);
	}
	
	public int getNumbreJoueur() {
		return this.points.size();
	}
	
	public ArrayList<Double> getValsLine() {
		ArrayList<Double> vals = new ArrayList<Double>();
		for (Double val : points.values()) {
			vals.add(val);
		}
		return vals;
	}
	
	public ArrayList<String> getLabelsX() {
		ArrayList<String> vals = new ArrayList<String>();
		for (int i = 0; i < this.points.size(); i++) {
			vals.add(i+"");
			i++;
		}
		return vals;
	}
	
	public ArrayList<String> getLabelsX2() {
		ArrayList<String> vals = new ArrayList<String>();
		for (String val : points.keySet()) {
			vals.add(val);
		}
		return vals;
	}
	
	public int getMaxY() {
		
		int max = 0;
		
		if(points.size() > 1){
			int premier = 0;
			int deuxieme = 0;
			int aux = 0;
			for (Double val : points.values()) {
				aux = val.intValue();
				
				if(deuxieme == 0 && premier == 0){
					premier = aux;
				}else if(aux >= deuxieme && aux < premier ){
					deuxieme = aux;
				}else if(aux >= premier ){
					deuxieme = premier;
					premier = aux;
				}
			}
			
			max = (premier - deuxieme) + premier;
		}else{	
			for (Double val : points.values()) {
				max = val.intValue();
			}
		}
				
		return max;
		
	}

	public ArrayList<String> getLabelsY() {
		ArrayList<String> vals = new ArrayList<String>();
		
		int max = this.getMaxY();
		int dizaine = 10;
		int step =dizaine + 1;
		int i;
		for (i = dizaine; step >= dizaine ; i= i*dizaine ){
			step  = max/i;
			
		}
		step = i/dizaine;
	    int aux = step/2;
	    int max_y = 0;
	    
	    vals.add("0");
	    for (int k=1; max_y < max ; k++){
	    	vals.add(aux+"");
	    	vals.add(step*k+"");
	    	max_y = step*k;
	    	aux = max_y + step/2; 
	    }
		return vals;
	}
	

}
