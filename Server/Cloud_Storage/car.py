
__author__ = 'Nazzareno'

from Class.position import Position

import logging

from google.appengine.ext import ndb

class Car(ndb.Model):
    model = ndb.StringProperty()
    latitude = ndb.StringProperty()
    longitude = ndb.StringProperty()
    timestamp = ndb.StringProperty()


    def getPositionFromID(self):
        result = Position(self.longitude,self.longitude)
        return result

    @staticmethod
    def getCarbyID(id):
        app_key = ndb.Key('Car',id)
        logging.debug(Car.get_by_id(id))
        return app_key.get()
