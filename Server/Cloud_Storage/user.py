import logging
import sys

__author__ = 'Nazzareno'

import datetime
from google.appengine.ext import ndb

class User(ndb.Model):
  id_android = ndb.StringProperty()
  counter = ndb.IntegerProperty()
  latitude = ndb.StringProperty()
  longitude = ndb.StringProperty()
  last_time = ndb.StringProperty()

  def querySearch(self):
    id_android = User.query(User.id_android == self.id_android)
    return id_android

  def update_contact(self,latitude,longitude):
    self.counter += 1
    self.latitude = latitude
    self.longitude = longitude
    self.last_time = str(datetime.datetime.now())
    try:
      self.put()
      return True
    except:
      logging.debug(sys.exc_info())
      return False
