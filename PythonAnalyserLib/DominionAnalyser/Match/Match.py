class Match:
    def __init__(self, document):

        #the log id on the mongoDB database.
        self.ident = document.get('_id')

        #list of the winners .
        self.winners = document.get('winners')

        #list of empty piles on the game.
        self.cardsGonne = document.get('cardsgonne')

        #list of cards available on the game.
        self.market = document.get('market')

        #list of all the players on the match.
        self.players = document.get('players')

        #list of cards on the trash.
        self.trash = document.get('trash')

        #date and time of the game.
        self.dateTime = document.get('date')

        #the difference between the highest and lowest ELO in the match.
        self.eloGap = document.get('eloGap')

        #the step by step of all moves done in the game.
        self.log = document.get('log')

        #the name of the parsed file.
        self.fileName = document.get('filename')

    def toDoc(self):
        """save the object into the database"""
        document = {"date": self.dateTime,
                    "filename": self.fileName,
                    "eloGap": self.eloGap,
                    "winners": self.winners,
                    "cardsgonne": self.cardsGonne,
                    "market": self.market,
                    "trash": self.trash,
                    "players": self.players,
                    "log": self.log}
        return document
