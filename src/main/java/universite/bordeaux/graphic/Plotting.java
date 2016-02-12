package universite.bordeaux.graphic;

import static com.googlecode.charts4j.Color.*;

import java.util.ArrayList;
import java.util.Iterator;

import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.AxisStyle;
import com.googlecode.charts4j.AxisTextAlignment;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.DataUtil;
import com.googlecode.charts4j.Fills;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.Line;
import com.googlecode.charts4j.LineChart;
import com.googlecode.charts4j.LinearGradientFill;
import com.googlecode.charts4j.Plots;
import com.googlecode.charts4j.Shape;

public class Plotting {
	
	private GraphiqueEloJeux donnes;

	public Plotting(GraphiqueEloJeux graphique){
		this.donnes = graphique;
	}

    private String dessiner(ArrayList<Line> lines) {
    	
    	donnes.getLignes();
    	
    	// Defining axis info and styles
        AxisStyle axisStyle = AxisStyle.newAxisStyle(ORANGE, 14, AxisTextAlignment.CENTER);
        AxisLabels xAxis = AxisLabelsFactory.newAxisLabels(donnes.getLignes().iterator().next().getLabelsX());
        xAxis.setAxisStyle(axisStyle);
        AxisLabels xAxis2 = AxisLabelsFactory.newAxisLabels(donnes.getLignes().iterator().next().getLabelsX2());
        xAxis2.setAxisStyle(axisStyle);
        AxisLabels xAxis3 = AxisLabelsFactory.newAxisLabels(donnes.getLabelx(), 50.0);
        xAxis3.setAxisStyle(AxisStyle.newAxisStyle(WHITE, 14, AxisTextAlignment.CENTER));
    	
    	LineChart chart = GCharts.newLineChart(lines);
    	chart.setSize(600, 450);
        chart.setTitle(donnes.getTitle(), WHITE, 14);
        //chart.addHorizontalRangeMarker(40, 60, Color.newColor(RED, 30));
        //chart.addVerticalRangeMarker(70, 90, Color.newColor(GREEN, 30));
        chart.setGrid(10, 10, 5, 5);
    	
        AxisLabels yAxis = AxisLabelsFactory.newAxisLabels(donnes.getLabelsY()); 
        yAxis.setAxisStyle(axisStyle);
        AxisLabels yAxis2 = AxisLabelsFactory.newAxisLabels(donnes.getLabely(), 50.0);
        yAxis2.setAxisStyle(AxisStyle.newAxisStyle(WHITE, 14, AxisTextAlignment.CENTER));
        yAxis2.setAxisStyle(axisStyle);

        // Adding axis info to chart.
        chart.addXAxisLabels(xAxis);
        chart.addXAxisLabels(xAxis2);
        chart.addXAxisLabels(xAxis3);
        chart.addYAxisLabels(yAxis);
        chart.addYAxisLabels(yAxis2);

        // Defining background and chart fills.
        chart.setBackgroundFill(Fills.newSolidFill(Color.newColor("1F1D1D")));
        LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.newColor("363433"), 100);
        fill.addColorAndOffset(Color.newColor("2E2B2A"), 0);
        chart.setAreaFill(fill);
        String url = chart.toURLString();
        
        return url;
       
    }
    
    public String getChartToutesLignes(){
    	
    	ArrayList<Line> lines = new ArrayList<Line>();
    	for (Iterator iterator = donnes.getLignes().iterator(); iterator.hasNext();) {
			LigneGraphique ligne = (LigneGraphique) iterator.next();
			
			Line line1 = Plots.newLine(DataUtil.scaleWithinRange(0,donnes.getMaxY(),ligne.getValsLine()), Color.YELLOW, ligne.getLineLabel());
			//line1.setLineStyle(LineStyle.newLineStyle(3, 1, 0));
	        //line1.addShapeMarkers(Shape.DIAMOND, Color.RED, 8);
			line1.addShapeMarkers(Shape.DIAMOND, Color.WHITE, 8);
			lines.add(line1);
		}
    	
    	return this.dessiner(lines);
    	    	
    }
    
    public String getChartPremieresLignes(){
    		return "";
    }
    
    public String getChartDernieresLignes(){
		return "";
    }
    
    public String getChartLignesApartirPosition(int position){
		return "";
    }

}
