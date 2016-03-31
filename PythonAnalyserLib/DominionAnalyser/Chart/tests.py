from Plotter import *
from DataSet import *

data = DataSet()

data.add_entry("testPlot", [(1, 1),(2, 2), (3, 3)])
plot_line("x axis", "y axis", data)

plot_cloud("x axis", "y axis", data)

data2 = DataSet()
data2.add_entry("one", [3])
data2.add_entry("two", [4])
data2.add_entry("three", [5])
plot_bar("xaxis", "yaxis", data2)
