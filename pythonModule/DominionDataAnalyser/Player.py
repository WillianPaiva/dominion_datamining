
class Player:
    def __init__(self,document):

        BASE_ELO = 1000
        self.playerName = document["name"]

        #list of victory cards on with quantity
        self.victoryCards= document["victorycards"]

        self.elo = document["elo"]

        self.points = document["points"]

        self.strategy = document["strategy"]

        self.turns = document["turns"]
        #list of cards on the player deck
        self.deck = document[deck]
        #cards that the player got in the first hand
        self.firsthand = document["firsthand"]

        #TODO find out what the hell is this information on the log!!!
        self.opening = document["opening"]


    def toDoc(self):
        """return the document version of this object"""
        document = {"name":self.playerName,
                    "elo":self.elo,
                    "points":self.points,
                    "stratgy":self.strategy,
                    "turns":self.turns,
                    "victorycards":self.victoryCards,
                    "deck":self.deck,
                    "firsthand":self.firsthand,
                    "opening":self.opening
        }
        return document
