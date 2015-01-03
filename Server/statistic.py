__author__ = 'Nazzareno'

from google.appengine.ext import ndb

class Statistic(ndb.Model):
  id_android = ndb.StringProperty()
  counter = ndb.IntegerProperty()
