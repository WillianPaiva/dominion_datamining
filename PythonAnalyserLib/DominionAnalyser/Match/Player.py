class Player:
    """Player is the class representing the player present inside the log"""

    def __init__(self, document):

        self.playerName = document.get('name')

        #list of victory cards on with quantity
        self.victoryCards = document.get('victorycards')

        self.elo = document.get('elo')

        self.points = document.get('points')

        self.strategy = document.get('strategy')

        self.turns = document.get('turns')
        #list of cards on the player deck
        self.deck = document.get('deck')
        #cards that the player got in the first hand
        self.firsthand = document.get('firsthand')

        #TODO find out what the hell is this information on the log!!!
        self.opening = document.get('opening')

    def toDoc(self):
        """return the document version of this object"""
        document = {"name": self.playerName,
                    "elo": self.elo,
                    "points": self.points,
                    "strategy": self.strategy,
                    "turns": self.turns,
                    "victorycards": self.victoryCards,
                    "deck": self.deck,
                    "firsthand": self.firsthand,
                    "opening": self.opening}
        return document
