package universite.bordeaux.app.plotter;

import java.util.ArrayList;

import org.bson.Document;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

import universite.bordeaux.app.game.Game;
import universite.bordeaux.app.game.GameItf;
import universite.bordeaux.app.game.player.Player;
import universite.bordeaux.app.game.player.PlayerItf;
import universite.bordeaux.app.mapper.MongoMapper;

public class Chart extends ApplicationFrame {
    public XYSeries temp;
    public Chart(String applicationTitle,
                 String chartTitle,
                 String xtitle,
                 String ytitle){
      super(applicationTitle);
      JFreeChart lineChart = ChartFactory.createXYLineChart(
                                                          chartTitle,
                                                          xtitle,ytitle,
                                                          EloChart(),
                                                          PlotOrientation.VERTICAL,
                                                          true,true,false);
      XYSplineRenderer t = new XYSplineRenderer();
      t.setSeriesShapesVisible(0, false);
      t.setSeriesShapesVisible(1, false);
      t.setSeriesShapesVisible(2, false);
      t.setSeriesShapesVisible(3, false);
      t.setSeriesShapesVisible(4, false);
      lineChart.getXYPlot().setRenderer(t);
      ChartPanel chartPanel = new ChartPanel( lineChart );
      chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
      setContentPane( chartPanel );

       }


    public XYDataset EloChart(){
        XYSeriesCollection dataset = new XYSeriesCollection();
        final ArrayList<String> players = MongoMapper.getRank(5);


        for(final String player: players){
            temp = new XYSeries(player);
            FindIterable<Document> it = MongoMapper.getPlayerGames(player);
            final int total = MongoMapper.getPlayerTotalGames(player);
            it.forEach(new Block<Document>() {
                    int x = 0;
                    float test;
                    @Override
                    public void apply(final Document document) {
                        test = ((float)x/(float)total)*100;
                        final GameItf g = new Game(document);
                        final PlayerItf p = g.getPlayer(player);
                        temp.add((double) x,(double)p.getGameElo());
                        x++;
                    }
                });
            dataset.addSeries(temp);
        }
        return dataset;


    }




}
