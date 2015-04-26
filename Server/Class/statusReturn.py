import logging
import sys
import json
from setting import static_variable



class StatusReturn:
    number = ""
    index = ""

    def __init__(self, numb, function, object_result=0, description=0, data=None):
        self.number = numb
        self.function = function
        self.Object_result = object_result
        self.Description = description
        self.data = data

    def print_general_error(self):
        error_data = {}
        if self.number == 1:
            logging.error("[" + str(self.function) + "] Error 1 --> Can't load json "+str(sys.exc_info()))
            logging.error(self.data)
            error_data["Flag"] = False
            error_data["Description"] = "Can't load json"
            error_data["Object"] = 1

        elif self.number == 2:
            logging.error("[" + str(self.function) + "] Error 2 --> User not found ")
            error_data["Flag"] = False
            error_data["Description"] = "User not found"
            error_data["Object"] = 2

        elif self.number == 3:
            logging.error("[" + str(self.function) + "] Error 3 --> Code not valid")
            error_data["Flag"] = False
            error_data["Description"] = "Code not valid"
            error_data["Object"] = 3

        elif self.number == 4:
            logging.error("[" + str(self.function) + "] Error 4 --> Check code problem")
            error_data["Flag"] = False
            error_data["Description"] = "Check code problem"
            error_data["Object"] = 4

        elif self.number == 5:
            logging.error("[" + str(self.function) + "] Error 5 --> Return value not match")
            error_data["Flag"] = False
            error_data["Description"] = "Return value not match"
            error_data["Object"] = 5

        elif self.number == 6:
            logging.error("[" + str(self.function) + "] Error 6 --> Generic error "+str(sys.exc_info()))
            error_data["Flag"] = False
            error_data["Description"] = "Generic error"
            error_data["Object"] = 6

        elif self.number == 7:
            logging.error("[" + str(self.function) + "] Error 7 --> ERROR ID_group")
            error_data["Flag"] = False
            error_data["Description"] = "ERROR ID_group"
            error_data["Object"] = 7

        elif self.number == 8:
            logging.error("[" + str(self.function) + "] Error 8 --> SysError = " + self.Object_result)
            error_data["Flag"] = False
            error_data["Description"] = "Sys ERROR"
            error_data["Object"] = 8

        else:
            logging.error("[" + str(self.function) + "]: -----ERROR---- "+str(sys.exc_info()))
            error_data["Flag"] = False
            error_data["Description"] = "ERROR"
            error_data["Object"] = None

        return json.dumps(error_data)

    def print_result(self):
        result_data = {}
        if self.number == 1:
            logging.debug("[" + str(self.function) + "] OK 1 --> ID groups ")
            result_data["Flag"] = True
            result_data["Description"] = " ID groups"
            result_data["Object"] = self.Object_result

        elif self.number == 2:
            logging.debug("[" + str(self.function) + "] OK 2 --> CAR by id group ")
            result_data["Flag"] = True
            result_data["Description"] = " Car by id group"
            result_data["Object"] = self.Object_result

        elif self.number == 3:
            logging.debug("[" + str(self.function) + "] OK 3 --> All Car ")
            result_data["Flag"] = True
            result_data["Description"] = " All Car"
            result_data["Object"] = self.Object_result

        elif self.number == 4:
            logging.debug("[" + str(self.function) + "] OK 4 --> createCar ")
            result_data["Flag"] = True
            result_data['Description'] = ' Car created with success'
            result_data["Object"] = self.Object_result

        elif self.number == 5:
            logging.debug("[" + str(self.function) + "] OK 5 --> updateCar ")
            result_data["Flag"] = True
            result_data["Description"] = " Car updated with success"
            result_data["Object"] = self.Object_result

        elif self.number == 6:
            logging.debug("[" + str(self.function) + "] OK 6 --> createCar ")
            result_data["Flag"] = True
            result_data["Description"] = " Group created with success"
            result_data["Object"] = self.Object_result

        elif self.number == 7:
            logging.debug("[" + str(self.function) + "] OK 7 --> deleteCar ")
            result_data["Flag"] = True
            result_data["Description"] = " Car removed with success"
            result_data["Object"] = self.Object_result

        elif self.number == 8:
            logging.debug("[" + str(self.function) + "] OK 8 --> deleteGroup ")
            result_data["Flag"] = True
            result_data["Description"] = " Group removed with success"
            result_data["Object"] = self.Object_result

        elif self.number == 9:
            logging.debug("[" + str(self.function) + "] OK 9 --> Position of the car ")
            result_data["Flag"] = True
            result_data["Description"] = "Position of the car"
            result_data["Object"] = self.Object_result

        elif self.number == 10:
            logging.debug("[" + str(self.function) + "] OK 10 --> Insert contacts in the group ")
            result_data["Flag"] = True
            result_data["Description"] = "Contacts inserted with success"
            result_data["Object"] = self.Object_result

        elif self.number == 11:
            logging.debug("[" + str(self.function) + "] OK 11 --> Confirm code")
            result_data["Flag"] = True
            result_data["Description"] = "Code right"
            result_data["Object"] = self.Object_result

        elif self.number == 12:
            logging.debug("[" + str(self.function) + "] OK 12 --> CAR by email ")
            result_data["Flag"] = True
            result_data["Description"] = " Car by email"
            result_data["Object"] = self.Object_result

        elif self.number == 13:
            logging.debug("[" + str(self.function) + "] OK 13 --> Car connect with group")
            result_data["Flag"] = True
            result_data["Description"] = " Car connect with group"
            result_data["Object"] = self.Object_result

        elif self.number == 14:
            logging.debug("[" + str(self.function) + "] OK 13 --> Delete car connect with group")
            result_data["Flag"] = True
            result_data["Description"] = " Delete car"
            result_data["Object"] = self.Object_result

        elif self.number == 15:
            logging.debug("[" + str(self.function) + "] OK 15 --> Update google code")
            result_data["Flag"] = True
            result_data["Description"] = " Update google code"
            result_data["Object"] = self.Object_result

        elif self.number == 16:
            logging.debug("[" + str(self.function) + "] OK 16 --> Update car")
            result_data["Flag"] = True
            result_data["Description"] = " Update car"
            result_data["Object"] = self.Object_result

        elif self.number == 17:
            logging.debug("[" + str(self.function) + "] OK 17 --> Update group")
            result_data["Flag"] = True
            result_data["Description"] = " Update group"
            result_data["Object"] = self.Object_result

        elif self.number == 18:
            logging.debug("[" + str(self.function) + "] OK 18 --> Delete contacts in the group ")
            result_data["Flag"] = True
            result_data["Description"] = "Contacts deleted with success"
            result_data["Object"] = self.Object_result

        elif self.number == 19:
            logging.debug("[" + str(self.function) + "] OK 19 --> Edit car ")
            result_data["Flag"] = True
            result_data["Description"] = "Car edited with success"
            result_data["Object"] = self.Object_result

        elif self.number == 20:
            logging.debug("[" + str(self.function) + "] OK 20 --> Get Notification ")
            result_data["Flag"] = True
            result_data["Description"] = "Get notification"
            result_data["Object"] = self.Object_result

        elif self.number == 21:
            logging.debug("[" + str(self.function) + "] OK 21 --> Not Notification ")
            result_data["Flag"] = True
            result_data["Description"] = "Not Notification"
            result_data["Object"] = self.Object_result

        elif self.number == 22:
            logging.debug("[" + str(self.function) + "] OK 22 --> Pick the car ")
            result_data["Flag"] = True
            result_data["Description"] = "Pick the car"
            result_data["Object"] = self.Object_result

        if static_variable.DEBUG_STATUS_RETURN:
            logging.debug(result_data)
        return json.dumps(result_data)

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
            logging.debug("Error registration: 1 --> " + str(sys.exc_info()))
            error_data["Flag"] = False
            error_data["Description"] = "Can't load json"
            error_data["Object"] = None
        elif self.number == 2:
            logging.debug("Error registration: 2 --> " + str(sys.exc_info()))
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
        elif self.number == 5:
            logging.debug("Error registration: 5")
            error_data["Flag"] = False
            error_data["Description"] = "Email not valid"
            error_data["Object"] = None
        elif self.number == 6:
            logging.debug("No error: OK")
            error_data["Flag"] = True
            error_data["Description"] = "Social OK"
            error_data["Object"] = self.Object_result
        else:
            logging.debug("No error: OK")
            error_data["Flag"] = True
            error_data["Description"] = "Code sent"
            error_data["Object"] = None

        return json.dumps(error_data)