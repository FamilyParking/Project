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
        result = Position(self.latitude,self.longitude)
        return result

    def updatePosition(self,latitude,longitude):
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
        return "{" + idString + "," + modelString + "," + nameString + "," + latitudeString + "," + longitudeString + "}"

    @staticmethod
    def getCarbyID(id):
        app_key = Car.get_by_id(long(id))
        return app_key

    @staticmethod
    def update_position_ID(id,latitude,longitude):
        temp_car = Car.getCarbyID(id)
        return temp_car.updatePosition(latitude,longitude)

    @staticmethod
    def delete_car_ID(id):
        app_key = Car.getCarbyID(id)
        app_key.key.delete()

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

