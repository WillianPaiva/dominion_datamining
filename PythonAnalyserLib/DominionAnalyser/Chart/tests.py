from Plotter import *
from DataSet import *

data = DataSet()

data.addEntry("testPlot", [(1, 1),(2, 2), (3, 3)])
plotLine("x axis", "y axis", data)

data2 = DataSet()
data2.addEntry("one", [3])
data2.addEntry("two", [4])
plotBar("yaxis", data2)
