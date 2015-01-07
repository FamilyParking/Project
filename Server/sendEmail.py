import logging
import webapp2
import json
import sys
import statusReturn
import statistic
import datetime
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
			new_contact = statistic.Statistic(id_android=data["ID"], counter=1,latitude = data["latitude"],longitude = data["longitude"],last_time = str(datetime.datetime.now()))

			try:
				contact_key = new_contact.querySearch()
				#logging.debug(contact_key.get())
			except:
				logging.debug(sys.exc_info())

			try:
				if contact_key.count() == 0:
					add_db_contact = new_contact.put()
				else:
					temp_contact = contact_key.get()
					temp_contact.update_contact(data["latitude"],data["longitude"])

			except:
				logging.debug(sys.exc_info())

			try:
				message = mail.EmailMessage(sender="Family Parking <familyparkingapp@gmail.com>",
											subject="Position of car")
				message.body = "Your car is parked here: http://www.google.com/maps/place/" + data["latitude"] + "," + \
							   data["longitude"]
				receiver_mail = data["email"]
				i = 0
				for value in receiver_mail:
					try:
						message.to = "<" + value + ">"
						#message.send()
						i = i + 1;
					except:
						self.error(500)
						error = statusReturn.StatusReturn(3, i);
						self.response.write(error.toJSON())
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