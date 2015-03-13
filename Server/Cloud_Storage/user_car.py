import logging
from Cloud_Storage.user import User

from google.appengine.ext import ndb


class User_car(ndb.Model):
    id_user = ndb.IntegerProperty()
    id_car = ndb.IntegerProperty()

    def getUser(self):
        user = User.static_querySearch_email(self.id_user)
        return user

    @staticmethod
    def getUserFromCar(id_car):
        result_group = User_car.query(User_car.id_car == long(id_car))
        return result_group

    @staticmethod
    def getCarFromUser(id_user):
        result_user = User_car.query(User_car.id_user == long(id_user))
        return result_user

    @staticmethod
    def check_user_exist(id_user, id_car):
        temp_user = User_car.getCarFromUser(id_user)
        if temp_user.count() == 0:
            return 1
        else:
            for car in temp_user:
                if car.id_car == id_car:
                    return -1
        return 1

    @staticmethod
    def deleteCarUser(id_user, id_car):
        delete_entry = User_car.getCarFromUser(id_user)
        for car in delete_entry:
            if long(car.id_car) == long(id_car):
                car.key.delete()
        return 0

    @staticmethod
    def insertContactCar(user, car):
        new_contact_car = User_car(id_user=user, id_car=long(car))
        new_contact_car.put()
        return 0
