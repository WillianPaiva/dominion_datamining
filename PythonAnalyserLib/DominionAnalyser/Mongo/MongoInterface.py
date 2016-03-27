from pymongo import MongoClient
from bson.objectid import ObjectId

class MongoInterface:
    def __init__(self):

        #create the client to communicate with mongoDB.
        self.client = MongoClient('localhost',27020)

        #get the database instance.
        self.db = self.client["game-logs"]

        #get the collection with the players data.
        self.players_col = self.db["players"]

        # get the collection with the full log.
        self.logs_col = self.db["logs"]

    def updateLog(self,id,document):
        """update log document in the database"""
        self.logs_col.update_one(
            {"_id": id},
            {"$set":document}
        )

    def updatePlayer(self,id,document):
        """update player document in the database"""
        self.players.update_one(
            {"id":id},
            {"$set":document}
        )

    def getLog(self,id):
        """search the database for a log and return it"""
        return self.logs_col.find_one({"_id":ObjectId(id)})

    def getPlayer(self,playerName):
        """search the database for a player and return it"""
        return self.players_col.find_one({"name":playerName})

    def findOne(self):
        print(self.players_col.find_one())
