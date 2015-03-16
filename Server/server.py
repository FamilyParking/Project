import logging
import json
import sys
import datetime
import webapp2
import random
from Automatic.summary import Summary
from setting import static_variable

from Class.statusReturn import StatusReturn
from Cloud_Storage.history_park import History_park

from Cloud_Storage.user import User
from Cloud_Storage.car import Car
from Cloud_Storage.user_car import User_car

from Tool.push_notification import Push_notification
from Tool.send_email import Send_email
from Tool.user_tool import User_tool

class MainPage(webapp2.RequestHandler):
    def get(self):
        in_file = open("website/index.html", "r")
        MAIN_PAGE_HTML = in_file.read()

        # Google_api_request.request_place("41.9135029","12.5212144")

        self.response.write(MAIN_PAGE_HTML)
        in_file.close()


class HowToUsePage(webapp2.RequestHandler):
    def get(self):
        in_file = open("website/howtouse.html", "r")
        MAIN_PAGE_HTML = in_file.read()
        self.response.write(MAIN_PAGE_HTML)
        in_file.close()


class registrationForm(webapp2.RequestHandler):
    def post(self):
        try:
            data = json.loads(self.request.body)

            try:
                code = random.randint(100000, 999999)
                new_user = User(code=code, temp_code=code, email=data["Email"],
                                nickname=data["Name"], is_user=0)

                try:
                    contact_key = new_user.querySearch_email()
                    if contact_key.count() == 0:
                        new_user.put()
                    else:
                        temp_user = contact_key.get()
                        temp_user.update_contact(code, data["Name"])
                    try:
                        Send_email.send_code(code, data["Email"])

                        right = StatusReturn(0, 0)
                        self.response.write(right.error_registration())

                    except:
                        self.error(200)
                        error = StatusReturn(3, 0)
                        self.response.write(error.error_registration())
                except:
                    self.error(200)
                    error = StatusReturn(4, 0)
                    self.response.write(error.error_registration())
            except:
                self.error(200)
                error = StatusReturn(2, 0)
                self.response.write(error.error_registration())

        except:
            self.error(200)
            error = StatusReturn(1, 0)
            self.response.write(error.error_registration())


