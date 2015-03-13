from Tool.google_api_request import Google_api_request

__author__ = 'Nazzareno'

from google.appengine.ext import ndb

import cmath


class History_park(ndb.Model):
    id_user = ndb.IntegerProperty()
    latitude = ndb.StringProperty()
    longitude = ndb.StringProperty()
    parked = ndb.IntegerProperty()
    timestamp = ndb.StringProperty()

    @staticmethod
    def history_from_user(id_user):
        result_history = History_park.query(History_park.id_user == id_user)
        return result_history

    @staticmethod
    def get_notification(id_user, latitude, longitude, timestamp):

        # Request all the story of the car
        history = History_park.history_from_user(id_user)

        # Insert the new value
        new_history_park = History_park(id_user=id_user, latitude=latitude, longitude=longitude, timestamp=timestamp,
                                        parked=0)
        new_history_park.put()

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

            elif Google_api_request.request_place(latitude, longitude) == 1 and parked_counter > 0:
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
