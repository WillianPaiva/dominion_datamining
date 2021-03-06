from DominionAnalyser.Mongo import MongoInterface


class SimplifiedPlayer:
    """SimplifiedPlayer is the class that represents the player with less information

    this class is designed to represent just the player and its Elo
    it is used to create a separated collection on the database, with
    the intention of creating a leader board """

    def __init__(self, document):
        self.id = document.get('id')
        self.name = document.get('name')
        self.elo = document.get('elo')

    def save(self):
        """insert or update the player on the database

        this function will search the database on the players collection
        and see if player exists if it already exists and 'update' is true it
        will update the player data on the database else it does nothing,
        and if player don't exists it saves the new player
        """
        player = MongoInterface.get_player(self.name)
        document = {"name": self.name, "elo": self.elo}

        if player is None:
            MongoInterface.insert_player(document)
        else:
            MongoInterface.update_player(player["_id"], document)
