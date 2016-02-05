package reader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import game.Game;
import game.player.Player;





public class ReadGameHead {
    private FileReader reader ;
    private Game game;
    private Document doc;
    private boolean start = true;

    public ReadGameHead(FileReader r, Game g){
        this.game = g;
        this.reader = r ;
    }

    public void startParser(){
        getGameOverall();
        getPlayersData();
        getPlayersFirstHand();
        getTrash();
    }

    private void getGameOverall(){
        if(reader.getScan().hasNextLine()){
            //start the parsing of the game header
            if(reader.jumpline().contains("<title>")){

                //creates a document with the first line of the log
                this.doc = Jsoup.parse(reader.getLine());

                //get the game number
                String title = doc.title();
                String[] parts = title.split("#");
                // game.setGameNumber(Integer.parseInt(parts[1]));

                //find the match winner
                String[] win = doc.body().text().split("wins!");
                if(win[0].contains("rejoice in their shared victory!")){
                    String[] winners = win[0].split("rejoice in their shared victory!")[0].split("and");
                    for(String x: winners){
                        game.insertWinner(x.trim());
                    }
                }else{
                    game.insertWinner(win[0].trim());
                }

                //jumps to the next line
                this.doc = Jsoup.parse(reader.jumpline());

                //finds how the game finished by getting the empty piles
                Elements links = doc.select("span");
                for (Element link : links) {
                    game.insertCardGone(link.text());
                }

                //jumps 1 line
                reader.jumpline();
                this.doc = Jsoup.parse(reader.jumpline());

                //get all the cards available on market
                links = doc.select("span");
                for (Element link : links) {
                    game.insertCardsInSuply(link.text());
                }
            }
        }
        //return the file pointer to the beginning of the file
        reader.rewindFile();
    }


    private void getPlayersData(){
        //jump to the players section of the log
        if(reader.searchLineWithString("(.*)----------------------(.*)")!=null){
            this.doc = Jsoup.parse(reader.getLine());
            if(reader.getLine().contains("----------------------") && start == true){
                start = false;

                //jumps to the first player
                reader.jumpline();
                this.doc = Jsoup.parse(reader.jumpline());
                while(!reader.getLine().contains("----------------------")){

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
                    doc = Jsoup.parse(reader.jumpline());

                    //get opening cards
                    Elements links = doc.select("span");
                    for (Element link : links) {
                        pl.insertOpening(link.text());
                    }

                    //next line
                    doc = Jsoup.parse(reader.jumpline());

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

                    game.insertPlayer(pl);

                    //jumps 1 line
                    reader.jumpline();
                    this.doc = Jsoup.parse(reader.jumpline());
                }
            }

        }
        //return the file pointer to the beginning of the file
        reader.rewindFile();
    }

