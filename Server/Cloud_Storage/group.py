__author__ = 'Nazzareno'

from google.appengine.ext import ndb

class Group(ndb.Model):
    name = ndb.StringProperty()
    timestamp = ndb.StringProperty()

