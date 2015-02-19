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

    @staticmethod
    def send_position(email,latitude, longitude):
        message = mail.EmailMessage(sender="Family Parking <familyparkingapp@gmail.com>",
                                                        subject="Position of car")
        message.body = "Your car is parked here: http://www.google.com/maps/place/" + latitude + "," + longitude
        message.to = "<" + email + ">"
        message.send()