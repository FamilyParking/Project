__author__ = 'Nazzareno'

from google.appengine.ext import ndb


class Group(ndb.Model):
    name = ndb.StringProperty()
    timestamp = ndb.StringProperty()

    def update(self, name):
        self.name = name
        self.put()
        return 0


    @staticmethod
    def getGroupbyID(id):
        app_key = Group.get_by_id(long(id))
        return app_key


    @staticmethod
    def delete_group_ID(id):
        app_key = Group.get_by_id(long(id))
        app_key.key.delete()

    @staticmethod
    def update_group(id, name):
        app_key = Group.getGroupbyID(id)
        app_key.name = name
        app_key.put()

