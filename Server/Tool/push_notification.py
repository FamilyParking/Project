import httplib
import logging
import urllib2

__author__ = 'Nazzareno'

import json


class push_class:
    @staticmethod
    def send_post_request(regId):
        json_data = {"data": {
            "Category": "FOOD",
            "Type": "VEG",
            "Title": "Family Parking",
        }, "registration_ids": [regId],
        }
        url = 'https://android.googleapis.com/gcm/send'
        apiKey = "AIzaSyAN2KZpzIpWmPQidczGiyo3ZlV4j1ERe2U"
        myKey = "key=" + apiKey

        data = json.dumps(json_data)
        headers = {'Content-Type': 'application/json', 'Authorization': myKey}
        req = urllib2.Request(url, data, headers)

        f = urllib2.urlopen(req)
        response = json.loads(f.read())
        reply = {}

        # logging.debug(response)

        if response['failure'] == 0:
            reply['error'] = '0'
        else:
            response['error'] = '1'
