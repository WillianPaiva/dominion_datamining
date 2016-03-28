from pymongo import MongoClient
from bson.objectid import ObjectId

#create the client to communicate with mongoDB.
client = MongoClient('localhost', 27020)

#get the database instance.
db = client["game-logs"]

#get the collection with the players data.
players_col = db["players"]

# get the collection with the full log.
logs_col = db["logs"]


def update_log(ident, document):
    """update log document in the database

    it takes the id of the log and the new document representing the log
    and update it into the database
    """
    logs_col.update_one({"_id": ident}, {"$set": document})


def update_player(ident, document):
    """update player document in the database

    it takes the id of the player and the new document representing the player
    and update it into the database
    """
    players_col.update_one({"_id": ident}, {"$set": document})


def insert_player(document):
    """insert player document in the database

    it creates a new player into the database
    """
    players_col.insert_one(document)


def get_log(ident):
    """search the database for a log and return it"""
    return logs_col.find_one({"_id": ObjectId(ident)})


def get_player(playerName):
    """search the database for a player and return it"""
    return players_col.find_one({"name": playerName})


def create_collection(collection):
    """creates a new collection on the database"""
    return db[collection]


def create_index(collection, index):
    """creates a new index on a given collection"""
    db[collection].create_index(index)
