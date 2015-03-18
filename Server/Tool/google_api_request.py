import httplib
import json
import logging

__author__ = 'Nazzareno'

from setting import static_variable

class Google_api_request:

    @staticmethod
    def request_place(latitude,longitude):

        #url = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?location='
        url = '/maps/api/place/nearbysearch/json?location='
        url_place = url+latitude+","+longitude
        url_radius = url_place+"&radius="+str(static_variable.google_radius)
        url_type = url_radius+"&types="+static_variable.google_types
        my_key = "key=" + static_variable.google_api_key

        conn = httplib.HTTPSConnection("maps.googleapis.com")
        conn.request("GET", url_type+"&"+my_key)
        r1 = conn.getresponse()

        response = json.loads(r1.read())

        if static_variable.DEBUG:
            logging.debug("Status request google place --> "+response["status"])
        if response["status"] == "OK":
            temp_result = response["results"]
            for temp in temp_result:
                #logging.debug(temp)
                if static_variable.DEBUG:
                    if static_variable.DEBUG:
                        logging.debug(str(temp["name"])+" is near")
                    for value in temp["types"]:
                        if value == "parking":
                            return 2
                        elif value == "train_station" or value == "subway_station":
                            return 1
            return 1

        else:
            return 0