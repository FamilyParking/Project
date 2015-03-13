import httplib
import json
import logging
import urllib2

__author__ = 'Nazzareno'

DEBUG = True
class Google_api_request:

    @staticmethod
    def request_place(latitude,longitude):



        #url = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?location='
        url = '/maps/api/place/nearbysearch/json?location='
        url_place = url+latitude+","+longitude
        url_radius = url_place+"&radius=500"
        url_type = url_radius+"&types=subway_station|train_station"
        api_key = "AIzaSyAN2KZpzIpWmPQidczGiyo3ZlV4j1ERe2U"
        my_key = "key=" + api_key

        conn = httplib.HTTPSConnection("maps.googleapis.com")
        conn.request("GET", url_type+"&"+my_key)
        r1 = conn.getresponse()

        response = json.load(r1.read())

        if DEBUG:
            logging.debug("Status request google place --> "+response["status"])
        if response["status"] == "OK":
            if DEBUG:
                logging.debug("Station near the place")
            return 1
        else:
            return 0