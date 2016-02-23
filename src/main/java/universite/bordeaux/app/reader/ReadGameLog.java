package universite.bordeaux.app.reader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import universite.bordeaux.app.game.Turn;


public final class ReadGameLog {
    /**
     * {@inheritDoc}
     * @see Object#ReadGameLog()
     */
    private ReadGameLog(){}

    public static ArrayList<Turn> getGameLog(FileReader reader){
        Document doc;
        ArrayList<Turn> turns = new ArrayList<Turn>();
        boolean finished = false;
        int last = 0;
        int turn = 1;
        Turn t = new Turn(turn);
        String playername = "";

        //get the start of the game log
        doc = Jsoup.parse(reader.searchLineWithString("(.*)'s turn 1(.*)"));

        while(finished == false){
            if(doc.text().matches("(.*)'s turn [0-9]+(.*)")){
                int tempTurn = 0;
                if(!doc.text().contains("(possessed by")){
                    tempTurn = Integer.parseInt(doc.text().split("'s turn")[1].replaceAll("[^0-9]+",""));
                }
                if(tempTurn > turn){
                    turn = tempTurn;
                    t = new Turn(turn);
                }
                playername = turnGetPlayer(doc.text());
            }
            doc = Jsoup.parse(reader.jumpline());

            while(!doc.text().matches("(.*)'s turn [0-9]*(.*)")){
                if(doc.text().contains("game aborted: ") ||
                   doc.text().contains("resigns from the game") ||
                   doc.text().matches("All [a-z A-z]* are gone.") ||
                   doc.text().matches("(.*) are all gone.")){
                    finished = true;
                    break;
                }else{
                    HashMap<String,Integer> move = new HashMap<String,Integer>();
                    if(!doc.text().matches("^\\.\\.\\.(.*)")){
                        if(reader.getLine().matches("(.*) plays (a|an|[0-9]*) \\<span class(.*)")){
                            String cards = "";
                            if(!doc.text().matches("^\\.\\.\\.(.*)") && playername.contains("plays")){
                                cards = doc.text().trim().replace(playername+" plays ","").replaceAll("\\.","");
                            }else{
                                cards = doc.text().split(" plays ")[1].replaceAll("\\.","");
                            }
                            cards = cards.replace("again","").replace("a third time","");
                            if(cards.contains(",")){
                                cards = cards.replace("and","");
                                String[] playedCards = cards.split(",");
                                for(String x: playedCards){
                                    x = x.trim();
                                    String[] card = x.split(" ",3);
                                    int qty;
                                    try {
                                        qty = Integer.parseInt(card[0]);
                                    } catch (NumberFormatException e) {
                                        qty = 1;
                                    }
                                    String c = card[1];
                                    move.put(c,qty);
                                }
                            }else{
                                String[] playedCards = cards.split(" and ");
                                for(String x: playedCards){
                                    x = x.trim();
                                    String[] card = x.split(" ",3);
                                    int qty;
                                    try {
                                        qty = Integer.parseInt(card[0]);
                                    } catch (NumberFormatException e) {
                                        qty = 1;
                                    }
                                    String c = card[1];
                                    move.put(c,qty);
                                }
                            }
                            String player = doc.text().split("plays")[0].trim();
                            int level = 0;
                            // if(player.matches("^\\.\\.\\.(.*)")){
                            // player = player.substring(3).trim();
                            // level++;
                            // }
                            t.insertMove(playername,level,"plays",move);

                        }else if(reader.getLine().matches("(.*) buys (a|an|[0-9]*) \\<span class(.*)")){
                            String cards = "";
                            if(!doc.text().matches("^\\.\\.\\.(.*)") && playername.contains("buys")){
                                cards = doc.text().trim().replace(playername+" buys ","").replaceAll("\\.","");
                            }else{
                                cards = doc.text().split(" buys ")[1].replaceAll("\\.","");
                            }
                            if(cards.contains(",")){
                                cards = cards.replace("and","");
                                String[] playedCards = cards.split(",");
                                for(String x: playedCards){
                                    x = x.trim();
                                    String[] card = x.split(" ",3);
                                    int qty;
                                    try {
                                        qty = Integer.parseInt(card[0]);
                                    } catch (NumberFormatException e) {
                                        qty = 1;
                                    }
                                    String c = card[1];
                                    move.put(c,qty);
                                }
                            }else{
                                String[] playedCards = cards.split(" and ");
                                for(String x: playedCards){
                                    x = x.trim();
                                    String[] card = x.split(" ",3);
                                    int qty;
                                    try {
                                        qty = Integer.parseInt(card[0]);
                                    } catch (NumberFormatException e) {
                                        qty = 1;
                                    }
                                    String c = card[1];
                                    move.put(c,qty);
                                }
                            }
                            String player = doc.text().split("buys")[0].trim();
                            int level = 0;
                            // if(player.matches("^\\.\\.\\.(.*)")){
                            // player = player.substring(3).trim();
                            // level++;
                            // }
                            t.insertMove(playername,level,"buys",move);
                        } else if(reader.getLine().trim().matches("\\<span class\\=logonly\\>\\((.*) draws: (.*)" )){
                            String cards =  doc.text().trim().split(" draws: ")[1].replaceAll("\\.\\)", "");
                            if(cards.contains(",")){
                                cards = cards.replace("and","");
                                String[] playedCards = cards.split(",");
                                for(String x: playedCards){
                                    x = x.trim();
                                    String[] card = x.split(" ",3);
                                    int qty;
                                    try {
                                        qty = Integer.parseInt(card[0]);
                                    } catch (NumberFormatException e) {
                                        qty = 1;
                                    }
                                    String c = card[1];
                                    move.put(c,qty);
                                }
                            }else{
                                String[] playedCards = cards.split(" and ");
                                for(String x: playedCards){
                                    x = x.trim();
                                    String[] card = x.split(" ",3);
                                    int qty;
                                    try {
                                        qty = Integer.parseInt(card[0]);
                                    } catch (NumberFormatException e) {
                                        qty = 1;
                                    }
                                    String c = card[1];
                                    move.put(c,qty);
                                }
                            }
                            t.insertMove(playername,0,"draws",move);
                        } else if(doc.text().matches("^... drawing (.*)")){
                        } else if(doc.text().contains("gains")){
                        } else if(doc.text().contains("getting")){
                        } else if(doc.text().contains("trashes")){
                        }else if(doc.text().contains("putting")){
                        }else if(doc.text().contains("revealing")){
                        }else if(doc.text().contains("reveals")){
                        }else if(doc.text().contains("trashing")){
                        }
                    }
                }
                doc = Jsoup.parse(reader.jumpline());
            }
            if(last != turn){
                turns.add(t);
                last = turn;
            }

        }
        return turns;
    }

    private static String turnGetPlayer(String s){
        return s.trim().replaceAll("^—" ,"").trim().replaceAll("'s turn [0-9]*(.*)","").trim();
    }
}
