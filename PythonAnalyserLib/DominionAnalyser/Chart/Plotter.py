import matplotlib.pyplot as plt
import numpy as np


def plotLine(xlabel, ylabel, dataSet):
    #TODO: adding coloring options if more than one keyName (to be added in the plt.plot function)
    for keyName in dataSet.getKeys():
        values = __getxyValues(dataSet, keyName)
        plt.plot(values[0], values[1])
    plt.xlabel(xlabel)
    plt.ylabel(ylabel)
    plt.show()

def plotCloud(xlabel, ylabel, dataSet):
    for keyName in dataSet.getKeys():
        values = __getxyValues(dataSet, keyName)
        plt.plot(values[1], values[1], 'o')
    plt.xlabel(xlabel)
    plt.ylabel(ylabel)
    plt.show()

def plotBar(ylabel, dataSet):
    values = []
    xTickLabels = []
    for keyName in dataSet.getKeys():
        values.append(dataSet.getValuesFromKey(keyName))
        xTickLabels.append(keyName)
    ind = np.arange(len(values))
    print len(values)
    plt.bar(ind, values)
    plt.set_xticklabels(xTickLabels)
    plt.show

def __getxyValues (dataSet, keyName):
    xValues = []
    yValues = []
    values = dataSet.getValuesFromKey(keyName)
    for tupleValue in values:
        xValues.append(tupleValue[0])
        yValues.append(tupleValue[1])
    return [xValues, yValues]

