from DominionAnalyser.Match.Match import Match
from DominionAnalyser.Match.Player import Player
from DominionAnalyser.Match.Log import *
from DominionAnalyser.Match.SimplifiedPlayer import SimplifiedPlayer
from DominionAnalyser.Mongo import MongoInterface
from collections import Counter
import pyprind
import math

victory_cards = ["estate", "duchy", "province", "colony", "vineyard",
                 "greathall", "tunnel", "gardens", "island", "silkroad",
                 "feodum", "duke", "damejosephine", "distantlands", "harem",
                 "nobles", "fairgrounds", "farmland"]


def apply_function_to_query(function, query, progress_bar=True):
    """takes a function and apply it to every log returned from the query

    the function argument needs to be a function that takes a match as an
    argument
    """

    n = query.count()
    progress_bar = pyprind.ProgBar(n, monitor=True, width=70)
    for match in query:
        function(match)
        if (progress_bar):
            progress_bar.update(item_id=match.get("_id"), force_flush=True)
    print(progress_bar)


def elo_calculator(match):
    """generate the ELO of the match"""

    ELO_FACTOR = 32
    ELO_PONDERATION = 400

    game = Match(match)
    playersOldElo = {}
    eloPool = 0
    #collect the base data to calculate the elo
    for player in game.players:
        temp = MongoInterface.get_player(player.playerName).get('elo')
        playersOldElo[player.playerName] = temp
        eloPool += temp
    #calculate each players elo
    for player in game.players:

        splayer = SimplifiedPlayer(MongoInterface.get_player(
            player.playerName))

        oldElo = playersOldElo[player.playerName]

        if player.playerName in game.winners:
            newElo = oldElo + ((ELO_FACTOR * (1 - (math.pow(
                10, oldElo / ELO_PONDERATION) / eloPool))) / len(game.winners))
        else:
            newElo = oldElo + (
                (ELO_FACTOR *
                 (0 - (math.pow(10, oldElo / ELO_PONDERATION) / eloPool))))

        splayer.elo = newElo
        splayer.save()
        player.elo = newElo
    MongoInterface.update_log(game.ident, game.toDoc())
    return newElo


def generate_elo():
    apply_function_to_query(elo_calculator,
                            MongoInterface.logs_col.find().sort("date"))

def detect_bigmoney_strategy(match):
    game = Match(match)
    is_big_money_flag = True
    for player in game.players:
        deck = player.deck
        for card in deck:
            if not (card in ("province", "gold", "silver", "duchy", "smithy")):
                is_big_money_flag = False
        if is_big_money_flag:
            player.strategy = "bigmoney"
    MongoInterface.update_log(game.ident, game.toDoc())


def detect_strategy_on_all_logs(strategy):
    apply_function_to_query(strategy, MongoInterface.logs_col.find())


def generate_simplified_player(match):
    game = Match(match)
    for pl in game.players:
        player = Player(pl)
        splayer = SimplifiedPlayer({"name": player.playerName, "elo": 1000})
        splayer.save()


def generate_player_table():
    apply_function_to_query(generate_simplified_player,
                            MongoInterface.logs_col.find())


def get_player_elo_data(playerName, match):
    game = Match(match)
    return game.get_player(playerName).elo


def genereate_player_elo_curve(playerName):
    player_curve = []
    apply_function_to_query(
        lambda x: player_curve.append(get_player_elo_data(playerName, x)),
        MongoInterface.logs_col.find({"players.name": playerName}).sort(
            "date"))
    return player_curve
