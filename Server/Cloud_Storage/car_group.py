__author__ = 'Nazzareno'

from google.appengine.ext import ndb


class Car_group(ndb.Model):
    id_car = ndb.IntegerProperty()
    id_group = ndb.IntegerProperty()

    @staticmethod
    def getCarFromGroup(id_group):
        result_group = Car_group.query(Car_group.id_group == id_group)
        return result_group

    @staticmethod
    def getGroupFromCar(id_car):
        result_Car = Car_group.query(Car_group.id_car == id_car)
        return result_Car

    @staticmethod
    def getCarGroup(id_car, id_group):
        result_Car = Car_group.query(Car_group.id_car == id_car and Car_group.id_group == id_group)
        return result_Car

    @staticmethod
    def delete_car_ID(id_car, id_group):
        app_key = Car_group.getCarGroup(id_car, id_group)
        app_key.key.delete()