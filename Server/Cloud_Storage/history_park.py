__author__ = 'Nazzareno'

from google.appengine.ext import ndb

import cmath


class History_park(ndb.Model):
    id_car = ndb.IntegerProperty()
    latitude = ndb.StringProperty()
    longitude = ndb.StringProperty()
    parked = ndb.IntegerProperty()

    @staticmethod
    def history_from_car(id_car):
        result_history = History_park.query(History_park.id_car == id_car)
        return result_history

    @staticmethod
    def get_notification(id_car, latitude, longitude):

        history = History_park.history_from_car(id_car)

        if history.count() < 20:
            return 1
        else:
            counter = 0
            parked_counter = 0
            for value in history:
                if History_park.right_point(latitude, longitude, value.latitude, value.longitude):
                    counter += 1
                    if value.parked == 1:
                        parked_counter += 1

            if counter < 20:
                return 1
            else:
                # If the percentage of parked counter and counter is bigger than 30% the application send notification
                if parked_counter / counter > 0.3:
                    return 1
                else:
                    return 0

        return 0


    @staticmethod
    def right_point(latitude, longitude, lat_point, lon_point):

        radius = 1
        r = radius / 6371
        delta_lon = cmath.asin(cmath.sin(r) / cmath.cos(latitude))
        delta_lat = cmath.asin(cmath.sin(r) / cmath.cos(longitude))

        min_lat = latitude - delta_lat
        max_lat = latitude + delta_lat
        min_lon = longitude - delta_lon
        max_lon = longitude + delta_lon
        if (min_lat <= lat_point <= max_lat) and (min_lon <= lon_point <= max_lon):
            return True
        else:
            return False


    @staticmethod
    def check_user_exist(id_user, id_group):
        temp_user = User_group.getGroupFromUser(id_user)
        if temp_user.count() == 0:
            return 1
        else:
            for group in temp_user:
                if group.id_group == id_group:
                    return -1
        return 1

    @staticmethod
    def delete_contact_group(id_user, id_group):
        delete_entry = User_group.getGroupFromUser(id_user)
        for group in delete_entry:
            # logging.debug("ID del gruppo da controllare: "+str(group.id_group)+" id del gruppo da eliminare: "+str(id_group))
            if long(group.id_group) == long(id_group):
                #l ogging.debug("Gruppo da eliminare: "+str(group))
                group.key.delete()
        return 0;