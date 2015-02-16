__author__ = 'Nazzareno'

from google.appengine.ext import ndb

class Group(ndb.Model):
    name = ndb.StringProperty()
    timestamp = ndb.StringProperty()

    @staticmethod
    def getGroupbyID(id):
        app_key = Group.get_by_id(long(id))
        return app_key

    @staticmethod
    def delete_group_ID(id):
        app_key = Group.get_by_id(long(id))
        app_key.key.delete()

