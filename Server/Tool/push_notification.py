import logging
import urllib2

__author__ = 'Nazzareno'

import json

from setting import static_variable

class Push_notification():

    @staticmethod
    def send_push_park(reg_id, name_car, user, id_car):
        json_data = {
            "data": {
                "ID_car":long(id_car),
                "User": str(user),
                "Car": str(name_car),
                "Type": "Park",
            },
            "registration_ids": [reg_id],
        }
        url = 'https://android.googleapis.com/gcm/send'
        myKey = "key=" + static_variable.google_api_key

        data = json.dumps(json_data)
        headers = {'Content-Type': 'application/json', 'Authorization': myKey}
        req = urllib2.Request(url, data, headers)

        f = urllib2.urlopen(req)
        response = json.loads(f.read())
        reply = {}

        if static_variable.DEBUG:
            logging.debug("Name of car: "+str(name_car)+" User: "+str(user))
            logging.debug("Lista dei contatti da notificare: " + str(reg_id))

        if response['failure'] == 0:
            reply['error'] = '0'
        else:
            reply['error'] = '1'

    @staticmethod
    def send_push_add_group(reg_id, user, car):
        json_data = {"data": {
            "Type": "Group",
            "User": str(user),
            "Car": str(car),
        }, "registration_ids": [reg_id],
        }
        url = 'https://android.googleapis.com/gcm/send'
        myKey = "key=" + static_variable.google_api_key

        data = json.dumps(json_data)
        headers = {'Content-Type': 'application/json', 'Authorization': myKey}
        req = urllib2.Request(url, data, headers)

        f = urllib2.urlopen(req)
        response = json.loads(f.read())
        reply = {}

        if static_variable.DEBUG:
            logging.debug("Name of car: "+str(car)+" User: "+str(user))

        # logging.debug(response)

        if response['failure'] == 0:
            reply['error'] = '0'
        else:
            reply['error'] = '1'
