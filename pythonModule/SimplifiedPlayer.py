from MongoInterface import *

class SimplifiedPlayer:
    def __init__(self,document):
        id = document["_id"]
        name = document["name"]
        elo = document["elo"]

    def save(self):
        """update the player on the database"""
        document = {"name":name,
                    "elo":elo}
        updatePlayer(id,document)
