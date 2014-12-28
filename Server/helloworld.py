import webapp2
import json
from google.appengine.api import mail

MAIN_PAGE_HTML = """\
    <html>
    <body>
    <form action="/sign" method="post">
    <div><textarea name="emailAddress" rows="3" cols="60"></textarea></div>
    <div><input type="submit" value="Sign Guestbook"></div>
    </form>
    </body>
    </html>
    """

class MainPage(webapp2.RequestHandler):
    def get(self):
        self.response.write(MAIN_PAGE_HTML)

class Guestbook(webapp2.RequestHandler):
    def post(self):
        data = json.loads(self.request.body)

        message = mail.EmailMessage(sender="Family Parking <familyparkingapp@gmail.com>",subject="Position of car")

        message.body = "Your car is parked here: http://www.google.com/maps/place/"+data["latitude"]+","+ data["longitude"]

        receiver_mail = data["email"]

        for value in receiver_mail:
            message.to = "<"+value+">"
            message.send()

        self.response.write("we are begginers +5")

application = webapp2.WSGIApplication([
                                       ('/', MainPage),
                                       ('/sign', Guestbook),
                                       ], debug=True)