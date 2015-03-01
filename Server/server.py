import logging
import json
import sys
import datetime
import webapp2
import random

from Class.statusReturn import StatusReturn

from Cloud_Storage.car_group import Car_group
from Cloud_Storage.user import User
from Cloud_Storage.car import Car
from Cloud_Storage.group import Group
from Cloud_Storage.user_copy import User_copy
from Cloud_Storage.user_group import User_group
from Cloud_Storage.user_car import User_car

from google.appengine.api import mail
from Tool.push_notification import push_class

from Tool.send_email import Send_email
from Tool.user_tool import User_tool


class MainPage(webapp2.RequestHandler):
	def get(self):
		in_file = open("website/index.html", "r")
		MAIN_PAGE_HTML = in_file.read()
		self.response.write(MAIN_PAGE_HTML)
		in_file.close()

class HowToUsePage(webapp2.RequestHandler):
	def get(self):
		in_file = open("website/howtouse.html", "r")
		MAIN_PAGE_HTML = in_file.read()
		self.response.write(MAIN_PAGE_HTML)
		in_file.close()

class SendEmail(webapp2.RequestHandler):
	def post(self):
		logging.debug("Received from user: " + str(self.request.body))
		try:
			data = json.loads(self.request.body)
			new_contact = User_copy(id_android=data["ID"], counter=1, latitude=data["latitude"],
									longitude=data["longitude"], last_time=str(datetime.datetime.now()))
			new_car = Car(brand="testBrand", latitude=data["latitude"],
						  longitude=data["longitude"], timestamp=str(datetime.datetime.now()))
			try:
				contact_key = new_contact.querySearch()
			except:
				logging.debug(sys.exc_info())

			try:
				if contact_key.count() == 0:
					add_db_contact = new_contact.put()
				else:
					temp_contact = contact_key.get()

					if bool(temp_contact.update_contact(data["latitude"], data["longitude"])):
						try:
							message = mail.EmailMessage(sender="Family Parking <familyparkingapp@gmail.com>",
														subject="Position of car")
							message.body = "Your car is parked here: http://www.google.com/maps/place/" + data[
								"latitude"] + "," + \
										   data["longitude"]
							receiver_mail = data["email"]
							i = 0
							for value in receiver_mail:
								try:
									message.to = "<" + value + ">"
									message.send()
									i += 1
								except:
									self.error(500)
									error = StatusReturn(3, i)
									self.response.write(error.toJSON())
							right = StatusReturn(0, 0)
							self.response.write(right.toJSON())
						except:
							self.error(500)
							error = StatusReturn(2, 0)
							self.response.write(error.toJSON())
					else:
						self.error(500)
						error = StatusReturn(4, 0)
						self.response.write(error.toJSON())
				new_car.put()
			except:
				logging.debug(sys.exc_info())

		except:
			self.error(500)
			error = StatusReturn(1, 0)
			self.response.write(error.toJSON())
			
class registrationForm(webapp2.RequestHandler):
	def post(self):
		try:
			data = json.loads(self.request.body)

			try:
				code = random.randint(100000, 999999)
				new_user = User(id_android=data["ID"], code=code, temp_code=code, email=data["Email"],
								nickname=data["Nickname"], is_user=1)

				try:
					contact_key = new_user.querySearch_email()
					if contact_key.count() == 0:
						new_user.put()
					else:
						temp_user = contact_key.get()
						temp_user.update_contact(code)
					try:
						Send_email.send_code(code, data["Email"])

						right = StatusReturn(0, 0)
						self.response.write(right.error_registration())

					except:
						self.error(500)
						error = StatusReturn(3, 0)
						self.response.write(error.error_registration())
				except:
					self.error(500)
					error = StatusReturn(4, 0)
					self.response.write(error.error_registration())
			except:
				self.error(500)
				error = StatusReturn(2, 0)
				self.response.write(error.error_registration())

		except:
			self.error(500)
			error = StatusReturn(1, 0)
			self.response.write(error.error_registration())
			
class confirmCode(webapp2.RequestHandler):
	def post(self):
		if User_tool.check_before_start("confirmCode", self) >= 0:
			right = StatusReturn(11, "confirmCode")
			self.response.write(right.print_result())

