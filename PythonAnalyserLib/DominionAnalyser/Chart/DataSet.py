class DataSet:


# The DataSet objects must be used as follows
# key_name must be a string
# values can be a single value or a tuple of two values
    def __init__(self):

        self.dataset_dictionary = dict()

    def add_entry(self, key_name, values):

        if not(key_name == ""):
            if not(self.dataset_dictionary.has_key(key_name)):
                self.dataset_dictionary[key_name] = values
            else:
                self.dataset_dictionary[key_name].append(values)

    def delete_entry(self, key_name):

        del self.dataset_dictionary[key_name]

    def get_keys(self):

        return self.dataset_dictionary.keys()

    def get_values_from_key(self, key_name):

        if len(self.dataset_dictionary[key_name]) == 1:
            return self.dataset_dictionary[key_name][0]
        else:
            return self.dataset_dictionary[key_name]
