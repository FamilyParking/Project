import logging
import json
import sys
import datetime
import webapp2
import random

from Class.statusReturn import StatusReturn

from Cloud_Storage.car_group import Car_group
from Cloud_Storage.user import User
from Cloud_Storage.car import Car
from Cloud_Storage.group import Group
from Cloud_Storage.user_copy import User_copy
from Cloud_Storage.user_group import User_group

from google.appengine.api import mail
from Tool.push_notification import push_class

from Tool.send_email import Send_email
from Tool.user_tool import User_tool


class MainPage(webapp2.RequestHandler):
    def get(self):
        in_file = open("website/index.html", "r")
        MAIN_PAGE_HTML = in_file.read()
        self.response.write(MAIN_PAGE_HTML)
        in_file.close()


class HowToUsePage(webapp2.RequestHandler):
    def get(self):
        in_file = open("website/howtouse.html", "r")
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
            new_car = Car(brand="testBrand", latitude=data["latitude"],
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
            except:
                logging.debug(sys.exc_info())

        except:
            self.error(500)
            error = StatusReturn(1, 0)
            self.response.write(error.toJSON())


class confirmCode(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("confirmCode", self) >= 0:
            right = StatusReturn(11, "confirmCode")
            self.response.write(right.print_result())


class getIDGroups(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("getIDGroups", self) >= 0:
            data = json.loads(self.request.body)
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


class getAllCars_groupID(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("getAllCars_groupID", self) >= 0:
            data = json.loads(self.request.body)
            try:
                # id_groups = User_tool.return_groups(data["Email"])
                result = []
                # if(id_groups>0):
                # for key in id_groups:
                id_car_by_group = Car_group.getCarFromGroup(data["ID_group"])

                for carTemp in id_car_by_group:
                    result.append((Car.getCarbyID(carTemp.id_car)).toString_JSON())

                result_json = StatusReturn(2, "getAllCars_groupID", result)
                self.response.write(result_json.print_result())
            except:
                self.error(500)
                error = StatusReturn(6, "getAllCars_groupID")
                self.response.write(error.print_general_error())


class getAllCars(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("getAllCars", self) >= 0:
            data = json.loads(self.request.body)
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


class getAllCars_fromEmail(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("getAllCars_fromEmail", self) >= 0:
            data = json.loads(self.request.body)
            try:
                # id_groups = User_tool.return_groups(data["Email"])
                result = []
                # if(id_groups>0):
                # for key in id_groups:
                id_car_by_email = Car.get_all_cars(data["Email"])

                for carTemp in id_car_by_email:
                    result.append(carTemp.toString_JSON())

                result_json = StatusReturn(12, "getAllCars_fromEmail", result)
                self.response.write(result_json.print_result())
            except:
                self.error(500)
                error = StatusReturn(6, "getAllCars_fromEmail")
                self.response.write(error.print_general_error())


class createCar(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("createCar", self) >= 0:
            data = json.loads(self.request.body)
            new_car = Car(name=data["Name"], latitude="0",
                          longitude="0", timestamp=str(datetime.datetime.now()), email=data["Email"], bluetooth_MAC=data["Bluetooth_MAC"], bluetooth_name=data["Bluetooth_name"])
            new_car.put()

            right = StatusReturn(4, "createCar", new_car.key.id())
            self.response.write(right.print_result())


class deleteCar(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("deleteCar", self) >= 0:
            data = json.loads(self.request.body)
            Car.delete_car_ID(data["ID"])
            right = StatusReturn(7, "deleteCar", "Delete " + str(data["ID"]))
            self.response.write(right.print_result())


class getPositionCar(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("getPositionCar", self) >= 0:
            data = json.loads(self.request.body)

            result = Car.get_position_id(data["ID"])
            result_JSON = {}
            result_JSON["latitude"] = result.latitude
            result_JSON["longitude"] = result.longitude

            right = StatusReturn(9, "getPositionCar", result_JSON)
            self.response.write(right.print_result())


class createGroup(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("createGroup", self) >= 0:
            data = json.loads(self.request.body)
            new_group = Group(name=data["Name"], timestamp=str(datetime.datetime.now()))
            new_group.put()
            new_user_group = User_group(id_user=int(User_tool.return_ID_from_email(str(data["Email"]))),
                                        id_group=int(new_group.key.id()))
            new_user_group.put()

            list_user = data["List_email"]
            logging.debug(list_user)
            for user in list_user:

                user_key = User.is_user_check(user)
                if user_key == 0:
                    new_user = User(id_android=None, code=0, temp_code=0, email=user, nickname=None, is_user=0)
                    temp_user_key = new_user.put()
                    # logging.debug(temp_user_key)
                    user_key = temp_user_key.id()

                if User_group.check_user_exist(user_key) > 0:
                    new_contact_group = User_group(id_user=user_key, id_group=int(new_group.key.id()))
                    new_contact_group.put()

            right = StatusReturn(6, "createGroup", new_group.key.id())
            self.response.write(right.print_result())


class deleteGroup(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("deleteGroup", self) >= 0:
            data = json.loads(self.request.body)
            Group.delete_group_ID(data["ID"])
            right = StatusReturn(8, "deleteGroup", "Delete " + str(data["ID"]))
            self.response.write(right.print_result())


class insertContactGroup(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("insertContactGroup", self) >= 0:
            data = json.loads(self.request.body)
            list_user = data["List_email"]
            logging.debug(list_user)
            for user in list_user:

                user_key = User.is_user_check(user)
                if user_key == 0:
                    new_user = User(id_android=None, code=0, temp_code=0, email=user, nickname=None, is_user=0)
                    temp_user_key = new_user.put()
                    # logging.debug(temp_user_key)
                    user_key = temp_user_key.id()

                if User_group.check_user_exist(user_key) > 0:
                    new_contact_group = User_group(id_user=user_key, id_group=int(data["ID_group"]))
                    new_contact_group.put()

            right = StatusReturn(10, "insertContactGroup")
            self.response.write(right.print_result())


class removeContactGroup(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("removeContactGroup", self) >= 0:
            data = json.loads(self.request.body)
            list_user = data["List_email"]
            logging.debug(list_user)
            for user in list_user:

                user_key = User.is_user_check(user)

                if user_key != 0 and User_group.check_user_exist(user_key) < 0:
                    User_group.delete_contact_group(user_key)

            right = StatusReturn(10, "removeContactGroup")
            self.response.write(right.print_result())

class removeCarGroup(webapp2.RequestHandler):
	def post(self):
		if User_tool.check_before_start("removeCarGroup", self) >= 0:
			data = json.loads(self.request.body)
			Car_group.delete_car_ID(data["ID_car"], data["ID_group"])
			right = StatusReturn(14, "removeCarGroup", "Delete " + str(data["ID"]))
			self.response.write(right.print_result())
			

			
class insertCarGroup(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("insertCarGroup", self) >= 0:
            data = json.loads(self.request.body)
            id_car = data["ID_car"]
            id_group = data["ID_group"]

            temp_car_group = Car_group(id_car= id_car,id_group= id_group)
            temp_car_group.put()

            right = StatusReturn(13, "insertCarGroup")
            self.response.write(right.print_result())

class deleteCarGroup(webapp2.RequestHandler):
	def post(self):
		
class updatePosition(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("updatePosition", self) >= 0:
            data = json.loads(self.request.body)
            latitude = data["Latitude"]
            longitude = data["Longitude"]
            id_car = data["ID_car"]
            try:
                Car.update_position_ID(id_car, latitude, longitude)

                push_class.send_post_request("APA91bHEPz23R7Esaq70tw7iY4Zw_e9UwC7RWDe8hOJz2jzttLJr5b969vqAh3zSTvGhCWOfLhuVyEAP0Sm9CTeGJI4SPNnqdDD0ygKHFMQbodBcwZO4-xo-J8nQgqxCAOSUasPEoFBN1rsLdA07CxEKFwUhRe71dWVScm7bfEYzlhDEujIovvSNGVM62XjFKVva4evDGJSl")

                right = StatusReturn(5, "updatePosition")
                self.response.write(right.print_result())
            except:
                self.error(500)
                error = StatusReturn(8, "updatePosition", str(sys.exc_info()))
                self.response.write(error.print_general_error())





class registrationForm(webapp2.RequestHandler):
    def post(self):
        try:
            data = json.loads(self.request.body)

            try:
                code = random.randint(100000, 999999)
                new_user = User(id_android=data["ID"], code=code, temp_code=code, email=data["Email"],
                                nickname=data["Nickname"], is_user=1)

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
                                          ('/howtouse', HowToUsePage),
                                          ('/sign', SendEmail),
                                          ('/requestPositionCar', getPositionCar),
                                          ('/registration', registrationForm),
                                          ('/getIDGroups', getIDGroups),
                                          ('/getAllCars', getAllCars),
                                          ('/getAllCars_groupID', getAllCars_groupID),
                                          ('/getAllCars_fromEmail', getAllCars_fromEmail),
                                          ('/createCar', createCar),
                                          ('/deleteCar', deleteCar),
                                          ('/createGroup', createGroup),
                                          ('/deleteGroup', deleteGroup),
                                          ('/insertCarGroup', insertCarGroup),
                                          ('/updatePosition', updatePosition),
                                          ('/getPositionCar', getPositionCar),
                                          ('/confirmCode', confirmCode),
                                          ('/insertContactGroup', insertContactGroup),
                                          ('/removeContactGroup', removeContactGroup),
                                      ], debug=True)