    private void getTrash(){
        //look for the line that describes the trash
        if(reader.searchLineWithString("trash: (.*)")!=null){
            this.doc = Jsoup.parse(reader.getLine());

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
                    game.insertTrash(qty, card);
                }
            }
        }
        //return the file pointer to the beginning of the file
        reader.rewindFile();
    }

    private void getPlayersFirstHand(){
        //parse the first hand of each player on the match
        if(reader.searchLineWithString("(.*)'s first hand: (.*)")!=null){
            for(int x = 0 ; x < game.getTotalPlayers(); x++){
                this.doc = Jsoup.parse(reader.getLine());
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
                    game.getPlayer(firstHand[0].substring(1)).insertFirstHand(qty,card);
                }

                reader.searchLineWithString("(.*)'s first hand: (.*)");
            }
        }
        //return the file pointer to the beginning of the file
        reader.rewindFile();
    }


    // reader.rewindFile();
    // this.doc = Jsoup.parse(reader.searchLineWithString("(.*)'s turn 1(.*)"));
    // boolean finished = false;
    // int depth = 0;
    // int turn = 1;
    // Turn t = new Turn(turn);
    // TurnsLog log = new TurnsLog();

    // while(finished == false){

    //     if(doc.text().matches("(.*)'s turn [0-9]*(.*)")){
    //         int tempTurn = 0;
    //         if(!doc.text().contains("(possessed by")){
    //             tempTurn = Integereader.parseInt(doc.text().split("'s turn")[1].replaceAll("[^0-9]+",""));
    //         }
    //         if(tempTurn > turn){
    //             turn = tempTurn;
    //             logame.insertPlay(t);
    //             t = new Turn(turn);
    //         }

    //         Player tempPlayer = game.getPlayer(turnGetPlayer(doc.text()));
    //         PlayerTurn pturn = new PlayerTurn(tempPlayer);
    //         this.doc = Jsoup.parse(reader.jumpline());

    //         while(!doc.text().matches("(.*)'s turn [0-9]*(.*)")){

    //             if(doc.text().contains("game aborted: ") ||
    //                doc.text().contains("resigns from the game") ||
    //                doc.text().matches("All [a-z A-z]* are gone.") ||
    //                doc.text().matches("(.*) are all gone.")){

    //                 finished = true;
    //                 break;
    //             }else{

    //                 if(doc.text().contains("plays")){
    //                     Play temp = new Play("play",tempPlayer);
    //                     String cards = doc.text().split("plays")[1].replaceAll("\\.","");
    //                     if(cards.contains(",")){
    //                         cards = cards.replace("and","");
    //                             String[] playedCards = cards.split(",");

    //                             for(String x: playedCards){
    //                                 while(x.charAt(0)==' '){
    //                                     x = x.substring(1);
    //                                 }
    //                                 String[] card = x.split(" ");
    //                                 int qty;
    //                                 try {
    //                                     qty = Integereader.parseInt(card[0]);
    //                                 } catch (NumberFormatException e) {
    //                                     qty = 1;
    //                                 }
    //                                 String c = card[1];
    //                                 temp.insertCard(qty,c);
    //                             }
    //                             pturn.insertPlay(temp);
    //                     }else{
    //                         String[] playedCards = cards.split(" and ");
    //                         for(String x: playedCards){
    //                             int qty;
    //                             try {
    //                                 qty = Integereader.parseInt(x.replaceAll("[^0-9]+",""));
    //                             } catch (NumberFormatException e) {
    //                                 qty = 1;
    //                             }
    //                             String c = x.replaceAll("[0-9]+","").replaceAll("a ","").replaceAll("an ","").replaceAll("^\\s+","");
    //                             temp.insertCard(qty,c);
    //                             System.out.println(c);
    //                         }
    //                         pturn.insertPlay(temp);
    //                     }
    //                 } else if(doc.text().contains("buys")){
    //                     // System.out.println(doc.text()+"BUYS");
    //                 } else if(doc.text().contains("draws")){
    //                     // System.out.println(doc.text()+"DRAWS");
    //                 } else if(doc.text().matches("^... drawing (.*)")){
    //                     // System.out.println(doc.text()+"DRAWING");
    //                 } else if(doc.text().contains("gains")){
    //                     // System.out.println(doc.text()+"GAINS");
    //                 } else if(doc.text().contains("getting")){
    //                     // System.out.println(doc.text()+"GETTING");
    //                 } else if(doc.text().contains("trashes")){
    //                     // System.out.println(doc.text()+"TRASHES");
    //                 }else if(doc.text().contains("putting")){
    //                     // System.out.println(doc.text()+"PUTTING");
    //                 }else if(doc.text().contains("revealing")){
    //                     // System.out.println(doc.text()+"REVEALING");
    //                 }else if(doc.text().contains("reveals")){
    //                     // System.out.println(doc.text()+"REVEALS");
    //                 }else if(doc.text().contains("trashing")){
    //                     // System.out.println(doc.text()+"TRASHING");
    //                 }
    //             }
    //             this.doc = Jsoup.parse(reader.jumpline());
    //         }
    //     }
    // }






    private String turnGetPlayer(String t){
        String[] temp = t.split("'s turn ");
        return temp[0].replace(temp[1]+" ","");
    }


}
