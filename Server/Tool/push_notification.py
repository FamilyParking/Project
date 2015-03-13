import logging
import urllib2

__author__ = 'Nazzareno'

import json


class Push_notification():

    @staticmethod
    def send_push_park(reg_id,name_car):
        json_data = {
            "data": {
                "name": str(name_car),
                "type": "park",
            },
            "registration_ids": [reg_id],
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

        logging.debug("Lista dei contatti da notificare: " + str(reg_id))

        if response['failure'] == 0:
            reply['error'] = '0'
        else:
            reply['error'] = '1'

    @staticmethod
    def send_push_add_group(regId):
        json_data = {"data": {
            "type": "group",
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
            reply['error'] = '1'
