import logging
import json
import sys
import datetime
import webapp2
import random

from decimal import Decimal

from Class.statusReturn import StatusReturn

from Cloud_Storage.car_group import Car_group
from Cloud_Storage.user import User
from Cloud_Storage.car import Car
from Cloud_Storage.group import Group
from Cloud_Storage.user_copy import User_copy
from Cloud_Storage.user_group import User_group

from google.appengine.api import mail

from Tool.send_email import Send_email
from Tool.user_tool import User_tool


class MainPage(webapp2.RequestHandler):
    def get(self):
        in_file = open("website/index.html", "r")
        MAIN_PAGE_HTML = in_file.read()
        self.response.write(MAIN_PAGE_HTML)
        in_file.close()

class SendEmail(webapp2.RequestHandler):
    def post(self):
        logging.debug("Received from user: " + str(self.request.body))
        try:
            data = json.loads(self.request.body)
            new_contact = User_copy(id_android=data["ID"], counter=1, latitude=data["latitude"],
                                    longitude=data["longitude"], last_time=str(datetime.datetime.now()))
            new_car = Car(model="testModel", latitude=data["latitude"],
                          longitude=data["longitude"], timestamp=str(datetime.datetime.now()))
            # logging.debug(new_car)
            try:
                contact_key = new_contact.querySearch()
            # logging.debug(contact_key.get())
            except:
                logging.debug(sys.exc_info())

            try:
                if contact_key.count() == 0:
                    add_db_contact = new_contact.put()
                else:
                    temp_contact = contact_key.get()
                    # logging.debug(new_car.getPositionFromID())

                    if bool(temp_contact.update_contact(data["latitude"], data["longitude"])):
                        try:
                            message = mail.EmailMessage(sender="Family Parking <familyparkingapp@gmail.com>",
                                                        subject="Position of car")
                            message.body = "Your car is parked here: http://www.google.com/maps/place/" + data[
                                "latitude"] + "," + \
                                           data["longitude"]
                            receiver_mail = data["email"]
                            i = 0
                            for value in receiver_mail:
                                try:
                                    message.to = "<" + value + ">"
                                    message.send()
                                    i += 1
                                except:
                                    self.error(500)
                                    error = StatusReturn(3, i)
                                    self.response.write(error.toJSON())
                            right = StatusReturn(0, 0)
                            self.response.write(right.toJSON())
                        except:
                            self.error(500)
                            error = StatusReturn(2, 0)
                            self.response.write(error.toJSON())
                    else:
                        self.error(500)
                        error = StatusReturn(4, 0)
                        self.response.write(error.toJSON())
                new_car.put()
                new_car_group = Car_group(id_car=int(new_car.key.id()), id_group=868686868)
                new_car_group.put()
            except:
                logging.debug(sys.exc_info())

        except:
            self.error(500)
            error = StatusReturn(1, 0)
            self.response.write(error.toJSON())

class getIDGroups(webapp2.RequestHandler):
    def post(self):
        try:
            data = json.loads(self.request.body)
            code = int(data["Code"])
            try:
                result_check_code = User_tool.check_code(data["Email"], code)

                if result_check_code == -2:
                    self.error(500)
                    error = StatusReturn(2, "getIDGroups")
                    self.response.write(error.print_general_error())

                elif result_check_code == -1:
                    self.error(500)
                    error = StatusReturn(3, "getIDGroups")
                    self.response.write(error.print_general_error())

                elif result_check_code >= 0:
                    try:
                        id_groups = User_tool.return_groups(data["Email"])
                        if (id_groups > 0):
                            all_id_group = "["
                            for key in id_groups:
                                if all_id_group != "[":
                                    all_id_group = all_id_group + ",\"" + str(key.id_group) + "\""
                                else:
                                    all_id_group = all_id_group + "\"" + str(key.id_group) + "\""
                            all_id_group += "]"
                            result_json = StatusReturn(1, "getIDGroups", all_id_group)
                            self.response.write(result_json.print_result())
                        else:
                            self.error(500)
                            error = StatusReturn(5, "getIDGroups")
                            self.response.write(error.print_general_error())
                    except:
                        self.error(500)
                        error = StatusReturn(6, "getIDGroups")
                        self.response.write(error.print_general_error())
                else:
                    self.error(500)
                    error = StatusReturn(5, "getIDGroups")
                    self.response.write(error.print_general_error())
            except:
                self.error(500)
                error = StatusReturn(4, "getIDGroups")
                self.response.write(error.print_general_error())
        except:
            self.error(500)
            error = StatusReturn(1, "getIDGroups")
            self.response.write(error.print_general_error())

