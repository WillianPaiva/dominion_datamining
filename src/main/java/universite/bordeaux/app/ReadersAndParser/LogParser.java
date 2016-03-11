package universite.bordeaux.app.ReadersAndParser;

import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import universite.bordeaux.app.GameDataStructure.GameTurn;


public final class LogParser {
    /**
     * {@inheritDoc}
     * @see Object#ReadGameLog()
     */
    private LogParser(){}

    public static ArrayList<GameTurn> getGameLog(FileReader reader){
        Document doc;
        ArrayList<GameTurn> turns = new ArrayList<>();
        boolean finished = false;
        int last = 0;
        int turn = 1;
        GameTurn t = new GameTurn(turn);
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
                    t = new GameTurn(turn);
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
                    if(!doc.text().contains("nothing") && !doc.text().contains("reshuffles.)")){

                        //gets the plays move
                        if(reader.getLine().matches("(.*) plays (a|the|an|[0-9]+) \\<span class(.*)")){
                            String cards = "";
                            if(!doc.text().matches("^\\.\\.\\.(.*)") && playername.contains("plays")){
                                cards = doc.text().trim().replace(playername+" plays ","").replaceAll("\\.","");
                            }else{
                                cards = doc.text().split(" plays ")[1].replaceAll("\\.","");
                            }
                            cards = cards.replace("again","").replace("a third time","");
                            int level = 0;
                            String countLevel = doc.text();
                            while(countLevel.matches("\\.\\.\\.(.*)")){
                                countLevel = countLevel.substring(3);
                                level++;
                            }
                            t.insertMove(playername,level,"plays",getCards(cards));
                            break;

                            //gets the buys move
                        }else if(reader.getLine().matches("(.*) buys (a|an|[0-9]+) \\<span class(.*)")){
                            String cards = "";
                            if(!doc.text().matches("^\\.\\.\\.(.*)") && playername.contains("buys")){
                                cards = doc.text().trim().replace(playername+" buys ","").replaceAll("\\.","");
                            }else{
                                cards = doc.text().split(" buys ")[1].replaceAll("\\.","");
                            }
                            int level = 0;
                            String countLevel = doc.text();
                            while(countLevel.matches("\\.\\.\\.(.*)")){
                                countLevel = countLevel.substring(3);
                                level++;
                            }
                            t.insertMove(playername,level,"buys",getCards(cards));
                            break;

                            //gets the draws on the finish of the turn
                        } else if(reader.getLine().trim().matches("\\<span class\\=logonly\\>\\((.*) draws: (.*)" )){
                            String cards =  doc.text().trim().split(" draws: ")[1].replaceAll("\\.\\)", "");
                            t.insertMove(playername,0,"draws",getCards(cards));
                            break;

                            //gets the move that consists in draw and discard a same card
                        } else if(reader.getLine().trim().matches("(.*) draws and discard (a|an|[0-9]+) \\<span class(.*)")){
                            String cards =  doc.text().trim().split(" draws and discard ")[1].replaceAll("\\.", "");
                            int level = 0;
                            String countLevel = doc.text();
                            while(countLevel.matches("\\.\\.\\.(.*)")){
                                countLevel = countLevel.substring(3);
                                level++;
                            }
                            t.insertMove(playername,level,"draws",getCards(cards));
                            t.insertMove(playername,level,"discard",getCards(cards));
                            break;

                            //gets the gains action
                        } else if(reader.getLine().matches("(.*) gains (a|an|[0-9]+) \\<span class(.*)")){
                            String cards =  doc.text().trim().split(" gains ")[1].replaceAll("\\.", "").replaceAll(" on top of the deck","").replaceAll(" on the deck", "").replaceAll(" to replace it", "");
                            int level = 0;
                            String countLevel = doc.text();
                            while(countLevel.matches("\\.\\.\\.(.*)")){
                                countLevel = countLevel.substring(3);
                                level++;
                            }
                            t.insertMove(playername,level,"gains",getCards(cards));
                            break;
                            //gets the double action discards and gains
                        } else if(reader.getLine().matches("(.*) discards (a|an|[0-9]+) \\<span class(.*)")){
                            String cards =  doc.text().trim().split(" discards ")[1].replaceAll("\\.", "").replaceAll("on the deck","");
                            if(cards.contains(" and gains ")){
                                String[] actionList = cards.split(" and gains ");
                                int level = 0;
                                String countLevel = doc.text();
                                while(countLevel.matches("\\.\\.\\.(.*)")){
                                    countLevel = countLevel.substring(3);
                                    level++;
                                }
                                t.insertMove(playername,level,"discards",getCards(actionList[0]));
                                t.insertMove(playername,level,"gains",getCards(actionList[1]));
                            }else{
                                int level = 0;
                                String countLevel = doc.text();
                                while(countLevel.matches("\\.\\.\\.(.*)")){
                                    countLevel = countLevel.substring(3);
                                    level++;
                                }
                                t.insertMove(playername,level,"discards",getCards(cards));
                            }
                            break;
                            //get trashes
                        } else if(reader.getLine().matches("(.*) trashes (a|an|[0-9]+) \\<span class(.*)")){
                            String cards =  doc.text().trim().split(" trashes ")[1].replaceAll("and gets (.*)\\.", "").replaceAll("\\.", "");
                            if(cards.contains("gaining")){
                                String[] actionList = cards.split(", gaining ");
                                int level = 0;
                                String countLevel = doc.text();
                                while(countLevel.matches("\\.\\.\\.(.*)")){
                                    countLevel = countLevel.substring(3);
                                    level++;
                                }
                                t.insertMove(playername,level,"trashes",getCards(actionList[0]));
                                t.insertMove(playername,level,"gains",getCards(actionList[1].replaceAll(" in hand","")));
                            }else{
                                int level = 0;
                                String countLevel = doc.text();
                                while(countLevel.matches("\\.\\.\\.(.*)")){
                                    countLevel = countLevel.substring(3);
                                    level++;
                                }
                                t.insertMove(playername,level,"trashes",getCards(cards));
                            }
                            break;
                            // get reveals
                        }else if(reader.getLine().matches("(.*) reveals (a|an|[0-9]+) \\<span class(.*)")){
                            String cards =  doc.text().trim().split(" reveals ")[1].replaceAll("\\.", "");
                            boolean trashes = false;
                            if(cards.contains("and trashes it")){
                                trashes = true;
                                cards = cards.replaceAll("and trashes it","");
                            }
                            cards = cards.replaceAll(" and then ",",").replaceAll(", and ",",").replaceAll(" and ", ",");
                            int level = 0;
                            String countLevel = doc.text();
                            while(countLevel.matches("\\.\\.\\.(.*)")){
                                countLevel = countLevel.substring(3);
                                level++;
                            }
                            t.insertMove(playername,level,"reveals",getCards(cards));
                            if(trashes){
                                t.insertMove(playername,level,"trashes",getCards(cards));
                            }
                            break;


                            //get the trashing action
                        }else if(reader.getLine().trim().matches("(.*) trashing (a|an|the|[0-9]+) \\<span class(.*)")){
                            String cards =  reader.getLine().trim().split(" trashing ")[1].replaceAll("(\\</span\\>) (for|from).*\\.$", "$1");
                            cards = Jsoup.parse(cards).text().replaceAll("\\.", "").replaceAll("the", "a");
                            int level = 0;
                            String countLevel = doc.text();
                            while(countLevel.matches("\\.\\.\\.(.*)")){
                                countLevel = countLevel.substring(3);
                                level++;
                            }
                            t.insertMove(playername,level,"trashes",getCards(cards));
                            break;

                        }else if(reader.getLine().trim().matches("(.*) revealing (a|an|[0-9]+) \\<span class(.*)")){
                            String cards =  doc.text().trim().split(" revealing ")[1].replaceAll("\\.", "");
                            int level = 0;
                            String countLevel = doc.text();
                            while(countLevel.matches("\\.\\.\\.(.*)")){
                                countLevel = countLevel.substring(3);
                                level++;
                            }
                            if(cards.contains(" and putting it in the hand")){
                                cards = cards.replaceAll(" and putting it in the hand","");
                                t.insertMove(playername,level,"revealing",getCards(cards));
                                t.insertMove(playername,level,"putting",getCards(cards));
                            }else{
                                t.insertMove(playername,level,"revealing",getCards(cards));
                            }
                            break;
                        }else if(reader.getLine().trim().matches("(.*) putting (a|the|an|[0-9]+) \\<span class(.*)")){
                            String cards =  doc.text().trim().split(" putting ")[1].replaceAll("\\.", "").replaceAll("into the hand", "");
                            int level = 0;
                            String countLevel = doc.text();
                            while(countLevel.matches("\\.\\.\\.(.*)")){
                                countLevel = countLevel.substring(3);
                                level++;
                            }
                            t.insertMove(playername,level,"putting",getCards(cards));
                            break;
                        }else if(reader.getLine().trim().matches("(.*) gaining (a|the|another|an|[0-9]+) \\<span class(.*)")){
                            String cards =  doc.text().trim().split(" gaining ")[1].replaceAll("\\.", "").replaceAll("another", "a");
                            int level = 0;
                            String countLevel = doc.text();
                            while(countLevel.matches("\\.\\.\\.(.*)")){
                                countLevel = countLevel.substring(3);
                                level++;
                            }
                            t.insertMove(playername,level,"gains",getCards(cards));
                            break;
                        }else if(reader.getLine().trim().matches("(.*) discarding (a|an|[0-9]+)+ \\<span class(.*)")){
                            String cards =  doc.text().trim().split(" discarding ")[1].replaceAll("\\.", "").replaceAll("the ([0-9]+)", "$1").replaceAll("and the","a");
                            int level = 0;
                            String countLevel = doc.text();
                            while(countLevel.matches("\\.\\.\\.(.*)")){
                                countLevel = countLevel.substring(3);
                                level++;
                            }
                            t.insertMove(playername,level,"discarding",getCards(cards));
                            break;
                        }else if(reader.getLine().trim().matches("(.*) playing (a|an|[0-9]+)+ \\<span class(.*)")){
                            String cards =  doc.text().trim().split(" playing ")[1].replaceAll("\\.", "");
                            int level = 0;
                            String countLevel = doc.text();
                            while(countLevel.matches("\\.\\.\\.(.*)")){
                                countLevel = countLevel.substring(3);
                                level++;
                            }
                            t.insertMove(playername,level,"playing",getCards(cards));
                            break;
                        }else if(reader.getLine().trim().matches("(.*) drawing (a|an|[0-9]+)+ \\<span class(.*)")){
                            String cards =  doc.text().trim().split(" drawing ")[1].replaceAll("\\.", "").replaceAll("from the Black Market deck", "");
                            int level = 0;
                            String countLevel = doc.text();
                            while(countLevel.matches("\\.\\.\\.(.*)")){
                                countLevel = countLevel.substring(3);
                                level++;
                            }
                            t.insertMove(playername,level,"draws",getCards(cards));
                            break;
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

    private static HashMap<String,Integer> getCards(String cards){
        HashMap<String,Integer> move = new HashMap<>();
        if(cards.contains(",")){
            cards = cards.replace("and","");
            String[] playedCards = cards.split(",");
            for(String x: playedCards){
                x = x.trim();
                String[] card = x.split(" ",4);
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
                String[] card = x.split(" ",4);
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
        return move;
    }
}
