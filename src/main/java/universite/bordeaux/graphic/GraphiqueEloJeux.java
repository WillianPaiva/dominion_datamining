package universite.bordeaux.graphic;

import java.util.ArrayList;
import java.util.Iterator;

public class GraphiqueEloJeux implements AcquerirDonnesGraphique{
	
	private ArrayList<LigneGraphique> lignes ;
	private String axex;
	private String axey;
	private String title;
	
	public GraphiqueEloJeux() {
		lignes = new ArrayList<LigneGraphique>();		
	}

	public String getAxex() {
		return axex;
	}

	public void setAxex(String axex) {
		this.axex = axex;
	}

	public String getAxey() {
		return axey;
	}

	public void setAxey(String axey) {
		this.axey = axey;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public ArrayList<LigneGraphique> getLignes() {
		return lignes;
	}
	
	public void addligne(LigneGraphique l){
		this.lignes.add(l);
	}

	public LigneGraphique getLigneId(int id){
		LigneGraphique ligne = null;
		for (Iterator iterator = lignes.iterator(); iterator.hasNext();) {
			LigneGraphique ligneGraphique = (LigneGraphique) iterator.next();
			if(ligneGraphique.getId() == id){
				ligne = ligneGraphique;
				break;
			}
		}
		return ligne;
	}
	/* 
	 * return les valeurs y de la ligne
	 */
	@Override
	public ArrayList<Double> getValsLine(int id) {
		LigneGraphique ligne = this.getLigneId(id);
		return (ligne.getValsLine());
	}

	@Override
	public ArrayList<String> getLabelsX(int id) {
		LigneGraphique ligne = this.getLigneId(id);
		return (ligne.getLabelsX());
	}

	@Override
	public ArrayList<String> getLabelsX2(int id) {
		LigneGraphique ligne = this.getLigneId(id);
		return (ligne.getLabelsX2());
	}

	@Override
	public String getLabelx() {
		return this.axex;
	}
	

	@Override
	public ArrayList<String> getLabelsY() {
		int id_max = this.getIdMaxY();
		LigneGraphique ligne = this.getLigneId(id_max);
		return ligne.getLabelsY();
	}

	@Override
	public ArrayList<String> getLabelsY2(int id) {
		return null;
	}

	@Override
	public String getLabely() {
		return this.axey;
	}

	@Override
	public int getIdMaxY() {	
		int max = 0;
		int id_max = 0;
		for (Iterator iterator = lignes.iterator(); iterator.hasNext();) {
			LigneGraphique ligneGraphique = (LigneGraphique) iterator.next();
			if (max < ligneGraphique.getMaxY()){
				max = ligneGraphique.getMaxY();
				id_max = ligneGraphique.getId();
			}
		}
		return id_max;
	}
	
	@Override
	public int getMaxY() {	
		int max = 0;
		for (Iterator iterator = lignes.iterator(); iterator.hasNext();) {
			LigneGraphique ligneGraphique = (LigneGraphique) iterator.next();
			if (max < ligneGraphique.getMaxY()){
				max = ligneGraphique.getMaxY();
			
			}
		}
		return max;
	}
	
	
	public String getTitle() {
		return title;
	}

	public String getLineLabel(int id) {
		LigneGraphique ligne = this.getLigneId(id);
		return (ligne.getLineLabel());
		
	}

}
