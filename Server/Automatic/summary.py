import logging
import os

__author__ = 'Nazzareno'

from google.appengine.api import app_identity

class Summary():

    @staticmethod
    def create_statistic():
        bucket_name = os.environ.get('BUCKET_NAME', app_identity.get_default_gcs_bucket_name())

        temp_file = file.File()

        temp_file.response.headers['Content-Type'] = 'text/plain'
        temp_file.response.write('Demo GCS Application running from Version: '
                            + os.environ['CURRENT_VERSION_ID'] + '\n')
        temp_file.response.write('Using bucket name: ' + bucket_name + '\n\n')

        bucket = '/' + bucket_name
        filename = bucket + '/demo-testfile'
        temp_file.tmp_filenames_to_clean_up = []

        try:
            temp_file.create_file(filename)
            temp_file.response.write('\n\n')
        except Exception, e:
            logging.exception(e)
            temp_file.delete_files()
            temp_file.response.write('\n\nThere was an error running the demo! '
                                'Please check the logs for more details.\n')

        logging.debug("Print cron job")