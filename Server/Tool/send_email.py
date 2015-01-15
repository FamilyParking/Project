__author__ = 'Nazzareno'

from google.appengine.api import mail


class Send_email():

    @staticmethod
    def send_code(code,email):
        message = mail.EmailMessage(sender="Family Parking <familyparkingapp@gmail.com>",
                                                        subject="Verification code")
        message.body = "The code is: " + str(code)
        message.to = "<" + email + ">"
        message.send()