class getAllCars_groupID(webapp2.RequestHandler):
    def post(self):
        try:
            data = json.loads(self.request.body)
            code = int(data["Code"])
            try:
                result_check_code = User_tool.check_code(data["Email"], code)
                id_group = int(data["ID_group"])

                if id_group < 0 or id_group == "":
                    self.error(500)
                    error = StatusReturn(7, "getAllCars_groupID")
                    self.response.write(error.print_general_error())

                elif result_check_code == -2:
                    self.error(500)
                    error = StatusReturn(2, "getAllCars_groupID")
                    self.response.write(error.print_general_error())

                elif result_check_code == -1:
                    self.error(500)
                    error = StatusReturn(3, "getAllCars_groupID")
                    self.response.write(error.print_general_error())

                elif result_check_code >= 0:
                    try:
                        # id_groups = User_tool.return_groups(data["Email"])
                        result = []
                        #                    if(id_groups>0):
                        #                        for key in id_groups:
                        id_car_by_group = Car_group.getCarFromGroup(id_group)

                        for carTemp in id_car_by_group:
                            result.append((Car.getCarbyID(carTemp.id_car)).toString_JSON())

                        result_json = StatusReturn(2, "getAllCars_groupID", result)
                        self.response.write(result_json.print_result())
                    except:
                        self.error(500)
                        error = StatusReturn(6, "getAllCars_groupID")
                        self.response.write(error.print_general_error())
                else:
                    self.error(500)
                    error = StatusReturn(5, "getAllCars_groupID")
                    self.response.write(error.print_general_error())
            except:
                self.error(500)
                error = StatusReturn(4, "getAllCars_groupID")
                self.response.write(error.print_general_error())
        except:
            self.error(500)
            error = StatusReturn(1, "getAllCars_groupID")
            self.response.write(error.print_general_error())

class getAllCars(webapp2.RequestHandler):
    def post(self):
        try:
            data = json.loads(self.request.body)
            code = int(data["Code"])
            try:
                result_check_code = User_tool.check_code(data["Email"], code)

                if result_check_code == -2:
                    self.error(500)
                    error = StatusReturn(2, "getAllCars")
                    self.response.write(error.print_general_error())

                elif result_check_code == -1:
                    self.error(500)
                    error = StatusReturn(3, "getAllCars")
                    self.response.write(error.print_general_error())

                elif result_check_code >= 0:

                    try:
                        id_groups = User_tool.return_groups(data["Email"])
                        result = []
                        if (id_groups > 0):
                            for key in id_groups:
                                id_car_by_group = Car_group.getCarFromGroup(key.id_group)
                                for carTemp in id_car_by_group:
                                    result.append((Car.getCarbyID(carTemp.id_car)).toString_JSON())

                        result_json = StatusReturn(3, "getAllCars", result)
                        self.response.write(result_json.print_result())

                    except:
                        self.error(500)
                        error = StatusReturn(6, "getAllCars")
                        self.response.write(error.print_general_error())
                else:
                    self.error(500)
                    error = StatusReturn(5, "getAllCars")
                    self.response.write(error.print_general_error())
            except:
                self.error(500)
                error = StatusReturn(4, "getAllCars")
                self.response.write(error.print_general_error())
        except:
            self.error(500)
            error = StatusReturn(1, "getAllCars")
            self.response.write(error.print_general_error())

class getPositionCar(webapp2.RequestHandler):
    def post(self):
        result = {}
        logging.debug("Received from user: " + str(self.request.body))
        try:
            data = json.loads(self.request.body)
            key_car = data["ID"]
            try:
                car_select = Car.getCarbyID(key_car)
                logging.debug(car_select)
                try:
                    position = car_select.getPositionFromID
                    result["latitude"] = position.latitude
                    result["longitude"] = position.longitude
                    self.response.write(result)
                except:
                    logging.debug("Third try: " + str(sys.exc_info()))
            except:
                logging.debug("Second try: " + str(sys.exc_info()))

        except:
            logging.debug("Error first try: " + str(sys.exc_info()))

class createCar(webapp2.RequestHandler):
    def post(self):
        try:
            data = json.loads(self.request.body)
            code = int(data["Code"])
            try:
                result_check_code = User_tool.check_code(data["Email"], code)

                if result_check_code == -2:
                    self.error(500)
                    error = StatusReturn(2, "createCar")
                    self.response.write(error.print_general_error())

                elif result_check_code == -1:
                    self.error(500)
                    error = StatusReturn(3, "createCar")
                    self.response.write(error.print_general_error())

                elif result_check_code >= 0:
                    new_car = Car(model=data["Name"], latitude="0",
                                  longitude="0", timestamp=str(datetime.datetime.now()))
                    new_group = Group(name=data["Name"], timestamp=str(datetime.datetime.now()))
                    new_car.put()
                    new_group.put()
                    new_car_group = Car_group(id_car=int(new_car.key.id()), id_group=int(new_group.key.id()))
                    new_car_group.put()
                    new_user_group = User_group(id_user=int(User_tool.return_ID_from_email(str(data["Email"]))),
                                                    id_group=int(new_group.key.id()))
                    new_user_group.put()

                    right = StatusReturn(4, "createCar", new_car.key.id())
                    self.response.write(right.print_result())
                else:
                    self.error(500)
                    error = StatusReturn(5, "createCar")
                    self.response.write(error.print_general_error())
            except:
                self.error(500)
                error = StatusReturn(4, "createCar")
                self.response.write(error.print_general_error())
        except:
            self.error(500)
            error = StatusReturn(1, "createCar")
            self.response.write(error.print_general_error())

