import httplib
import json
import logging
import urllib2

__author__ = 'Nazzareno'

class Google_api_request:

    @staticmethod
    def request_place(latitude,longitude):



        #url = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?location='
        url = '/maps/api/place/nearbysearch/json?location='
        url_place = url+latitude+","+longitude
        url_radius = url_place+"&radius=500"
        url_type = url_radius+"&types=subway_station"
        apiKey = "AIzaSyAN2KZpzIpWmPQidczGiyo3ZlV4j1ERe2U"
        myKey = "key=" + apiKey

        conn = httplib.HTTPSConnection("maps.googleapis.com")
        conn.request("GET", url_type+"&"+myKey)
        r1 = conn.getresponse()

        # headers = {'Content-Type': 'application/json', 'Authorization': myKey}
        # req = urllib2.Request(url_type, headers)
        # f = urllib2.urlopen(req)
        # response = json.loads(f.read())
        reply = {}



        logging.debug(r1.read())