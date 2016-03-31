import matplotlib.pyplot as plt
import numpy as np

#the use of the plotting functions must be the following
# xlabel and ylabel: string containing the names fo the x and y axis
# dataset: a DataSet object containing the data to plot
def plot_line(xlabel, ylabel, dataset):
    #TODO: adding coloring options if more than one key_name (to be added in the plt.plot function)
    for key_name in dataset.get_keys():
        values = __getxy_values(dataset, key_name)
        plt.plot(values[0], values[1])
    plt.xlabel(xlabel)
    plt.ylabel(ylabel)
    plt.show()

def plot_cloud(xlabel, ylabel, dataset):
    for key_name in dataset.get_keys():
        values = __getxy_values(dataset, key_name)
        plt.plot(values[0], values[1], 'o')
    plt.xlabel(xlabel)
    plt.ylabel(ylabel)
    plt.show()

def plot_bar(xlabel, ylabel, dataset):
    values = []
    x_tick_labels = []
    bar_width = 0.35
    for key_name in dataset.get_keys():
        values.append(dataset.get_values_from_key(key_name))
        x_tick_labels.append(key_name)
    ind = np.arange(len(values))
    plt.bar(ind, values, bar_width, color = 'r')
    plt.ylabel(ylabel)
    plt.xlabel(xlabel)
    plt.xticks(ind+bar_width/2, x_tick_labels)
    plt.show()

def __getxy_values (dataset, key_name):
    x_values = []
    y_values = []
    values = dataset.get_values_from_key(key_name)
    for tuple_value in values:
        x_values.append(tuple_value[0])
        y_values.append(tuple_value[1])
    return [x_values, y_values]

