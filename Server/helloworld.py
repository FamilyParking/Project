import webapp2
import json
import statusReturn
import statistic
import urllib
from google.appengine.api import urlfetch
from google.appengine.api import mail
from google.appengine.ext import ndb



class MainPage(webapp2.RequestHandler):
    def get(self):
		in_file = open("website/index.html", "r")
		MAIN_PAGE_HTML = in_file.read()
		self.response.write(MAIN_PAGE_HTML)
		in_file.close()


class SendEmail(webapp2.RequestHandler):
    def post(self):
		try:
			data = json.loads(self.request.body)
			contact_key = ndb.key(statistic.Statistic,data["ID"])
			new_contact = statistic.Statistic(id_android=data["ID"], counter=1)
			add_db_contact = new_contact.put()

			try:
				message = mail.EmailMessage(sender="Family Parking <familyparkingapp@gmail.com>",
											subject="Position of car")
				message.body = "Your car is parked here: http://www.google.com/maps/place/" + data["latitude"] + "," + \
							   data["longitude"] + " number of contact: "
				receiver_mail = data["email"]
				i = 0
				for value in receiver_mail:
					try:
						message.to = "<" + value + ">"
						message.send()
						i = i + 1;
					except:
						error = statusReturn.StatusReturn(3, i);
						self.response.write(error.toJSON())
						self.error(500)
				right = statusReturn.StatusReturn(4, 0);
				self.response.write(right.toJSON())
			except:
				self.error(500)
				error = statusReturn.StatusReturn(2, 0);
				self.response.write(error.toJSON())

		except:
			self.error(500)
			error = statusReturn.StatusReturn(1, 0);
			self.response.write(error.toJSON())

application = webapp2.WSGIApplication([
										  ('/', MainPage),
										  ('/sign', SendEmail),
									  ], debug=True)