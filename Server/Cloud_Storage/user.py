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
    is_user = ndb.IntegerProperty()
    id_gcm = ndb.IntegerProperty()

    def toString_JSON(self):
        if (self.nickname != None):
            result = {"Name": str(self.nickname), "Email": str(self.email)}
        else:
            result = {"Name": str(self.email), "Email": str(self.email)}
        return result

    def querySearch_id_android(self):
        id_android = User.query(User.id_android == self.id_android)
        return id_android

    def querySearch_email(self):
        try:
            email = User.query(User.email == self.email)
        except:
            logging.debug(sys.exc_info())
        return email

    @staticmethod
    def static_querySearch_email(email_user):
        try:
            email = User.query(User.email == email_user)
        except:
            logging.debug(sys.exc_info())
        return email

    @staticmethod
    def is_user_check(email_user):
        try:
            email = User.static_querySearch_email(email_user)
            if email.count() == 0:
                return 0
            else:
                return email.get().key.id()
        except:
            logging.debug(sys.exc_info())
        return -1

    @staticmethod
    def is_registered_check(id):
        temp_user = User.get_user_by_id(id)
        return temp_user.is_user

    @staticmethod
    def update_google_code(email_user, id_android):
        try:
            code = User.static_querySearch_email(email_user)

            temp_user = code.get()
            temp_user.id_android = id_android
            temp_user.is_user = 1
            temp_user.put()
            return 0
        except:
            logging.debug(sys.exc_info())
            return -1

    @staticmethod
    def get_id_android(id):
        temp_user = User.get_user_by_id(id)
        return temp_user.id_android

    @staticmethod
    def get_email_user(id):
        temp_user = User.get_user_by_id(id)
        return temp_user.email

    @staticmethod
    def get_user_by_id(id):
        temp_user = User.get_by_id(long("%.0f" % float(id)))
        return temp_user

    def check_code(self, new_code):
        if new_code == self.temp_code:
            if self.temp_code == self.code:
                return 0
            else:
                self.code = new_code
                self.put()
                return 1
        else:
            return -1

    def update_contact(self, code,name):
        self.temp_code = code
        self.nickname = name
        self.is_user=0
        try:
            self.put()
            return True
        except:
            logging.debug(sys.exc_info())
            return False

    @staticmethod
    def all_user():
        try:
            users = User.query()
        except:
            logging.debug(sys.exc_info())
        return users

