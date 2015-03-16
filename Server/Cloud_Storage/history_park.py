import logging
from time import strftime
from Tool.google_api_request import Google_api_request

__author__ = 'Nazzareno'

from google.appengine.ext import ndb

import cmath

from setting import static_variable

class History_park(ndb.Model):
    id_user = ndb.IntegerProperty()
    latitude = ndb.StringProperty()
    longitude = ndb.StringProperty()
    parked = ndb.IntegerProperty()
    timestamp = ndb.StringProperty()

    def update_parked(self):
        self.parked = 1
        self.put()

    @staticmethod
    def history_from_user(id_user):
        result_history = History_park.query(History_park.id_user == id_user)
        return result_history

    @staticmethod
    def parky(id_user, latitude, longitude, timestamp):

        # temp_time = timestamp.strftime("%y-%m-%d %H:%M:%S")
        #
        # if static_variable.DEBUG:
        #     logging.debug("timestamp iso_format: "+temp_time)


        # Request all the story of the car
        history = History_park.history_from_user(id_user)

        # Insert the new value
        new_history_park = History_park(id_user=id_user, latitude=latitude, longitude=longitude, timestamp=timestamp,
                                        parked=0)
        new_history_park.put()

        if history.count() < static_variable.min_value:
            return 1
        else:
            counter = 0
            parked_counter = 0
            for value in history:
                # if DEBUG:
                # logging.debug("Value history=lat:"+str(value.latitude)+" long:"+str(value.longitude))

                if static_variable.DEBUG:
                    logging.debug("Timestamp: "+value.timestamp)

                if History_park.right_point(latitude, longitude, value.latitude, value.longitude):
                    counter += 1
                    if value.parked == 1:
                        parked_counter += 1

            if static_variable.DEBUG:
                logging.debug("Value counter user=" + str(counter) + " value parked_counter=" + str(parked_counter))

            if counter < static_variable.min_value:
                return 1

            elif Google_api_request.request_place(latitude, longitude) == 1 and parked_counter > 0:
                return 1

            else:
                # If the percentage of parked counter and counter is bigger than 30% the application send notification
                if parked_counter / counter > static_variable.percentage_not:
                    return 1
                else:
                    return 0

        return 0

    @staticmethod
    def right_point(latitude, longitude, lat_point, lon_point):
        r = static_variable.radius / 6371.0
        delta_lon = abs(cmath.asin(cmath.sin(float(r)) / cmath.cos(float(latitude))))
        delta_lat = abs(cmath.asin(cmath.sin(float(r)) / cmath.cos(float(longitude))))

        # if DEBUG:
        # logging.debug("lat_point:"+str(lat_point)+" lon_point:"+str(lon_point))

        min_lat = float(latitude) - delta_lat.real
        max_lat = float(latitude) + delta_lat.real
        min_lon = float(longitude) - delta_lon.real
        max_lon = float(longitude) + delta_lon.real

        # if DEBUG:
        #     logging.debug("min_lat:"+str(min_lat)+" min_lon:"+str(min_lon)+" max_lat:"+str(max_lat)+" max_lon:"+str(max_lon))

        if (float(min_lat) <= float(lat_point) <= float(max_lat)) and (
                        float(min_lon) <= float(lon_point) <= float(max_lon)):
            # if DEBUG:
            #     logging.debug("Inside the IF")
            return True
        else:
            return False

    @staticmethod
    def update_history(id_user, latitude, longitude, timestamp):

        result_query = History_park.query(
            History_park.id_user == id_user and History_park.latitude == latitude and
            History_park.longitude == longitude and History_park.timestamp == timestamp)

        if result_query.count() == 0:
            temp_history = History_park(id_user=id_user, latitude=latitude, longitude=longitude, timestamp=timestamp,
                                        parked=1)
            temp_history.put()
        else:
            temp_history = result_query.get()
            temp_history.update_parked()