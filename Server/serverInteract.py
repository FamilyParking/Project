import logging
import json
import sys
import datetime
import webapp2
import random


from decimal import Decimal


from Class.statusReturn import StatusReturn

from Cloud_Storage.user import User
from Cloud_Storage.car import Car
from Cloud_Storage.user_copy import User_copy

from google.appengine.api import mail


from Tool.send_email import Send_email


class MainPage(webapp2.RequestHandler):
    def get(self):
        in_file = open("website/index.html", "r")
        MAIN_PAGE_HTML = in_file.read()
        self.response.write(MAIN_PAGE_HTML)
        in_file.close()


class SendEmail(webapp2.RequestHandler):
    def post(self):
        logging.debug("Received from user: "+str(self.request.body))
        try:
            data = json.loads(self.request.body)
            new_contact = User_copy(id_android=data["ID"], counter=1, latitude=data["latitude"],
                                              longitude=data["longitude"], last_time=str(datetime.datetime.now()))
            new_car = Car(model="testModel",latitude=data["latitude"],
                                              longitude=data["longitude"], timestamp=str(datetime.datetime.now()))
#			logging.debug(new_car)
            try:
                contact_key = new_contact.querySearch()
#			 	logging.debug(contact_key.get())
            except:
                logging.debug(sys.exc_info())

            try:
                if contact_key.count() == 0:
                    add_db_contact = new_contact.put()
                    add_db_car = new_car.put()

                else:
                    temp_contact = contact_key.get()
#					logging.debug(new_car.getPositionFromID())

                    if bool(temp_contact.update_contact(data["latitude"], data["longitude"])):
                        try:
                            message = mail.EmailMessage(sender="Family Parking <familyparkingapp@gmail.com>",
                                                        subject="Position of car")
                            message.body = "Your car is parked here: http://www.google.com/maps/place/" + data["latitude"] + "," + \
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
            except:
                logging.debug(sys.exc_info())

        except:
            self.error(500)
            error = StatusReturn(1, 0)
            self.response.write(error.toJSON())

class getIDGroup(webapp2.RequestHandler):
    def post(self):
        try:
            data = json.loads(self.request.body)
        except:
            logging.debug(str(sys.exc_info()))


class getPositionCar(webapp2.RequestHandler):
    def post(self):
        result = {}
        logging.debug("Received from user: "+str(self.request.body))
        try:
            data = json.loads(self.request.body)
            key_car = data["ID"]
            try:
                car_select = Car.getCarbyID(key_car)
                logging.debug(car_select)
                try:
                    position = car_select.getPositionFromID()
                    result["latitude"] = position.latitude
                    result["longitude"] = position.longitude
                    self.response.write(result)
                except:
                    logging.debug("Third try: " + str(sys.exc_info()))
            except:
                logging.debug("Second try: " + str(sys.exc_info()))

        except:
            logging.debug("Error first try: "+ str(sys.exc_info()))


class registrationForm(webapp2.RequestHandler):
    def post(self):
        try:
            data = json.loads(self.request.body)

            try:
                code = random.randint(100000,999999)
                new_user = User(id_android=data["ID"], code=code, temp_code=code, email=data["Email"], nickname=data["Nickname"])

                try:
                    contact_key = new_user.querySearch_email()
                    if contact_key.count() == 0:
                        new_user.put()
                    else:
                        temp_user = contact_key.get()
                        temp_user.update_contact(code)
                    try:
                        Send_email.send_code(code,data["Email"])

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
                                          ('/requestPositionCar',getPositionCar),
                                          ('/registration',registrationForm),
                                      ], debug=True)