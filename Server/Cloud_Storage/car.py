import sys
from setting import static_variable

__author__ = 'Nazzareno'

from Class.position import Position

import logging
import time
import datetime

from Cloud_Storage.user_car import User_car
from Cloud_Storage.user import User

from google.appengine.ext import ndb


class Car(ndb.Model):
    brand = ndb.StringProperty()
    latitude = ndb.StringProperty()
    longitude = ndb.StringProperty()
    timestamp = ndb.StringProperty()
    bluetooth_MAC = ndb.StringProperty()
    bluetooth_name = ndb.StringProperty()
    name = ndb.StringProperty()
    email = ndb.StringProperty()
    register = ndb.StringProperty()
    lastdriver = ndb.StringProperty()
    isParked = ndb.BooleanProperty()
    uuid = ndb.StringProperty()
    bmaj = ndb.StringProperty()
    bmin = ndb.StringProperty()
    marker_color = ndb.FloatProperty()

    def getPositionFromID(self):
        result = Position(self.latitude, self.longitude)
        return result

    def updatePosition(self, latitude, longitude, lastdriver):
        self.latitude = latitude
        self.longitude = longitude
        self.lastdriver = lastdriver

        st = str(datetime.datetime.utcnow() + datetime.timedelta(hours=1))

        self.timestamp = st
        self.isParked = True
        self.put()
        return 0

    def updateParked(self):
        self.isParked = False
        self.put()
        return 0

    def to_string_json_car(self):
        car_users = []
        id_users = User_car.getUserFromCar(self.key.id())
        for id_user in id_users:
            user = User.get_user_by_id(id_user.id_user)
            car_users.append(user.toString_JSON())
        return {
                "ID_car": str(self.key.id()), "Brand": str(self.brand), "Name": str(self.name),
                "Latitude": str(self.latitude),
                "Longitude": str(self.longitude), "Users": car_users, "Timestamp": str(self.timestamp),
                "Register": str(self.register), "Last_driver": str(self.lastdriver), "isParked": self.isParked,
                "Bluetooth_MAC": str(self.bluetooth_MAC), "Bluetooth_Name": str(self.bluetooth_name),
                "UUID": str(self.uuid), "Bmin": str(self.bmin), "Bmaj": str(self.bmaj),
                "Marker_Color": self.marker_color
                }

    @staticmethod
    def updateUUID(id,uuid, bmaj, bmin):
        temp_car = Car.getCarbyID(id)
        temp_car.bmaj = bmaj
        temp_car.bmin = bmin
        temp_car.uuid = uuid
        temp_car.put()
        return 0


    def update(self, bluetooth_MAC, bluetooth_name, brand, email, latitude, longitude, name, maker_color):
        self.latitude = latitude
        self.longitude = longitude
        self.bluetooth_MAC = bluetooth_MAC
        self.bluetooth_name = bluetooth_name
        self.brand = brand
        self.email = email
        self.name = name
        self.marker_color = maker_color
        self.put()
        return 0

    @staticmethod
    def getCarbyID(id):

        if static_variable.DEBUG and static_variable.DEBUG_ALL_CARS:
            logging.debug("Value ID: "+str(id)+" Cast a float: "+str("%.0f" % float(id)))

        app_key = Car.get_by_id(long("%.0f" % float(id)))     # This why sometime arrive id like '4.93745548034048E15'
        return app_key

    @staticmethod
    def get_json(id):
        app_key = Car.getCarbyID(id)
        return app_key.to_string_json_car()

    @staticmethod
    def update_position_ID(id, latitude, longitude, lastdriver):
        if static_variable.DEBUG and static_variable.DEBUG_UPDATE_POSITION:
            logging.debug("latitude: "+str(latitude)+" longitude: "+str(longitude))
            logging.debug("%.7f" % float(latitude))
        conv_latitude = "%.7f" % float(latitude) # This why sometime arrive id like '4.93745548034048E15'
        conv_longitude = "%.7f" % float(longitude) # This why sometime arrive id like '4.93745548034048E15'
        temp_car = Car.getCarbyID(id)
        return temp_car.updatePosition(conv_latitude, conv_longitude, lastdriver)

    @staticmethod
    def pick_car(id):
        temp_car = Car.getCarbyID(id)
        return temp_car.updateParked()

    @staticmethod
    def update_car(id, bluetooth_MAC, bluetooth_name, brand, name, register, marker_color):
        temp_car = Car.getCarbyID(id)
        temp_car.bluetooth_MAC = bluetooth_MAC
        temp_car.bluetooth_name = bluetooth_name
        temp_car.brand = brand
        temp_car.name = name
        temp_car.register = register
        temp_car.marker_color = marker_color
        temp_car.put()

    @staticmethod
    def delete_car_ID(id):
        app_key = Car.getCarbyID(id)
        app_key.key.delete()

    @staticmethod
    def get_name_id(ID):
        app_key = Car.getCarbyID(ID)
        return app_key.name

    @staticmethod
    def get_position_id(ID):
        app_key = Car.getCarbyID(ID)
        return app_key.getPositionFromID()

    @staticmethod
    def get_all_cars(email_user):
        try:
            cars = Car.query(Car.email == email_user)
        except:
            logging.debug(sys.exc_info())
        return cars

