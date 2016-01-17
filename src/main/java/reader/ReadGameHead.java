package reader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import game.Game;
import game.Play;
import game.PlayerTurn;
import game.Turn;
import game.TurnsLog;
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
            if(r.searchLineWithString("(.*)----------------------(.*)")!=null){
                this.doc = Jsoup.parse(r.getLine());
                if(r.getLine().contains("----------------------") && start == true){
                    start = false;

                    //jumps to the first player
                    r.jumpline();
                    this.doc = Jsoup.parse(r.jumpline());
                    while(!r.getLine().contains("----------------------")){

                        //create the player
                        Player pl;
                        String name;
                        if(doc.select("b").text().contains("points")){
                            name = doc.select("b").text().split(":")[0];
                        }else{
                            name = doc.select("b").text().replaceAll("^#[0-9]* ","");
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
                }

            }
                //return the file pointer to the beginning of the file
                r.rewindFile();

                //look for the line that describes the trash
                if(r.searchLineWithString("trash: (.*)")!=null){
                    this.doc = Jsoup.parse(r.getLine());

                    //parse the line trash and add the list to the game object
                    if(!doc.text().contains("nothing")){
                        String[] trash = doc.text().replace("trash: ","").replace("and","").split(",");
                        for(String x: trash){
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
                            g.insertTrash(qty, card);
                        }
                    }
                }

                //return the file pointer to the beginning of the file
                r.rewindFile();

                //parse the first hand of each player on the match
                if(r.searchLineWithString("(.*)'s first hand: (.*)")!=null){
                    for(int x = 0 ; x < g.getTotalPlayers(); x++){
                        this.doc = Jsoup.parse(r.getLine());
                        String[] firstHand = doc.text().split("'s first hand: ");
                        for(String y: firstHand[1].replace(".)", "").split("and")){
                            while(y.charAt(0)==' '){
                                y = y.substring(1);
                            }
                            String[] cards = y.split(" ");
                            int qty;
                            try{
                                qty = Integer.parseInt(cards[0]);
                            } catch (NumberFormatException e){
                                qty = 1;
                            }
                            String card = cards[1];
                            g.getPlayer(firstHand[0].substring(1)).insertFirstHand(qty,card);
                        }

                        r.searchLineWithString("(.*)'s first hand: (.*)");
                    }
                }

                r.rewindFile();
                this.doc = Jsoup.parse(r.searchLineWithString("(.*)'s turn 1(.*)"));
                boolean finished = false;
                int depth = 0;
                int turn = 1;
                Turn t = new Turn(turn);
                TurnsLog log = new TurnsLog();

                while(finished == false){

                    if(doc.text().matches("(.*)'s turn [0-9]*(.*)")){
                        int tempTurn = 0;
                        if(!doc.text().contains("(possessed by")){
                            tempTurn = Integer.parseInt(doc.text().split("'s turn")[1].replaceAll("[^0-9]+",""));
                        }
                        if(tempTurn > turn){
                            turn = tempTurn;
                            log.insertPlay(t);
                            t = new Turn(turn);
                        }

                        Player tempPlayer = g.getPlayer(turnGetPlayer(doc.text()));
                        PlayerTurn pturn = new PlayerTurn(tempPlayer);
                        this.doc = Jsoup.parse(r.jumpline());

                        while(!doc.text().matches("(.*)'s turn [0-9]*(.*)")){

                            if(doc.text().contains("game aborted: ") ||
                               doc.text().contains("resigns from the game") ||
                               doc.text().matches("All [a-z A-z]* are gone.") ||
                               doc.text().matches("(.*) are all gone.")){

                                finished = true;
                                break;
                            }else{

                                if(doc.text().contains("plays")){
                                    Play temp = new Play("play",tempPlayer);
                                    String cards = doc.text().split("plays")[1].replaceAll("\\.","");
                                    if(cards.contains(",")){
                                        cards = cards.replace("and","");
                                            String[] playedCards = cards.split(",");

                                            for(String x: playedCards){
                                                while(x.charAt(0)==' '){
                                                    x = x.substring(1);
                                                }
                                                String[] card = x.split(" ");
                                                int qty;
                                                try {
                                                    qty = Integer.parseInt(card[0]);
                                                } catch (NumberFormatException e) {
                                                    qty = 1;
                                                }
                                                String c = card[1];
                                                temp.insertCard(qty,c);
                                            }
                                            pturn.insertPlay(temp);
                                    }else{
                                        String[] playedCards = cards.split(" and ");
                                        for(String x: playedCards){
                                            int qty;
                                            try {
                                                qty = Integer.parseInt(x.replaceAll("[^0-9]+",""));
                                            } catch (NumberFormatException e) {
                                                qty = 1;
                                            }
                                            String c = x.replaceAll("[0-9]+","").replaceAll("a ","").replaceAll("an ","").replaceAll("^\\s+","");
                                            temp.insertCard(qty,c);
                                            System.out.println(c);
                                        }
                                        pturn.insertPlay(temp);
                                    }
                                } else if(doc.text().contains("buys")){
                                    // System.out.println(doc.text()+"BUYS");
                                } else if(doc.text().contains("draws")){
                                    // System.out.println(doc.text()+"DRAWS");
                                } else if(doc.text().matches("^... drawing (.*)")){
                                    // System.out.println(doc.text()+"DRAWING");
                                } else if(doc.text().contains("gains")){
                                    // System.out.println(doc.text()+"GAINS");
                                } else if(doc.text().contains("getting")){
                                    // System.out.println(doc.text()+"GETTING");
                                } else if(doc.text().contains("trashes")){
                                    // System.out.println(doc.text()+"TRASHES");
                                }else if(doc.text().contains("putting")){
                                    // System.out.println(doc.text()+"PUTTING");
                                }else if(doc.text().contains("revealing")){
                                    // System.out.println(doc.text()+"REVEALING");
                                }else if(doc.text().contains("reveals")){
                                    // System.out.println(doc.text()+"REVEALS");
                                }else if(doc.text().contains("trashing")){
                                    // System.out.println(doc.text()+"TRASHING");
                                }
                            }
                            this.doc = Jsoup.parse(r.jumpline());
                        }
                    }
                }







        }else{
            System.out.println("ERROR EMPTYFILE");
        }

    }

    private String turnGetPlayer(String t){
        String[] temp = t.split("'s turn ");
        return temp[0].replace(temp[1]+" ","");
    }
}

