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


class updateGoogleCode(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("updateGoogleCode", self) >= 0:
            data = json.loads(self.request.body)
            user_data = data["User"]
            User.update_google_code(user_data["Email"], data["ID"])
            right = StatusReturn(14, "updateGoogleCode")
            self.response.write(right.print_result())


class confirmCode(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("confirmCode", self) >= 0:
            right = StatusReturn(11, "confirmCode")
            self.response.write(right.print_result())


class getIDGroups(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("getIDGroups", self) >= 0:
            data = json.loads(self.request.body)
            user_class = data["User"]
            try:
                id_groups = User_tool.return_groups(user_class["Email"])
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
            user_class = data["User"]

            try:
                id_groups = User_tool.return_groups(user_class["Email"])
                result = []
                if (id_groups > 0):
                    for key in id_groups:
                        id_car_by_group = Car_group.getCarFromGroup(key.id_group)
                        for carTemp in id_car_by_group:
                            return_json_value = Car.getCarbyID(carTemp.id_car).toString_JSON()
                            result.append(return_json_value)

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
            user_class = data["User"]
            try:
                # id_groups = User_tool.return_groups(data["Email"])
                result = []
                # if(id_groups>0):
                # for key in id_groups:
                id_car_by_email = Car.get_all_cars(user_class["Email"])

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
            user_data = data["User"]
            car_json = data["Car"]

            #Check if the user insert the Bluetooth_MAC
            if "Bluetooth_MAC" in data:
                new_car = Car(name=car_json["Name"], latitude="0", longitude="0", timestamp=str(datetime.datetime.now()),
                              email=user_data["Email"], bluetooth_MAC=car_json["Bluetooth_MAC"],
                              bluetooth_name=car_json["Bluetooth_name"], brand=car_json["Brand"])
            else:
                new_car = Car(name=car_json["Name"], latitude="0", longitude="0", timestamp=str(datetime.datetime.now()),
                              email=user_data["Email"], brand=car_json["Brand"])

            #Create the new car
            new_car.put()

            #Create the new group with the new car
            new_group = Group(name=car_json["Name"], timestamp=str(datetime.datetime.now()))
            new_group.put()

            #Add the user that create the car inside the group
            new_user_group = User_group(id_user=long(User_tool.return_ID_from_email(str(user_data["Email"]))),
                                        id_group=long(new_group.key.id()))
            new_user_group.put()

            #Link the car with the group
            temp_car_group = Car_group(id_car=long(new_car.key.id()), id_group=long(new_group.key.id()))
            temp_car_group.put()

            #Return the new ID of the car
            right = StatusReturn(4, "createCar", new_car.key.id())
            self.response.write(right.print_result())


class deleteCar(webapp2.RequestHandler):
    def post(self):
        logging.debug("TEST")

        if User_tool.check_before_start("deleteCar", self) >= 0:
            data = json.loads(self.request.body)
            try:
                data_car = data["Car"]
                data_user = data["User"]

                #Return group from car
                temp_group = Car_group.getGroupFromCar(data_car["ID_car"])

                #Delete the contact from the group
                temp_user = User.static_querySearch_email(data_user["Email"])

                User_group.delete_contact_group(temp_user.get().key.id(), temp_group.get().id_group)

                logging.debug("ID_user da eliminare: "+str(temp_user.get().key.id()) +" ID gruppo da eliminare: "+str(temp_group.get().id_group))

                count_user = User_group.getUserFromGroup(temp_group.get().id_group)

                #If the group is empty delete the car and the group
                if list.count(count_user) == 0:
                    Group.delete_group_ID(temp_group.get().id_group)
                    Car.delete_car_ID(data_car["ID_car"])

                right = StatusReturn(7, "deleteCar", "Delete " + str(data_car["ID_car"]))
                self.response.write(right.print_result())
            except:
                self.error(500)
                self.response.write("Errore")



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
            # logging.debug(list_user)
            for user in list_user:

                user_key = User.is_user_check(user)
                if user_key == 0:
                    new_user = User(id_android=None, code=0, temp_code=0, email=user, nickname=None, is_user=0)
                    temp_user_key = new_user.put()
                    # logging.debug(temp_user_key)
                    user_key = temp_user_key.id()

                if User_group.check_user_exist(user_key, int(new_group.key.id())) > 0:
                    new_contact_group = User_group(id_user=user_key, id_group=int(new_group.key.id()))
                    new_contact_group.put()

            right = StatusReturn(6, "createGroup", long(new_group.key.id()))
            self.response.write(right.print_result())


class deleteGroup(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("deleteGroup", self) >= 0:
            data = json.loads(self.request.body)
            id_group = long(data["ID_group"])
            temp_user = User.static_querySearch_email(data["Email"])
            User_group.delete_contact_group(temp_user.get().key.id(), id_group)

            logging.debug(User_group.getUserFromGroup(id_group).count())

            if User_group.getUserFromGroup(id_group).count() == 0:
                Group.delete_group_ID(id_group)
                list_car = Car_group.getCarFromGroup(id_group)
                for cars in list_car:
                    Car_group.delete_car_ID(cars.id_car, id_group)

            right = StatusReturn(8, "deleteGroup", "Removed From: " + str(id_group))
            self.response.write(right.print_result())


class insertContactCar(webapp2.RequestHandler):
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

                if User_group.check_user_exist(user_key, long(data["ID_group"])) > 0:
                    new_contact_group = User_group(id_user=user_key, id_group=long(data["ID_group"]))
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

                if User_group.check_user_exist(user_key, data["ID_group"]) > 0:
                    User_group.delete_contact_group(user_key, data["ID_group"])

            right = StatusReturn(18, "removeContactGroup")
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

            temp_car_group = Car_group(id_car=long(id_car), id_group=long(id_group))
            temp_car_group.put()

            right = StatusReturn(13, "insertCarGroup")
            self.response.write(right.print_result())


class editCar(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("editCar", self) >= 0:
            data = json.loads(self.request.body)
            id = data["ID_car"]
            email = data["Email"]
            bluetooth_MAC = data["Bluetooth_MAC"]
            bluetooth_name = data["Bluetooth_name"]
            brand = data["Brand"]
            name = data["Name"]
            try:
                Car.update_car(id, bluetooth_MAC, bluetooth_name, brand, email, name)
                right = StatusReturn(16, "editCar")
                self.response.write(right.print_result())
            except:
                self.error(500)
                error = StatusReturn(8, "editCar", str(sys.exc_info()))
                self.response.write(error.print_general_error())


class editGroup(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("editGroup", self) >= 0:
            data = json.loads(self.request.body)
            name = data["Name"]
            id = data["ID_group"]
            try:
                Group.update_group(id, name)
                right = StatusReturn(17, "editGroup")
                self.response.write(right.print_result())
            except:
                self.error(500)
                error = StatusReturn(8, "editGroup", str(sys.exc_info()))
                self.response.write(error.print_general_error())


class updatePosition(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("updatePosition", self) >= 0:
            data = json.loads(self.request.body)
            latitude = data["Latitude"]
            longitude = data["Longitude"]
            id_car = data["ID_car"]
            try:
                Car.update_position_ID(id_car, latitude, longitude)

                # push_class.send_post_request("APA91bFKpc1XNokg3Gv9GTWI49oE-UXe-ED6JMam2YPdAYG23yJf_P3c7Tl_55f9iECuhSNVa86PfZfcZ4knQ2VzFuBy_lNrq5_DLRHcghMkTQtRl9jyCbL6tV5TquDrse-dMQlGx9HKDLbtCNwEhGEFVeWXQH9EBjCt-VewSitHtgk2BxIB-w20ZLZtz2MCGAqRnTKD8B5n")

                temp_car_group = Car_group.getGroupFromCar(long(id_car))
                for group_result in temp_car_group:
                    list_user = User_group.getUserFromGroup(group_result.id_group)
                    result_id_android = []
                    for user_result in list_user:
                        temp_user = User.get_user_by_id(user_result.id_user)
                        if temp_user.email != data["Email"]:
                            if temp_user.is_user == 1:
                                logging.debug("Indirizzo email utente del push: " + str(temp_user.email))
                                result_id_android.append(temp_user.id_android)
                                # push_class.send_push_park(temp_user.id_android)
                            else:
                                Send_email.send_position(temp_user.email, latitude, longitude)
                    if len(result_id_android) > 0:
                        push_class.send_push_park(result_id_android , Car.get_name_id(long(id_car)))

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
                                          ('/updateGoogleCode', updateGoogleCode),
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
                                          ('/insertContactGroup', insertContactCar),
                                          ('/removeContactGroup', removeContactGroup),
                                          ('/editCar', editCar),
                                          ('/editGroup', editGroup)
                                      ], debug=True)