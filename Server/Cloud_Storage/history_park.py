import logging
from time import strftime
from Cloud_Storage.user_house import User_house
from Tool.google_api_request import Google_api_request

__author__ = 'Nazzareno'

from google.appengine.ext import ndb

import cmath
import datetime

LOCAL_PRINT = False

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
    def history_parked_from_user(id_user):
        result_history = History_park.query(ndb.AND(History_park.id_user == id_user, History_park.parked == 1))
        return result_history

    @staticmethod
    def parky(id_user, latitude, longitude, timestamp):

        # temp_time = timestamp.strftime("%y-%m-%d %H:%M:%S")
        #
        # if static_variable.DEBUG:
        # logging.debug("timestamp iso_format: "+temp_time)

        History_park.weight_date(timestamp)

        # Request all the story of the car
        history = History_park.history_from_user(id_user)

        # Insert the new value
        new_history_park = History_park(id_user=id_user, latitude=latitude, longitude=longitude, timestamp=timestamp,
                                        parked=0)
        new_history_park.put()

        # If the number of history is less than minimum value not have sense try to guest
        if history.count() < static_variable.min_value:
            return 1
        else:
            counter = 0
            parked_counter = 0
            for value in history:
                # if DEBUG:
                # logging.debug("Value history=lat:"+str(value.latitude)+" long:"+str(value.longitude))

                # if static_variable.DEBUG:
                #                    logging.debug("Timestamp: "+value.timestamp)


                if History_park.right_point(latitude, longitude, value.latitude, value.longitude):
                    weight_park = History_park.weight_date(value.timestamp)

                    if static_variable.DEBUG and LOCAL_PRINT:
                        logging.debug("weight of park: " + str(weight_park) + " value lat: " + str(value.latitude))

                    counter += weight_park
                    if value.parked == 1:
                        parked_counter += weight_park

            if static_variable.DEBUG:
                logging.debug("Value counter user=" + str(counter) + " value parked_counter=" + str(parked_counter))

            # If the number of history is less than minimum value not have sense try to guest
            if counter < static_variable.min_value:
                return 1

            else:

                # Check if that place is the house of user --> NOTIFICATION
                house = User_house.get_house(id_user)
                if house != 0:
                    if History_park.right_point(latitude, longitude, house.getLatitude(), house.getLongitude()):

                        if static_variable.DEBUG:
                            logging.debug("HOUSE")

                        return 1

                result_google_API = Google_api_request.request_place(latitude, longitude)

                # If the google API return that close there is a Station and you park there at least one time -->
                # NOTIFICATION
                if result_google_API == 1 and parked_counter > 1:
                    if static_variable.DEBUG:
                        logging.debug("STATION")
                    return 1

                # If the google API return that close there is a parking and you park there at least one time -->
                # NOTIFICATION
                if result_google_API == 2 and parked_counter > 1:
                    if static_variable.DEBUG:
                        logging.debug("PARK")
                    return 1

                # DEBUG
                if static_variable.DEBUG:
                    percentage = parked_counter / counter
                    logging.debug("Percentage: " + str(percentage))

                # If the percentage of parked counter and counter is bigger than 30% the application --> NOTIFICATION
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
        # logging.debug("min_lat:"+str(min_lat)+" min_lon:"+str(min_lon)+
        # " max_lat:"+str(max_lat)+" max_lon:"+str(max_lon))

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

    @staticmethod
    def weight_date(date1):
        temp_date1 = date1.split(" ")
        temp_day_date1 = temp_date1[0].split("-")
        if static_variable.DEBUG and LOCAL_PRINT:
            logging.debug(str(temp_day_date1) + " value year: " + str(
                int(temp_day_date1[0])) + " value now year " + datetime.datetime.utcnow().strftime("%Y"))
        if int(temp_day_date1[0]) == int(datetime.datetime.utcnow().strftime("%Y")):
            if static_variable.DEBUG and LOCAL_PRINT:
                logging.debug("value of month: " + str(temp_day_date1[1]))

            if int(temp_day_date1[1]) == int(datetime.datetime.utcnow().strftime("%m")):
                return 1.0
            else:
                if int(datetime.datetime.utcnow().strftime("%m")) - int(temp_day_date1[1]) <= 1:
                    return 0.7
                elif int(datetime.datetime.utcnow().strftime("%m")) - int(
                        temp_day_date1[1]) <= static_variable.range_month:
                    return 0.5
                else:
                    return 0.3
        else:
            return 0.1