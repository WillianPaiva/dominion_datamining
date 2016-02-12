package universite.bordeaux.graphic;


import com.googlecode.charts4j.UrlUtil;


public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GraphiqueEloJeux graphique = new GraphiqueEloJeux();
		
		LigneGraphique l1 = new LigneGraphique();
		/*
		l1.addpoint("TheMango", new Double(2760));
		l1.addpoint("HotBid", new Double(2640));
		l1.addpoint("Robot", new Double(2415));
		l1.addpoint("steve", new Double(2105));
		l1.addpoint("joe", new Double(1980));
		l1.addpoint("Wurial", new Double(1860));
		l1.addpoint("blerg", new Double(1620));
		l1.setLineLabel("Points");
		*/
		l1.addpoint("OCT-2010", new Double(23));
		l1.addpoint("NOV.2010", new Double(19));
		l1.addpoint("DIC-2010", new Double(40));
		l1.addpoint("ENE-2011", new Double(34));
		l1.addpoint("FEB-2011", new Double(30));
		l1.addpoint("MAR-2011", new Double(24));
		l1.addpoint("ABR-2011", new Double(17));
		l1.setLineLabel("TheMango");
		graphique.addligne(l1);
		
		LigneGraphique l2 = new LigneGraphique();
		l2.addpoint("OCT-2010", new Double(200));
		l2.addpoint("NOV.2010", new Double(208));
		l2.addpoint("DIC-2010", new Double(212));
		l2.addpoint("ENE-2011", new Double(281));
		l2.addpoint("FEB-2011", new Double(290));
		l2.addpoint("MAR-2011", new Double(300));
		l2.addpoint("ABR-2011", new Double(317));
		l2.setLineLabel("ELO GAME");
		graphique.addligne(l2);
		
		graphique.setAxex("Date");
		graphique.setAxey("Elo");
		graphique.setTitle("Classement ELO"); 
		
		Plotting elo = new Plotting(graphique);
		String url = elo.getChartToutesLignes();		
		
		
		url = UrlUtil.normalize(url);
		
		System.out.println(url);

	}

}
