import logging
import sys
from setting import static_variable

__author__ = 'Nazzareno'

from google.appengine.ext import ndb


class User_house(ndb.Model):
    id_user = ndb.IntegerProperty()
    latitude = ndb.StringProperty()
    longitude = ndb.StringProperty()

    def update_house(self, latitude, longitude):
        self.latitude = latitude
        self.longitude = longitude
        self.put()
        return 0

    @staticmethod
    def get_by_id_user(id_user):
        user = None
        try:
            user = User_house.query(User_house.id_user == id_user)
        except:
            logging.debug(sys.exc_info())
        if user.count() > 0:
            return user
        else:
            return 0

    @staticmethod
    def update(id_user, latitude, longitude):
        temp_user = User_house.get_by_id_user(id_user)
        if static_variable.DEBUG:
            logging.debug(temp_user)
        if temp_user == 0:
            temp_user = User_house(id_user=id_user, latitude=latitude, longitude=longitude)
            temp_user.put()
        else:
            app_user = temp_user.get()
            app_user.update_house(latitude,longitude)