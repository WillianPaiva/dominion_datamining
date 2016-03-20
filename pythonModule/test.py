from DominionDataAnalyser.Match import *
from DominionDataAnalyser.MongoInterface import *

test = MongoInterface()

ma = Match(test.logs_col.find_one())

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
