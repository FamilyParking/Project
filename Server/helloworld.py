import webapp2
import json
import FamError
import urllib
from google.appengine.api import urlfetch
from google.appengine.api import mail



class MainPage(webapp2.RequestHandler):
    def get(self):
		in_file = open("website/index.html", "r")
		MAIN_PAGE_HTML = in_file.read()
		self.response.write(MAIN_PAGE_HTML)
		in_file.close()


class Guestbook(webapp2.RequestHandler):
    def post(self):
		try:
			data = json.loads(self.request.body)
		except:
			self.error(500)
			error = FamError(1, 0);
			self.response.write(error.toString())
		message = mail.EmailMessage(sender="Family Parking <familyparkingapp@gmail.com>", subject="Position of car")

		try:
			message.body = "Your car is parked here: http://www.google.com/maps/place/" + data["latitude"] + "," + data[
				"longitude"]
			receiver_mail = data["email"]
		except:
			self.error(500)
			error = FamError(2, 0);
			self.response.write(error.toString())

		i = 0
		for value in receiver_mail:
			try:


				message.to = "<" + value + ">"
				message.send()
				i = i + 1;
			except:
				error = FamError(3, i);
				self.response.write(error.toString())
				self.error(500)
		self.response.write("Position sent")


application = webapp2.WSGIApplication([
										  ('/', MainPage),
										  ('/sign', Guestbook),
									  ], debug=True)