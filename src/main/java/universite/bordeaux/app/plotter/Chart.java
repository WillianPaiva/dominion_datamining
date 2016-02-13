package universite.bordeaux.app.plotter;

import org.jfree.chart.ChartPanel;

import java.text.SimpleDateFormat;

import org.bson.Document;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;

import com.mongodb.client.FindIterable;
import com.mongodb.Block;

import universite.bordeaux.app.game.Game;
import universite.bordeaux.app.game.player.Player;
import universite.bordeaux.app.mapper.MongoMapper;

import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class Chart extends ApplicationFrame {
    public DefaultCategoryDataset dataset ;

    public Chart(String applicationTitle,
                 String chartTitle,
                 String xtitle,
                 String ytitle,
                 String player){
      super(applicationTitle);
      JFreeChart lineChart = ChartFactory.createLineChart(
                                                          chartTitle,
                                                          xtitle,ytitle,
                                                          EloChart(player),
                                                          PlotOrientation.VERTICAL,
                                                          true,true,false);
      ChartPanel chartPanel = new ChartPanel( lineChart );
      chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
      setContentPane( chartPanel );

       }

    public DefaultCategoryDataset EloChart(final String player){
        dataset = new DefaultCategoryDataset();
        FindIterable it = MongoMapper.getPlayerGames(player);
        it.forEach(new Block<Document>() {

                @Override
                public void apply(final Document document) {
                    Game g = new Game(document);
                    Player p = g.getPlayer(player);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                    String DateToStr = format.format(g.getDate());
                    dataset.addValue(p.getGameElo(),"Elo",DateToStr);

                }
            });
        return dataset;


    }




}
