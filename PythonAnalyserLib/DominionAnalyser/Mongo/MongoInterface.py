from pymongo import MongoClient
from bson.objectid import ObjectId


class MongoInterface:
    """Class designed to create a layer of communication between the
    library and mongoDB"""

    def __init__(self):

        #create the client to communicate with mongoDB.
        self.client = MongoClient('localhost', 27020)

        #get the database instance.
        self.db = self.client["game-logs"]

        #get the collection with the players data.
        self.players_col = self.db["players"]

        # get the collection with the full log.
        self.logs_col = self.db["logs"]

    def update_log(self, ident, document):
        """update log document in the database

        it takes the id of the log and the new document representing the log
        and update it into the database
        """
        self.logs_col.update_one({"_id": ident}, {"$set": document})

    def update_player(self, ident, document):
        """update player document in the database

        it takes the id of the player and the new document representing the player
        and update it into the database
        """
        self.players_col.update_one({"id": ident}, {"$set": document})

    def insert_player(self, document):
        """insert player document in the database

        it creates a new player into the database
        """
        self.players_col.insert_one(document)

    def get_log(self, ident):
        """search the database for a log and return it"""
        return self.logs_col.find_one({"_id": ObjectId(ident)})

    def get_player(self, playerName):
        """search the database for a player and return it"""
        return self.players_col.find_one({"name": playerName})

    def create_collection(self, collection):
        """creates a new collection on the database"""
        return self.db[collection]

    def create_index(self, collection, index):
        """creates a new index on a given collection"""
        self.db[collection].create_index(index)