class createCar(webapp2.RequestHandler):
	def post(self):
		if User_tool.check_before_start("createCar", self) >= 0:
			dati = json.loads(self.request.body)
			car_data = dati["Car"]
			user_data = dati["User"]
			if "Bluetooth_MAC" in car_data:
				new_car = Car(name=car_data["Name"], latitude="0", longitude="0", timestamp=str(datetime.datetime.now()),
							  email=user_data["Email"], bluetooth_MAC=car_data["Bluetooth_MAC"],
							  bluetooth_name=car_data["Bluetooth_name"], brand=car_data["Brand"])
			else:
				new_car = Car(name=car_data["Name"], latitude="0", longitude="0", timestamp=str(datetime.datetime.now()),
							  email=car_data["Email"], brand=car_data["Brand"])

			new_car.put()
			list_user = car_data["Users"]
			searchuser = User.static_querySearch_email(user_data["Email"])
			for user in searchuser:
				new_contact_car = User_car(id_user=user.key.id(), id_car=int(new_car.key.id()))
				new_contact_car.put()
			for user in list_user:
				user_key = User.is_user_check(user)
				if user_key == 0:
					new_user = User(id_android=None, code=0, temp_code=0, email=user, nickname=None, is_user=0)
					temp_user_key = new_user.put()
					user_key = temp_user_key.id()

				if User_car.check_user_exist(user_key, int(new_car.key.id())) > 0:
					new_contact_car = User_car(id_user=user_key.key.id(), id_car=int(new_car.key.id()))
					new_contact_car.put()

			right = StatusReturn(4, "createCar", new_car.key.id())
			self.response.write(right.print_result())
			
class getCars(webapp2.RequestHandler):
	def post(self):
		if User_tool.check_before_start("getCars", self) >= 0:
			dati = json.loads(self.request.body)
			user_data = dati["User"]
			tempUser = User.static_querySearch_email(user_data["Email"])
			for id_user in tempUser:
				cars = User_car.getCarFromUser(id_user.key.id())
			if (cars.count() == 0): 
				allcars = []
				right = StatusReturn(3, "getAllCars_fromEmail", allcars)
				self.response.write(right.print_result())
			else:
				allcars = []
				for id_carTemp in cars:
					carTemp = Car.getCarbyID(id_carTemp.id_car)
					allcars.append(carTemp.toString_JSON())
				right = StatusReturn(3, "getCars", allcars)
				self.response.write(right.print_result())
				
class deleteCar(webapp2.RequestHandler):
	def post(self):
		if User_tool.check_before_start("deleteCar", self) >= 0:
			dati = json.loads(self.request.body)
			user_data = dati["User"]
			car_data = dati["Car"]
			tempUser = User.static_querySearch_email(user_data["Email"])
			for user in tempUser:
				id_user = user.key.id()
			User_car.deleteCarUser(id_user, car_data["ID_car"])
			right = StatusReturn(7,"deleteCar")
			self.response.write(right.print_result())
			
class updatePosition(webapp2.RequestHandler):
	def post(self):
		if User_tool.check_before_start("updatePosition", self) >= 0:
			dati = json.loads(self.request.body)
			user_data = dati["User"]
			car_data = dati["Car"]
			Car.update_position_ID(car_data["ID_car"], car_data["latitude"], car_data["longitude"])
			list_user = User_car.getUserFromCar(car_data["ID_car"])
			for user in list_user:
				temp_user = User.get_user_by_id(user.id_user)
				if temp_user.is_user == 0:
					Send_email.send_position(temp_user.email, latitude, longitude)
			right = StatusReturn(5, "updatePosition")
			self.response.write(right.print_result())
			
class insertContactCar(webapp2.RequestHandler):
	def post(self):
		if User_tool.check_before_start("insertContactCar", self) >= 0:
			dati = json.loads(self.request.body)
			user_data = dati["User"]
			car_data = dati["Car"]
			for user in car_data["Users"]:
				user_key = User.is_user_check(user)
				if user_key == 0:
					new_user = User(id_android=None, code=0, temp_code=0, email=user, nickname=None, is_user=0)
					temp_user_key = new_user.put()
					user_key = temp_user_key.id()
				if User_car.check_user_exist(user_key, int(car_data["ID_car"])) > 0:
					new_contact_car = User_car(id_user=user_key, id_car=int(car_data["ID_car"]))
					new_contact_car.put()
			right = StatusReturn(10, "insertContactCar")
			self.response.write(right.print_result())

class removeContactCar(webapp2.RequestHandler):
	def post(self):
		if User_tool.check_before_start("removeContactCar", self) >= 0:
			dati = json.loads(self.request.body)
			user_data = dati["User"]
			car_data = dati["Car"]
			for user in car_data["Users"]:
				tempUser = User.static_querySearch_email(user)
				for id_user in tempUser:
					user_key = id_user.key.id()
				User_car.deleteCarUser(user_key, car_data["ID_car"])
			right = StatusReturn(18, "removeContactCar")
			self.response.write(right.print_result())


			



application = webapp2.WSGIApplication([
										  ('/', MainPage),
										  ('/howtouse', HowToUsePage),
										  ('/sign', SendEmail),
										  ('/registration', registrationForm),
										  ('/createCar', createCar),
										  ('/confirmCode', confirmCode),
										  ('/getAllCars', getCars),
										  ('/deleteCar', deleteCar),
										  ('/updatePosition',updatePosition),
										  ('/insertContactCar', insertContactCar),
										  ('/removeContactCar', removeContactCar)
									   ], debug=True)
		