class createGroup(webapp2.RequestHandler):
    def post(self):
        try:
            data = json.loads(self.request.body)
            code = int(data["Code"])
            try:
                result_check_code = User_tool.check_code(data["Email"], code)

                if result_check_code == -2:
                    self.error(500)
                    error = StatusReturn(2, "createGroup")
                    self.response.write(error.print_general_error())

                elif result_check_code == -1:
                    self.error(500)
                    error = StatusReturn(3, "createGroup")
                    self.response.write(error.print_general_error())

                elif result_check_code >= 0:
                    new_group = Group(name=data["Name"], timestamp=str(datetime.datetime.now()))
                    new_group.put()
                    new_user_group = User_group(id_user=int(User_tool.return_ID_from_email(str(data["Email"]))),
                                                    id_group=int(new_group.key.id()))
                    new_user_group.put()

                    right = StatusReturn(6, "createGroup", new_group.key.id())
                    self.response.write(right.print_result())
                else:
                    self.error(500)
                    error = StatusReturn(5, "createGroup")
                    self.response.write(error.print_general_error())
            except:
                self.error(500)
                error = StatusReturn(4, "createGroup")
                self.response.write(error.print_general_error())
        except:
            self.error(500)
            error = StatusReturn(1, "createGroup")
            self.response.write(error.print_general_error())

class updatePosition(webapp2.RequestHandler):
    def post(self):
        try:
            data = json.loads(self.request.body)
            code = int(data["Code"])
            latitude = data["Latitude"]
            longitude = data["Longitude"]
            id_car = data["ID_car"]
            try:
                result_check_code = User_tool.check_code(data["Email"], code)

                if result_check_code == -2:
                    self.error(500)
                    error = StatusReturn(2, "updatePosition")
                    self.response.write(error.print_general_error())

                elif result_check_code == -1:
                    self.error(500)
                    error = StatusReturn(3, "updatePosition")
                    self.response.write(error.print_general_error())

                elif result_check_code >= 0:
                    try:

                        Car.update_position_ID(id_car,latitude,longitude)

                        right = StatusReturn(5, "updatePosition")
                        self.response.write(right.print_result())
                    except:
                        self.error(500)
                        error = StatusReturn(8, "updatePosition",str(sys.exc_info()))
                        self.response.write(error.print_general_error())
                else:
                    self.error(500)
                    error = StatusReturn(5, "updatePosition")
                    self.response.write(error.print_general_error())
            except:
                self.error(500)
                error = StatusReturn(4, "updatePosition")
                self.response.write(error.print_general_error())
        except:
            self.error(500)
            error = StatusReturn(1, "updatePosition")
            self.response.write(error.print_general_error())

class registrationForm(webapp2.RequestHandler):
    def post(self):
        try:
            data = json.loads(self.request.body)

            try:
                code = random.randint(100000, 999999)
                new_user = User(id_android=data["ID"], code=code, temp_code=code, email=data["Email"],
                                nickname=data["Nickname"])

                try:
                    contact_key = new_user.querySearch_email()
                    if contact_key.count() == 0:
                        new_user.put()
                    else:
                        temp_user = contact_key.get()
                        temp_user.update_contact(code)
                    try:
                        Send_email.send_code(code, data["Email"])

                        right = StatusReturn(0, 0)
                        self.response.write(right.error_registration())

                    except:
                        self.error(500)
                        error = StatusReturn(3, 0)
                        self.response.write(error.error_registration())
                except:
                    self.error(500)
                    error = StatusReturn(4, 0)
                    self.response.write(error.error_registration())
            except:
                self.error(500)
                error = StatusReturn(2, 0)
                self.response.write(error.error_registration())

        except:
            self.error(500)
            error = StatusReturn(1, 0)
            self.response.write(error.error_registration())


application = webapp2.WSGIApplication([
                                          ('/', MainPage),
                                          ('/sign', SendEmail),
                                          ('/requestPositionCar', getPositionCar),
                                          ('/registration', registrationForm),
                                          ('/getIDGroups', getIDGroups),
                                          ('/getAllCars', getAllCars),
                                          ('/createCar', createCar),
                                          ('/updatePosition', updatePosition),
                                      ], debug=True)