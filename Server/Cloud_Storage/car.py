import json
import sys

__author__ = 'Nazzareno'

from Class.position import Position

import logging

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

    def getPositionFromID(self):
        result = Position(self.latitude, self.longitude)
        return result

    def updatePosition(self, latitude, longitude):
        self.latitude = latitude
        self.longitude = longitude
        self.put()
        return 0

    def toString_JSON(self):
        idString = "'ID':'" + str(self.key.id()) + "'"
        modelString = "'brand':'" + str(self.brand) + "'"
        nameString = "'name':'" + str(self.name) + "'"
        latitudeString = "'latitude':'" + self.latitude + "'"
        longitudeString = "'longitude':'" + self.longitude + "'"
        # return '{' + idString + "," + modelString + "," + nameString + "," + latitudeString + "," + longitudeString + '}'
        return {"ID_car": str(self.key.id()), "Brand": str(self.brand), "Name": str(self.name), "Latitude": str(self.latitude),
                "Longitude":str(self.longitude)}

    def update(self, bluetooth_MAC, bluetooth_name, brand, email, latitude, longitude, name):
        self.latitude = latitude
        self.longitude = longitude
        self.bluetooth_MAC = bluetooth_MAC
        self.bluetooth_name = bluetooth_name
        self.brand = brand
        self.email = email
        self.name = name
        self.put()
        return 0

    @staticmethod
    def getCarbyID(id):
        app_key = Car.get_by_id(long(id))
        return app_key

    @staticmethod
    def update_position_ID(id, latitude, longitude):
        temp_car = Car.getCarbyID(id)
        return temp_car.updatePosition(latitude, longitude)

    @staticmethod
    def update_car(id, bluetooth_MAC, bluetooth_name, brand, email, name):
        temp_car = Car.getCarbyID(id)
        temp_car.bluetooth_MAC = bluetooth_MAC
        temp_car.bluetooth_name = bluetooth_name
        temp_car.brand = brand
        temp_car.email = email
        temp_car.name = name
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

