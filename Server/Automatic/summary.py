import logging
import os

#from Automatic.file import File
from Cloud_Storage.history_park import History_park
from Cloud_Storage.user_house import User_house

__author__ = 'Nazzareno'

from google.appengine.api import app_identity

class Summary():

    # @staticmethod
    # def create_statistic():
    #     bucket_name = os.environ.get('BUCKET_NAME', app_identity.get_default_gcs_bucket_name())
    #
    #     temp_file = File()
    #
    #     temp_file.response.headers['Content-Type'] = 'text/plain'
    #     temp_file.response.write('Demo GCS Application running from Version: '
    #                         + os.environ['CURRENT_VERSION_ID'] + '\n')
    #     temp_file.response.write('Using bucket name: ' + bucket_name + '\n\n')
    #
    #     bucket = '/' + bucket_name
    #     filename = bucket + '/demo-testfile'
    #     temp_file.tmp_filenames_to_clean_up = []
    #
    #     try:
    #         temp_file.create_file(filename)
    #         temp_file.response.write('\n\n')
    #     except Exception, e:
    #         logging.exception(e)
    #         temp_file.delete_files()
    #         temp_file.response.write('\n\nThere was an error running the demo! '
    #                             'Please check the logs for more details.\n')
    #
    #     logging.debug("Print cron job")

    @staticmethod
    def user_house(id_user):

        history = History_park.history_parked_from_user(id_user)

        max_value = 0
        latitude = 0
        longitude = 0
        for value in history:
            counter_max = 0
            for value_inside in history:
                if History_park.right_point(value.latitude, value.longitude,value_inside.latitude, value_inside.longitude):
                    counter_max +=1

            if counter_max>=max_value:
                max_value = counter_max
                latitude = value.latitude
                longitude = value.longitude

        User_house.update(id_user, latitude, longitude)
