__author__ = 'Nazzareno'

from google.appengine.api import mail

from setting import static_variable


class Send_email():

    @staticmethod
    def check_mail(email):
        if mail.is_email_valid(email):
            return True
        else:
            return False


    @staticmethod
    def send_code(code, email):
        message = mail.EmailMessage(sender=static_variable.sender,
                                    subject="Verification code")
        message.body = "The code is: " + str(code)
        message.to = "<" + email + ">"
        message.send()

    @staticmethod
    def send_position(email, latitude, longitude, name,car):
        message = mail.EmailMessage(sender=static_variable.sender,
                                    subject="Position of car")
        message.body = "Your car( "+str(car)+" ) has been parked by " + name + " here: http://www.google.com/maps/place/" + latitude + "," + longitude + " \nFind the application here --> http://www.familyparking.it"

        message.to = "<" + email + ">"
        message.send()

    @staticmethod
    def send_adding_group(email, nome, car):
        message = mail.EmailMessage(sender=static_variable.sender,
                                    subject="Shared car with you")
        message.body = nome + " shared " + car + " with you. \nFind the application here --> http://www.familyparking.it"
        message.to = "<" + email + ">"
        message.send()