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

    def test_log(self):
        m = Match(LogTest.dummy)
        self.assertEqual([p.to_doc() for p in m.log.turns],
                         LogTest.dummy.get("log"))

    def test_match_doc_diff(self):
        self.maxDiff = None
        m = Match(LogTest.dummy)
        doc = {"date": LogTest.dummy.get("date"),
               "filename": LogTest.dummy.get("filename"),
               "eloGap": LogTest.dummy.get("eloGap"),
               "winners": LogTest.dummy.get("winners"),
               "cardsgonne": LogTest.dummy.get("cardsgonne"),
               "market": LogTest.dummy.get("market"),
               "trash": LogTest.dummy.get("trash"),
               "players": LogTest.dummy.get("players"),
               "log": LogTest.dummy.get("log")}
        self.assertEqual(m.toDoc(), doc)


class TestTools(unittest.TestCase):
    MongoInterface.client = mongomock.MongoClient()
    MongoInterface.db = MongoInterface.client["game-logs"]
    MongoInterface.players_col = MongoInterface.db["players"]
    MongoInterface.logs_col = MongoInterface.db["logs"]
    MongoInterface.logs_col.insert_one(LogTest.dummy)

    def test_mongomock(self):
        self.assertEqual(MongoInterface.logs_col.count(), 1)
