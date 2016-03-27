from DominionDataAnalyser.MongoInterface import *
from DominionDataAnalyser.Player import *

class Match:
    ELO_FACTOR = 32
    ELO_PONDERATION = 400
    def __init__(self, document):

        #the log id on the mongoDB database.
        self.id = document["_id"]

        #list of the winners .
        self.winners = document["winners"]

        #list of empty piles on the game.
        self.cardsGonne = document["cardsgonne"]

        #list of cards available on the game.
        self.market = document["market"]

        #list of all the players on the match.
        self.players = document["players"]

        #list of cards on the trash.
        self.trash = document["trash"]

        #date and time of the game.
        self.dateTime = document["date"]

        #the difference between the highest and lowest ELO in the match.
        self.eloGap = document["eloGap"]

        #the step by step of all moves done in the game.
        self.log = document["log"]

        #the name of the parsed file.
        self.fileName = document["filename"]


    def toDoc(self):
        """save the object into the database"""
        document = {"date":self.dateTime,
                    "filename":self.fileName,
                    "eloGap":self.eloGap,
                    "winners":self.winners,
                    "cardsgonne":self.cardsGonne,
                    "market":self.market,
                    "trash":self.trash,
                    "players":self.players,
                    "log":self.log}
        return document

    def genereteElo(self):
        """generate the ELO of the match"""
        playersOldElo = {}
        eloPool = 0

        #collect the base data to calculate the elo
        for player in self.players:
            temp = getPlayer(player["name"])["elo"]
            playerOldElo[player["name"]] = temp
            eloPool  += temp

        #calculate each players elo
        for player in self.players:
            oldElo =playersOldElo[player["name"]]
            if player["name"] in winners:
                newElo= oldElo + (
                    (ELO_FACTOR * (1 - (math.pow(10,oldElo/ELO_PONDERATION) / eloPool ))) / len(self.winners))
            else:
                newElo= oldElo + (
                    (ELO_FACTOR * (0 - (math.pow(10,oldElo/ELO_PONDERATION) / eloPool ))))

            updatePlayer(player["id"],{"elo":newElo})
            player["Elo"] = newElo
