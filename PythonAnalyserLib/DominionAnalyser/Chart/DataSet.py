class DataSet:

    def __init__(self):

        dataSetDictionary = dict()

    def addEntry(self, keyName, values):

        if !(keyName == ""):
            if !self.dataSetDictionary.has_key(keyName):
                self.dataSetDictionary[keyName] = values
            else:
                self.dataSetDictionary[keyName].append(values)

    def deleteEntry(self, keyName):

        del self.dataSetDictionary[keyName]

    def getKeys(self):

        return self.dataSetDictionary.keys()

    def getValuesFromKey(self, keyName):

        return self.dataSetDictionary[keyName]
