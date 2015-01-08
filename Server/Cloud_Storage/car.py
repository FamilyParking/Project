
__author__ = 'Nazzareno'

from Class.position import Position

from google.appengine.ext import ndb

class Car(ndb.Model):
    model = ndb.StringProperty()
    latitude = ndb.StringProperty()
    longitude = ndb.StringProperty()
    timestamp = ndb.StringProperty()


    def getPositionFromID(self):
        result = Position(self.longitude,self.longitude)
        return result
