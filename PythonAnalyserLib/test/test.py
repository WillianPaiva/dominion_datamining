import unittest
import mongomock
from test import LogTest
from DominionAnalyser.Match.Match import Match
from DominionAnalyser.Match.Player import Player
from DominionAnalyser.Analysers.Tools import *
from bson.objectid import ObjectId


class TestMatch(unittest.TestCase):
    def test_player(self):
        m = Match(LogTest.dummy)
        self.assertEqual([p.toDoc() for p in m.players],
                         LogTest.dummy.get("players"))

ma.eloGap = 100

test.updateLog(ma.id, ma.toDoc())

print(ma.id)

# print(ma.players)
for p in ma.players:
    t = Player(p)
    print(t.playerName)

for p in test.logs_col.find():
    ma = Match(p)
    for p in ma.players:
        t = Player(p)
        print(t.playerName)
        print("*********************************************************")
    print("---------------------------------------------------------")
