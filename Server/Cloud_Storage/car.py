__author__ = 'Nazzareno'


from google.appengine.ext import ndb

class Car(ndb.Model):
    model = ndb.StringProperty()
    latitude = ndb.FloatProperty()
    longitude = ndb.FloatProperty()
    timestamp = ndb.StringProperty()

    def __init__(self,id,model,latitude,longitude,timestamp):
        self.id = id
        self.model = model
        self.latitude = latitude
        self.longitude = longitude
        self.timestamp = timestamp