class confirmCode(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("confirmCode", self) >= 0:
            right = StatusReturn(11, "confirmCode")
            self.response.write(right.print_result())


class createCar(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("createCar", self) >= 0:
            dati = json.loads(self.request.body)
            car_data = dati["Car"]
            user_data = dati["User"]
            bluetooth_MAC = ""
            bluetooth_name = ""
            register = ""
            if "Bluetooth_MAC" in car_data:
                bluetooth_MAC = car_data["Bluetooth_MAC"]
            if "Bluetooth_Name" in car_data:
                bluetooth_name = car_data["Bluetooth_Name"]
            if "Register" in car_data:
                register = car_data["Register"]
            new_car = Car(name=car_data["Name"], latitude="0", longitude="0", timestamp=str(datetime.datetime.now()),
                          email=user_data["Email"], bluetooth_MAC=bluetooth_MAC, bluetooth_name=bluetooth_name,
                          brand=car_data["Brand"], register=register, isParked=False, lastdriver=user_data["Email"])
            new_car = new_car.put()
            list_user = car_data["Users"]
            searchuser = User.static_querySearch_email(user_data["Email"])
            for user in searchuser:

                if static_variable.DEBUG:
                    logging.debug(user.key.id())
                new_contact_car = User_car(id_user=user.key.id(), id_car=(new_car.id()))
                new_contact_car.put()
            for user in list_user:
                userEmail = user["Email"]
                user_key = User.is_user_check(userEmail)
                if user_key == 0:
                    new_user = User(id_android=None, code=0, temp_code=0, email=userEmail, nickname=None, is_user=0)
                    temp_user_key = new_user.put()
                    user_key = temp_user_key.id()
                if User_car.check_user_exist(user_key, new_car.id()) > 0:
                    new_contact_car = User_car(id_user=user_key, id_car=new_car.id())
                    new_contact_car.put()
            right = StatusReturn(4, "createCar", new_car.id())
            self.response.write(right.print_result())


class getCars(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("getCars", self) >= 0:

            dati = json.loads(self.request.body)

            if static_variable.DEBUG:
                logging.debug(dati)

            user_data = dati["User"]
            lower_email = user_data["Email"].lower()
            temp_user = User.static_querySearch_email(lower_email)

            if static_variable.DEBUG:
                logging.debug(temp_user)

            id_user = temp_user.get()

            if static_variable.DEBUG:
                logging.debug(id_user.key.id())

            cars = User_car.getCarFromUser(id_user.key.id())

            if (cars.count() == 0):
                allcars = []
                right = StatusReturn(3, "getAllCars_fromEmail", allcars)
                self.response.write(right.print_result())
            else:
                allcars = []
                for id_carTemp in cars:
                    if static_variable.DEBUG:
                        logging.debug(id_carTemp)
                    allcars.append(Car.get_json(long(id_carTemp.id_car)))

                right = StatusReturn(3, "getCars", allcars)
                self.response.write(right.print_result())


class deleteCar(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("deleteCar", self) >= 0:

            dati = json.loads(self.request.body)
            user_data = dati["User"]
            car_data = dati["Car"]

            temp_user = User.static_querySearch_email(user_data["Email"])
            user = temp_user.get()
            id_user = user.key.id()

            User_car.deleteCarUser(id_user, car_data["ID_car"])
            user_car = User_car.getUserFromCar(car_data["ID_car"])

            if static_variable.DEBUG:
                logging.debug("Number of car with this user: "+str(user_car.count()))

            if user_car.count() == 0:
                Car.delete_car_ID(car_data["ID_car"])

            right = StatusReturn(7, "deleteCar")
            self.response.write(right.print_result())


class updatePosition(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("updatePosition", self) >= 0:
            dati = json.loads(self.request.body)
            user_data = dati["User"]
            temp_user = User.static_querySearch_email(user_data["Email"])

            car_data = dati["Car"]
            Car.update_position_ID(car_data["ID_car"], car_data["Latitude"], car_data["Longitude"], user_data["Email"])
            list_user = User_car.getUserFromCar(car_data["ID_car"])

            # Send notification to all user register with this car
            for user in list_user:

                # If the user is not registered inside the application send him an email
                if User.is_registered_check(user.id_user) == 0:
                    if User.get_email_user(user.id_user) != user_data["Email"]:
                        Send_email.send_position(User.get_email_user(user.id_user), car_data["Latitude"],
                                             car_data["Longitude"], user_data["Name"],car_data["Name"])
                else:
                    if User.get_email_user(user.id_user) != user_data["Email"]:
                        Push_notification.send_push_park(User.get_id_android(user.id_user), car_data["Name"], user_data["Name"])

            if static_variable.DEBUG:
                logging.debug("Date if car: "+car_data["Name"])

            id_user = temp_user.get().key.id()

            timestamp = str(datetime.datetime.utcnow() + datetime.timedelta(hours=1))
            if "Timestamp" in car_data:
                timestamp = car_data["Timestamp"]

            if static_variable.DEBUG:
                logging.debug(timestamp)

            History_park.update_history(id_user,car_data["Latitude"],car_data["Longitude"], timestamp)

            right = StatusReturn(5, "updatePosition")
            self.response.write(right.print_result())


class insertContactCar(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("insertContactCar", self) >= 0:
            dati = json.loads(self.request.body)
            user_data = dati["User"]
            car_data = dati["Car"]
            for userClass in car_data["Users"]:
                user = userClass["Email"]
                user_key = User.is_user_check(user)
                if user_key == 0:
                    new_user = User(id_android=None, code=0, temp_code=0, email=user, nickname=user, is_user=0)
                    temp_user_key = new_user.put()
                    user_key = temp_user_key.id()

                if User_car.check_user_exist(user_key, int(car_data["ID_car"])) > 0:
                    new_contact_car = User_car(id_user=user_key, id_car=int(car_data["ID_car"]))
                    new_contact_car.put()

                if User.is_registered_check(user_key) == 0:
                    Send_email.send_adding_group(user, user_data["Name"], car_data["Name"])
                else:
                    Push_notification.send_push_add_group(User.get_id_android(user_key), user_data["Name"],car_data["Name"])

            right = StatusReturn(10, "insertContactCar")
            self.response.write(right.print_result())


class editCar(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("editCar", self) >= 0:

            dati = json.loads(self.request.body)

            car_data = dati["Car"]
            bluetooth_MAC = ""
            bluetooth_name = ""
            register = ""

            if "Bluetooth_MAC" in car_data:
                bluetooth_MAC = car_data["Bluetooth_MAC"]
            if "Bluetooth_Name" in car_data:
                bluetooth_name = car_data["Bluetooth_Name"]
            if "Register" in car_data:
                register = car_data["Register"]

            Car.update_car(car_data["ID_car"], bluetooth_MAC, bluetooth_name, car_data["Brand"], car_data["Name"],
                           register)

            right = StatusReturn(19, "editCar")
            self.response.write(right.print_result())


class updateGCM(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("updateGCM", self) >= 0:

            dati = json.loads(self.request.body)
            user_data = dati["User"]

            if User.update_google_code(user_data["Email"], user_data["ID_gcm"]) == 0:
                right = StatusReturn(15, "updateGCM")
                self.response.write(right.print_result())


class removeContactCar(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("removeContactCar", self) >= 0:
            dati = json.loads(self.request.body)
            user_data = dati["User"]
            car_data = dati["Car"]
            for userClass in car_data["Users"]:
                user = userClass["Email"]
                tempUser = User.static_querySearch_email(user)
                for id_user in tempUser:
                    user_key = id_user.key.id()

                User_car.deleteCarUser(user_key, car_data["ID_car"])
            # user_car = User_car.getUserFromCar(car_data["ID_car"])
            # if (user_car.count() == 0):
            # Car.delete_car_ID(car_data["ID_car"])
            right = StatusReturn(18, "removeContactCar")
            self.response.write(right.print_result())


class getNotification(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("removeContactCar", self) >= 0:

            dati = json.loads(self.request.body)

            user_data = dati["User"]

            latitude = dati["Latitude"]
            longitude = dati["Longitude"]
            temp_user = User.static_querySearch_email(user_data["Email"])
            id_user = temp_user.get().key.id()
            timestamp = dati["Timestamp"]

            if History_park.parky(id_user, latitude, longitude, timestamp) == 1:
                if static_variable.DEBUG:
                    logging.debug("The application has to send notification? --> "+str(True))

                right = StatusReturn(20, "getNotification", True)
                self.response.write(right.print_result())

            else:
                if static_variable.DEBUG:
                    logging.debug("The application has to send notification? --> "+str(False))

                right = StatusReturn(21, "getNotification", False)
                self.response.write(right.print_result())


class automatic_summary(webapp2.RequestHandler):
    def get(self):
        Summary.create_statistic()

class pickCar(webapp2.RequestHandler):
    def post(self):
        if User_tool.check_before_start("updatePosition", self) >= 0:
            dati = json.loads(self.request.body)

            car_data = dati["Car"]
            Car.pick_car(car_data["ID_car"])

            right = StatusReturn(21, "getNotification", False)
            self.response.write(right.print_result())


application = webapp2.WSGIApplication([
    ('/', MainPage),
    ('/howtouse', HowToUsePage),
    ('/registration', registrationForm),
    ('/createCar', createCar),
    ('/confirmCode', confirmCode),
    ('/editCar', editCar),
    ('/updateGoogleCode', updateGCM),
    ('/getAllCars', getCars),
    ('/deleteCar', deleteCar),
    ('/pickCar', pickCar),
    ('/updatePosition', updatePosition),
    ('/insertContactCar', insertContactCar),
    ('/getNotification', getNotification),
    ('/removeContactCar', removeContactCar),
    ('/Automatic/summary', automatic_summary)

], debug=True)