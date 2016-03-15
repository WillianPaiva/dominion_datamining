from DominionDataAnalyser.MongoInterface import *

class SimplifiedPlayer:
    def __init__(self,document):
        self.id = document["_id"]
        self.name = document["name"]
        self.elo = document["elo"]

    def save(self):
        """update the player on the database"""
        document = {"name":self.name,
                    "elo":self.elo}
        updatePlayer(self.id,document)
