from DominionAnalyser.Mongo.MongoInterface import MongoInterface
from DominionAnalyser.Match.Match import Match
from DominionAnalyser.Match.Player import Player
from DominionAnalyser.Match.SimplifiedPlayer import SimplifiedPlayer
import pyprind
import math


def apply_function_to_query(function, query):
    """takes a function and apply it to every log returned from the query

    the function argument needs to be a function that takes a match as an
    argument
    """

    n = query.count()
    progress_bar = pyprind.ProgBar(n, monitor=True, width=70)
    for match in query:
        function(match)
        progress_bar.update(item_id=match.get("_id"), force_flush=True)
    print(progress_bar)


def elo_calculator(match):
    """generate the ELO of the match"""

    ELO_FACTOR = 32
    ELO_PONDERATION = 400

    game = Match(match)
    database = MongoInterface()
    playersOldElo = {}
    eloPool = 0
    #collect the base data to calculate the elo
    for pl in game.players:
        player = Player(pl)
        temp = database.get_player(player.playerName)["elo"]
        playersOldElo[player.playerName] = temp
        eloPool += temp
        #calculate each players elo
    for pl in game.players:
        player = Player(pl)
        oldElo = playersOldElo[player.playerName]
        if player.playerName in game.winners:
            newElo = oldElo + ((ELO_FACTOR * (1 - (math.pow(
                10, oldElo / ELO_PONDERATION) / eloPool))) / len(game.winners))
        else:
            newElo = oldElo + (
                (ELO_FACTOR *
                 (0 - (math.pow(10, oldElo / ELO_PONDERATION) / eloPool))))

            database.update_player(player["id"], {"elo": newElo})
            player["Elo"] = newElo


def generate_elo():
    database = MongoInterface()
    apply_function_to_query(elo_calculator,
                            database.logs_col.find().sort("date"))


def generate_simplified_player(match):
    game = Match(match)
    for pl in game.players:
        player = Player(pl)
        splayer = SimplifiedPlayer({"name": player.playerName, "elo": 1000})
        splayer.save(False)


def generate_player_table():
    database = MongoInterface()
    apply_function_to_query(generate_simplified_player,
                            database.logs_col.find())
