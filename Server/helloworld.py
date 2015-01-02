import webapp2
import json
from google.appengine.api import mail

MAIN_PAGE_HTML = """\
    <html>
    <body>
    <form action="/sign" method="post">
    <div><textarea name="emailAddress" rows="3" 

cols="60"></textarea></div>
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
		try:
			data = json.loads(self.request.body)
		except:
			self.error(1)
		message = mail.EmailMessage(sender="Family Parking <familyparkingapp@gmail.com>",subject="Position of car")

		try: 
			message.body = "Your car is parked here: http://www.google.com/maps/place/"+data["latitude"]+","+ data["longitude"]
		except:
			self.error(10)
		receiver_mail = data["email"]
		
		i = 0
		for value in receiver_mail:
			try:
				message.to = "<"+value+">"
				message.send()
				i = i+1;
				self.response.write("DONE!")
			except:
				self.error(5)
		self.response.write("done")



application = webapp2.WSGIApplication([
                                       ('/', MainPage),
                                       ('/sign', Guestbook),
                                       ], debug=True)

