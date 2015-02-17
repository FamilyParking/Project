import logging
import sys


class StatusReturn:
    number = ""
    index = ""

    def __init__(self, numb, function, object_result=0, description=0):
        self.number = numb
        self.function = function
        self.object_result = object_result
        self.description = description

    def print_general_error(self):
        error_data = {}
        if self.number == 1:
            logging.debug("[" + str(self.function) + "] Error 1 --> Can't load json ")
            error_data["flag"] = False
            error_data["description"] = "Can't load json"
            error_data["object"] = 1

        elif self.number == 2:
            logging.debug("[" + str(self.function) + "] Error 2 --> User not found ")
            error_data["flag"] = False
            error_data["description"] = "User not found"
            error_data["object"] = 2

        elif self.number == 3:
            logging.debug("[" + str(self.function) + "] Error 3 --> Code not valid")
            error_data["flag"] = False
            error_data["description"] = "Code not valid"
            error_data["object"] = 3

        elif self.number == 4:
            logging.debug("[" + str(self.function) + "] Error 4 --> Check code problem")
            error_data["flag"] = False
            error_data["description"] = "Check code problem"
            error_data["object"] = 4
        elif self.number == 5:
            logging.debug("[" + str(self.function) + "] Error 5 --> Return value not match")
            error_data["flag"] = False
            error_data["description"] = "Return value not match"
            error_data["object"] = 5
        elif self.number == 6:
            logging.debug("[" + str(self.function) + "] Error 6 --> Generic error "+ str(sys.exc_info()))
            error_data["flag"] = False
            error_data["description"] = "Generic error"
            error_data["object"] = 6
        elif self.number == 7:
            logging.debug("[" + str(self.function) + "] Error 7 --> ERROR ID_group")
            error_data["flag"] = False
            error_data["description"] = "ERROR ID_group"
            error_data["object"] = 7
        elif self.number == 8:
            logging.debug("[" + str(self.function) + "] Error 8 --> SysError = " + self.object_result)
            error_data["flag"] = False
            error_data["description"] = "Sys ERROR"
            error_data["object"] = 8
        else:
            logging.debug("[" + str(self.function) + "]: -----ERROR---- ")
            error_data["flag"] = False
            error_data["description"] = "ERROR"
            error_data["object"] = None

        return error_data

    def print_result(self):
        error_data = {}
        if self.number == 1:
            logging.debug("[" + str(self.function) + "] OK 1 --> ID groups ")
            error_data["flag"] = True
            error_data["description"] = " ID groups"
            error_data["object"] = self.object_result

        elif self.number == 2:
            logging.debug("[" + str(self.function) + "] OK 2 --> CAR by id group ")
            error_data["flag"] = True
            error_data["description"] = " Car by id group"
            error_data["object"] = self.object_result

        elif self.number == 3:
            logging.debug("[" + str(self.function) + "] OK 3 --> All Car ")
            error_data["flag"] = True
            error_data["description"] = " All Car"
            error_data["object"] = self.object_result

        elif self.number == 4:
            logging.debug("[" + str(self.function) + "] OK 4 --> createCar ")
            error_data["flag"] = True
            error_data["description"] = " Car created with success"
            error_data["object"] = self.object_result

        elif self.number == 5:
            logging.debug("[" + str(self.function) + "] OK 5 --> updateCar ")
            error_data["flag"] = True
            error_data["description"] = " Car updated with success"
            error_data["object"] = self.object_result

        elif self.number == 6:
            logging.debug("[" + str(self.function) + "] OK 6 --> createCar ")
            error_data["flag"] = True
            error_data["description"] = " Group created with success"
            error_data["object"] = self.object_result

        elif self.number == 7:
            logging.debug("[" + str(self.function) + "] OK 7 --> deleteCar ")
            error_data["flag"] = True
            error_data["description"] = " Car removed with success"
            error_data["object"] = self.object_result

        elif self.number == 8:
            logging.debug("[" + str(self.function) + "] OK 8 --> deleteGroup ")
            error_data["flag"] = True
            error_data["description"] = " Group removed with success"
            error_data["object"] = self.object_result

        elif self.number == 9:
            logging.debug("[" + str(self.function) + "] OK 9 --> Position of the car ")
            error_data["flag"] = True
            error_data["description"] = "Position of the car"
            error_data["object"] = self.object_result

        elif self.number == 10:
            logging.debug("[" + str(self.function) + "] OK 10 --> Insert contacts in the group ")
            error_data["flag"] = True
            error_data["description"] = "Contacts inserted with success"
            error_data["object"] = self.object_result

        elif self.number == 11:
            logging.debug("[" + str(self.function) + "] OK 11 --> Confirm code")
            error_data["flag"] = True
            error_data["description"] = "Code right"
            error_data["object"] = self.object_result

        elif self.number == 12:
            logging.debug("[" + str(self.function) + "] OK 2 --> CAR by email ")
            error_data["flag"] = True
            error_data["description"] = " Car by email"
            error_data["object"] = self.object_result

        elif self.number == 13:
            logging.debug("[" + str(self.function) + "] OK 13 --> Car connect with group")
            error_data["flag"] = True
            error_data["description"] = " Car connect with group"
            error_data["object"] = self.object_result

        return error_data

    def toJSON(self):
        error_data = {}
        if self.number == 1:
            logging.debug("Send Email Error: 1")
            error_data["flag"] = False
            error_data["description"] = "Can't load json"
            error_data["object"] = None
        elif self.number == 2:
            logging.debug("Send Email Error: 2")
            error_data["flag"] = False
            error_data["description"] = "Can't send the position"
            error_data["object"] = None
        elif self.number == 3:
            logging.debug("Send Email Error: 3")
            error_data["flag"] = False
            error_data["description"] = "Can't send the " + self.index + "email"
            error_data["object"] = None
        elif self.number == 4:
            logging.debug("Send Email Error: 4")
            error_data["flag"] = False
            error_data["description"] = "Can't update the position"
            error_data["object"] = None
        else:
            logging.debug("Send Email OK")
            error_data["flag"] = True
            error_data["description"] = "Email sent"
            error_data["object"] = None

        return error_data

    def error_registration(self):
        error_data = {}
        if self.number == 1:
            logging.debug("Error registration: 1 --> "+ str(sys.exc_info()))
            error_data["flag"] = False
            error_data["description"] = "Can't load json"
            error_data["object"] = None
        elif self.number == 2:
            logging.debug("Error registration: 2 --> "+ str(sys.exc_info()))
            error_data["flag"] = False
            error_data["description"] = "Enable to create User"
            error_data["object"] = None
        elif self.number == 3:
            logging.debug("Error registration: 3")
            error_data["flag"] = False
            error_data["description"] = "Can't send the email"
            error_data["object"] = None
        elif self.number == 4:
            logging.debug("Error registration: 4")
            error_data["flag"] = False
            error_data["description"] = "Can't search inside DB"
            error_data["object"] = None

        else:
            logging.debug("No error: OK")
            error_data["flag"] = True
            error_data["description"] = "Code sent"
            error_data["object"] = None

        return error_data
