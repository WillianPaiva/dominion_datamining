import matplotlib.pyplot as plt
import plotly.plotly as plotly
import plotly.graph_objs as graphObjects


def plotLine(xlabel, ylabel, dataSet):
    #TODO: adding coloring options if more than one keyName (to be added in the plt.plot function)
    for keyName in dataSet.getKeys():
        values = __getxyValues(dataSet)
        plt.plot(values[0], values[1])
    plt.xlabel(xlabel)
    plt.ylabel(ylabel)
    plt.show()

def plotCloud(xlabel, ylabel, dataSet):
    for keyName in dataSet.getKeys():
        values = __getxyValues(dataSet)
        plt.plot(values[1], values[1], 'o')
    plt.xlabel(xlabel)
    plt.ylabel(ylabel)
    plt.show()

def plotBar()
    barNames = []
    barValues = []
    for keyName in dataSet.getKeys():
        barNames.append(keyName)
        barValues.append(dataSet.getValuesFromKey(keyName))
    data = [go.Bar(x = barNames, y = barValues)]
    plotly.plot(data)

def __getxyValues (dataSet):
    xValues = []
    yValues = []
    values = dataSet.getValuesFromKey(keyName)
    for tupleValue in values:
        xValues.append(tupleValue[0])
        yValues.append(tupleValue[1])
    return [xValues, yValues]

