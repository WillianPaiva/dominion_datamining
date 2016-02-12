package universite.bordeaux.graphic;

import java.util.ArrayList;

public interface AcquerirDonnesGraphique {
	
	public LigneGraphique getLigneId(int id);
		
	public int getIdMaxY();
	
	public int getMaxY();
	
	public ArrayList<Double> getValsLine(int id);
	
	public ArrayList<String> getLabelsX(int id);
	
	public ArrayList<String> getLabelsX2(int id);
	
	public String getLabelx();
	
	public ArrayList<String> getLabelsY(); 
	
	public ArrayList<String> getLabelsY2(int id);
	
	public String getLabely();
	
	public String getTitle();

	public String getLineLabel(int id);
	
	
}
