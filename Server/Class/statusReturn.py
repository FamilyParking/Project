
class StatusReturn:
	number = ""
	index = ""

	def __init__(self,numb,ind):
		self.number=numb
		self.index=ind

	def toJSON(self):
		error_data = {}
		if self.number == 1:
			error_data["flag"] = False
			error_data["description"] = "Can't load json"
			error_data["object"] = None
		if self.number == 2:
			error_data["flag"] = False
			error_data["description"] = "Can't send the position"
			error_data["object"] = None
		if self.number == 3:
			error_data["flag"] = False
			error_data["description"] = "Can't send the " + self.index + "email"
			error_data["object"] = None
		if self.number == 4:
			error_data["flag"] = False
			error_data["description"] = "Can't update the position"
			error_data["object"] = None
		else:
			error_data["flag"] = True
			error_data["description"] = "Email sent"
			error_data["object"] = None

		return error_data
