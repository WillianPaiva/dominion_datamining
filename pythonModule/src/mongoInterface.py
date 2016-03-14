from pymongo import MongoClient
from bson.objectid import ObjectId

class MongoInterface:
    def __init__(self):

        #create the client to communicate with mongoDB.
        client = MongoClient('localhost',27020)

        #get the database instance.
        db = client["game-logs"]

        #get the collection with the players data.
        players_col = db["players"]

        # get the collection with the full log.
        logs_col = db["logs"]

    def updateLog(self,id,document):
        """update log document in the database"""
        self.logs_col.update_one(
            {"_id": ObjectId(id)},
            {"&set":document}
        )

    def updatePlayer(self,id,document):
        """update player document in the database"""
        self.players.update_one(
            {"id":PbjectId(id)},
            {"&set":document}
        )

    def getLog(self,id):
        """search the database for a log and return it"""
        return self.logs_col.find_one({"_id":ObjectId(id)})

    def getPlayer(self,playerName):
        """search the database for a player and return it"""
        return self.players_col.find_one({"name":playerName})

