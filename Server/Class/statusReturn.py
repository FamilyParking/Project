import logging


class StatusReturn:
    number = ""
    index = ""

    def __init__(self,numb,ind):
        self.number=numb
        self.index=ind

    def toJSON(self):
        error_data = {}
        if self.number == 1:
            logging.debug("Send Email Error: 1")
            error_data["flag"] = False
            error_data["description"] = "Can't load json"
            error_data["object"] = None
        if self.number == 2:
            logging.debug("Send Email Error: 2")
            error_data["flag"] = False
            error_data["description"] = "Can't send the position"
            error_data["object"] = None
        if self.number == 3:
            logging.debug("Send Email Error: 3")
            error_data["flag"] = False
            error_data["description"] = "Can't send the " + self.index + "email"
            error_data["object"] = None
        if self.number == 4:
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
            logging.debug("Error registration: 1")
            error_data["flag"] = False
            error_data["description"] = "Can't load json"
            error_data["object"] = None
        if self.number == 2:
            logging.debug("Error registration: 2")
            error_data["flag"] = False
            error_data["description"] = "Enable to create User"
            error_data["object"] = None
        if self.number == 3:
            logging.debug("Error registration: 3")
            error_data["flag"] = False
            error_data["description"] = "Can't send the email"
            error_data["object"] = None
        if self.number == 4:
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
