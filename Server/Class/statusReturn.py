import logging
import sys
import json


class StatusReturn:
    number = ""
    index = ""

    def __init__(self, numb, function, Object_result=0, Description=0):
        self.number = numb
        self.function = function
        self.Object_result = Object_result
        self.Description = Description

    def print_general_error(self):
        error_data = {}
        if self.number == 1:
            logging.debug("[" + str(self.function) + "] Error 1 --> Can't load json "+ str(sys.exc_info()))
            error_data["Flag"] = False
            error_data["Description"] = "Can't load json"
            error_data["Object"] = 1

        elif self.number == 2:
            logging.debug("[" + str(self.function) + "] Error 2 --> User not found ")
            error_data["Flag"] = False
            error_data["Description"] = "User not found"
            error_data["Object"] = 2

        elif self.number == 3:
            logging.debug("[" + str(self.function) + "] Error 3 --> Code not valid")
            error_data["Flag"] = False
            error_data["Description"] = "Code not valid"
            error_data["Object"] = 3

        elif self.number == 4:
            logging.debug("[" + str(self.function) + "] Error 4 --> Check code problem")
            error_data["Flag"] = False
            error_data["Description"] = "Check code problem"
            error_data["Object"] = 4
        elif self.number == 5:
            logging.debug("[" + str(self.function) + "] Error 5 --> Return value not match")
            error_data["Flag"] = False
            error_data["Description"] = "Return value not match"
            error_data["Object"] = 5
        elif self.number == 6:
            logging.debug("[" + str(self.function) + "] Error 6 --> Generic error "+ str(sys.exc_info()))
            error_data["Flag"] = False
            error_data["Description"] = "Generic error"
            error_data["Object"] = 6
        elif self.number == 7:
            logging.debug("[" + str(self.function) + "] Error 7 --> ERROR ID_group")
            error_data["Flag"] = False
            error_data["Description"] = "ERROR ID_group"
            error_data["Object"] = 7
        elif self.number == 8:
            logging.debug("[" + str(self.function) + "] Error 8 --> SysError = " + self.Object_result)
            error_data["Flag"] = False
            error_data["Description"] = "Sys ERROR"
            error_data["Object"] = 8
        else:
            logging.debug("[" + str(self.function) + "]: -----ERROR---- ")
            error_data["Flag"] = False
            error_data["Description"] = "ERROR"
            error_data["Object"] = None

        return json.dumps(error_data)

    def print_result(self):
        error_data = {}
        if self.number == 1:
            logging.debug("[" + str(self.function) + "] OK 1 --> ID groups ")
            error_data["Flag"] = True
            error_data["Description"] = " ID groups"
            error_data["Object"] = self.Object_result

        elif self.number == 2:
            logging.debug("[" + str(self.function) + "] OK 2 --> CAR by id group ")
            error_data["Flag"] = True
            error_data["Description"] = " Car by id group"
            error_data["Object"] = self.Object_result

        elif self.number == 3:
            logging.debug("[" + str(self.function) + "] OK 3 --> All Car ")
            error_data["Flag"] = True
            error_data["Description"] = " All Car"
            error_data["Object"] = self.Object_result

        elif self.number == 4:
            logging.debug("[" + str(self.function) + "] OK 4 --> createCar ")
            error_data["Flag"] = True
            error_data['Description'] = ' Car created with success'
            error_data["Object"] = self.Object_result

        elif self.number == 5:
            logging.debug("[" + str(self.function) + "] OK 5 --> updateCar ")
            error_data["Flag"] = True
            error_data["Description"] = " Car updated with success"
            error_data["Object"] = self.Object_result

        elif self.number == 6:
            logging.debug("[" + str(self.function) + "] OK 6 --> createCar ")
            error_data["Flag"] = True
            error_data["Description"] = " Group created with success"
            error_data["Object"] = self.Object_result

        elif self.number == 7:
            logging.debug("[" + str(self.function) + "] OK 7 --> deleteCar ")
            error_data["Flag"] = True
            error_data["Description"] = " Car removed with success"
            error_data["Object"] = self.Object_result

        elif self.number == 8:
            logging.debug("[" + str(self.function) + "] OK 8 --> deleteGroup ")
            error_data["Flag"] = True
            error_data["Description"] = " Group removed with success"
            error_data["Object"] = self.Object_result

        elif self.number == 9:
            logging.debug("[" + str(self.function) + "] OK 9 --> Position of the car ")
            error_data["Flag"] = True
            error_data["Description"] = "Position of the car"
            error_data["Object"] = self.Object_result

        elif self.number == 10:
            logging.debug("[" + str(self.function) + "] OK 10 --> Insert contacts in the group ")
            error_data["Flag"] = True
            error_data["Description"] = "Contacts inserted with success"
            error_data["Object"] = self.Object_result

        elif self.number == 11:
            logging.debug("[" + str(self.function) + "] OK 11 --> Confirm code")
            error_data["Flag"] = True
            error_data["Description"] = "Code right"
            error_data["Object"] = self.Object_result

        elif self.number == 12:
            logging.debug("[" + str(self.function) + "] OK 2 --> CAR by email ")
            error_data["Flag"] = True
            error_data["Description"] = " Car by email"
            error_data["Object"] = self.Object_result

        elif self.number == 13:
            logging.debug("[" + str(self.function) + "] OK 13 --> Car connect with group")
            error_data["Flag"] = True
            error_data["Description"] = " Car connect with group"
            error_data["Object"] = self.Object_result

        elif self.number == 14:
            logging.debug("[" + str(self.function) + "] OK 13 --> Delete car connect with group")
            error_data["Flag"] = True
            error_data["Description"] = " Delete car"
            error_data["Object"] = self.Object_result

        elif self.number == 15:
            logging.debug("[" + str(self.function) + "] OK 15 --> Update google code")
            error_data["Flag"] = True
            error_data["Description"] = " Update google code"
            error_data["Object"] = self.Object_result

        elif self.number == 16:
            logging.debug("[" + str(self.function) + "] OK 16 --> Update car")
            error_data["Flag"] = True
            error_data["Description"] = " Update car"
            error_data["Object"] = self.Object_result

        elif self.number == 17:
            logging.debug("[" + str(self.function) + "] OK 17 --> Update group")
            error_data["Flag"] = True
            error_data["Description"] = " Update group"
            error_data["Object"] = self.Object_result

        elif self.number == 18:
            logging.debug("[" + str(self.function) + "] OK 10 --> Delete contacts in the group ")
            error_data["Flag"] = True
            error_data["Description"] = "Contacts deleted with success"
            error_data["Object"] = self.Object_result
        logging.debug(error_data)
        return json.dumps(error_data)

    def toJSON(self):
        error_data = {}
        if self.number == 1:
            logging.debug("Send Email Error: 1")
            error_data["Flag"] = False
            error_data["Description"] = "Can't load json"
            error_data["Object"] = None
        elif self.number == 2:
            logging.debug("Send Email Error: 2")
            error_data["Flag"] = False
            error_data["Description"] = "Can't send the position"
            error_data["Object"] = None
        elif self.number == 3:
            logging.debug("Send Email Error: 3")
            error_data["Flag"] = False
            error_data["Description"] = "Can't send the " + self.index + "email"
            error_data["Object"] = None
        elif self.number == 4:
            logging.debug("Send Email Error: 4")
            error_data["Flag"] = False
            error_data["Description"] = "Can't update the position"
            error_data["Object"] = None
        else:
            logging.debug("Send Email OK")
            error_data["Flag"] = True
            error_data["Description"] = "Email sent"
            error_data["Object"] = None

        return json.dumps(error_data)

    def error_registration(self):
        error_data = {}
        if self.number == 1:
            logging.debug("Error registration: 1 --> "+ str(sys.exc_info()))
            error_data["Flag"] = False
            error_data["Description"] = "Can't load json"
            error_data["Object"] = None
        elif self.number == 2:
            logging.debug("Error registration: 2 --> "+ str(sys.exc_info()))
            error_data["Flag"] = False
            error_data["Description"] = "Enable to create User"
            error_data["Object"] = None
        elif self.number == 3:
            logging.debug("Error registration: 3")
            error_data["Flag"] = False
            error_data["Description"] = "Can't send the email"
            error_data["Object"] = None
        elif self.number == 4:
            logging.debug("Error registration: 4")
            error_data["Flag"] = False
            error_data["Description"] = "Can't search inside DB"
            error_data["Object"] = None

        else:
            logging.debug("No error: OK")
            error_data["Flag"] = True
            error_data["Description"] = "Code sent"
            error_data["Object"] = None

        return json.dumps(error_data)
