package reader;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import game.Game;
import game.player.Player;




public class ReadGameHead {
    private FileReader r ;
    private Game g;
    private Document doc;
    private boolean start = true;

    public ReadGameHead(FileReader r, Game g){
        this.g = g;
        this.r = r ;
        if(r.getScan().hasNextLine()){
            //start the parsing of the game header
            if(r.jumpline().contains("<title>")){

                //creates a document with the first line of the log
                this.doc = Jsoup.parse(r.getLine());

                //get the game number
                String title = doc.title();
                String[] parts = title.split("#");
                g.setGameNumber(Integer.parseInt(parts[1]));

                //find the match winner
                String[] win = doc.body().text().split("wins!");
                if(win[0].contains("rejoice in their shared victory!")){
                    String[] winners = win[0].split("rejoice in their shared victory!")[0].split("and");
                    for(String x: winners){
                        g.insertWinner(x);
                    }
                }else{
                    g.insertWinner(win[0]);
                }


                //jumps to the next line
                this.doc = Jsoup.parse(r.jumpline());


                //finds how the game finished by getting the empty piles
                Elements links = doc.select("span");
                for (Element link : links) {
                    g.insertCardGone(link.text());
                }
                //jumps 1 line
                r.jumpline();
                this.doc = Jsoup.parse(r.jumpline());

                //get all the cards available on market

                links = doc.select("span");
                for (Element link : links) {
                    g.insertCardsInSuply(link.text());
                }
            }

            //jump to the players section of the log
            this.doc = Jsoup.parse(r.searchLineWithString("(.*)-----------(.*)"));
            if(r.getLine().contains("------------") && start == true){
                start = false;

                //jumps to the first player
                r.jumpline();
                this.doc = Jsoup.parse(r.jumpline());
                while(!r.getLine().contains("----------")){

                    //create the player
                    Player pl;
                    String name;
                    if(doc.select("b").text().contains("points")){
                        name = doc.select("b").text().split(":")[0];
                    }else{
                        name = doc.select("b").text().replaceAll("#[0-9]* ","");
                    }

                    pl = new Player(name);

                    //break the string into 2 parts to find the points
                    if(doc.text().contains("points")){
                        String[] firstBreak = doc.text().split("points");

                        //set the player points
                        String[] p = firstBreak[0].split(":");
                        String temp = p[p.length-1].replace(" ","");
                        if(temp.contains("resigned")){
                            pl.setPoints(0);
                        }else{
                            pl.setPoints(Integer.parseInt(temp));
                        }

                        //split the string to get all victory points cards
                        String list = firstBreak[1].split(";")[0];
                        list = list.substring(2,list.length()-1).replace("and","");
                        if(!list.contains("nothing")){
                            String[] victoryCards = list.split(",");

                            //insert the victorycards on the player object
                            for(String x: victoryCards){
                                while(x.charAt(0)==' '){
                                    x = x.substring(1);
                                }
                                String[] cards = x.split(" ");
                                int qty;
                                try {
                                    qty = Integer.parseInt(cards[0]);
                                } catch (NumberFormatException e) {
                                    qty = 1;
                                }
                                String card = cards[1];
                                pl.insertVictoryCard(qty,card);
                            }

                        }
                        //get the turns
                        pl.setTurns(Integer.parseInt(firstBreak[1].split(";")[1].replace(" turns","").replace(" ","")));
                    }

                        //get next line
                        doc = Jsoup.parse(r.jumpline());

                        //get opening cards
                        Elements links = doc.select("span");
                        for (Element link : links) {
                            pl.insertOpening(link.text());
                        }

                        //next line
                        doc = Jsoup.parse(r.jumpline());

                        //get deck cards
                        if(!doc.text().contains("0 cards")){
                            String[] deck = doc.text().split("\\[[0-9]* cards\\]")[1].split(",");
                            for(String x: deck){
                                while(x.charAt(0)==' '){
                                    x = x.substring(1);
                                }
                                String[] cards = x.split(" ");
                                int qty;
                                try {
                                    qty = Integer.parseInt(cards[0]);
                                } catch (NumberFormatException e) {
                                    qty = 1;
                                }
                                String card = cards[1];
                                pl.insertDeck(qty,card);
                            }
                        }

                    g.insertPlayer(pl);

                    //jumps 1 line
                    r.jumpline();
                    this.doc = Jsoup.parse(r.jumpline());
                }

                r.rewindFile();
                this.doc = Jsoup.parse(r.searchLineWithString("trash: (.*)"));
                System.out.println(doc.text());

            }
        }else{
            System.out.println("ERROR EMPTYFILE");
        }

    }
}
