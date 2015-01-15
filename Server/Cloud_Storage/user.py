import logging
import sys

__author__ = 'Nazzareno'

import datetime
from google.appengine.ext import ndb

class User(ndb.Model):
    id_android = ndb.StringProperty()
    email = ndb.StringProperty()
    code = ndb.IntegerProperty()
    temp_code = ndb.IntegerProperty()
    nickname = ndb.StringProperty()

    def querySearch_id_android(self):
        id_android = User.query(User.id_android == self.id_android)
        return id_android

    def querySearch_email(self):
        email = User.query(User.email == self.email)
        return email

    def check_code(self,new_code):
        if new_code == self.temp_code:
            if self.temp_code == self.code:
                return True
            else:
                self.code = new_code
                return True
        else:
            return False

    def update_contact(self,code):
        self.temp_code = code
        try:
          self.put()
          return True
        except:
          logging.debug(sys.exc_info())
          return False
