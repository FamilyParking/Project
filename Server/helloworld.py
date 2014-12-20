import cgi
import webapp2
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
        message = mail.EmailMessage(sender="Family Parking <familyparkingapp@gmail.com>",
                            subject="Your account has been approved")

        receiver = cgi.escape(self.request.get('emailAddress'))

        message.to = "Albert Johnson <%s>" % receiver
        message.body = """
Dear Obaida:

Your car is parked!!!!!
"""

        message.send()


        self.response.write("we are begginers +3")
        self.response.write(receiver)
        
application = webapp2.WSGIApplication([
                                       ('/', MainPage),
                                       ('/sign', Guestbook),
                                       ], debug=True)