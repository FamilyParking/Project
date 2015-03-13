import logging
from Tool.google_api_request import Google_api_request

__author__ = 'Nazzareno'

from google.appengine.ext import ndb

import cmath

DEBUG = True


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

        if history.count() < 10:
            return 1
        else:
            counter = 0
            parked_counter = 0
            for value in history:
                #if DEBUG:
                    # logging.debug("Value history=lat:"+str(value.latitude)+" long:"+str(value.longitude))

                if History_park.right_point(latitude, longitude, value.latitude, value.longitude):
                    counter += 1
                    if value.parked == 1:
                        parked_counter += 1

            if DEBUG:
                logging.debug("Value counter user="+str(counter)+" value parked_counter="+str(parked_counter))

            if counter < 10:
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

        radius = 1.0
        r = radius / 6371.0
        delta_lon = abs(cmath.asin(cmath.sin(float(r)) / cmath.cos(float(latitude))))
        delta_lat = abs(cmath.asin(cmath.sin(float(r)) / cmath.cos(float(longitude))))

        # if DEBUG:
        #     logging.debug("lat_point:"+str(lat_point)+" lon_point:"+str(lon_point))

        min_lat = float(latitude) - delta_lat.real
        max_lat = float(latitude) + delta_lat.real
        min_lon = float(longitude) - delta_lon.real
        max_lon = float(longitude) + delta_lon.real

        # if DEBUG:
        #     logging.debug("min_lat:"+str(min_lat)+" min_lon:"+str(min_lon)+" max_lat:"+str(max_lat)+" max_lon:"+str(max_lon))

        if (float(min_lat) <= float(lat_point) <= float(max_lat)) and (float(min_lon) <= float(lon_point) <= float(max_lon)):
            if DEBUG:
                logging.debug("Inside the IF")
            return True
        else:
            return False
