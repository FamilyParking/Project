
class StatusReturn:
	number = ""
	index = ""

	def __init__(self,numb,ind):
		self.number=numb
		self.index=ind

	def toJSON(self):
		error_data = {}
		if self.number == 1:
			error_data["flag"] = 0
			error_data["description"] = "Can't load json"
			error_data["object"] = ""
		if self.number == 2:
			error_data["flag"] = 0
			error_data["description"] = "Can't send the position"
			error_data["object"] = ""
		if self.number == 3:
			error_data["flag"] = 0
			error_data["description"] = "Can't send the " + self.index + "email"
			error_data["object"] = ""
		else:
			error_data["flag"] = 1
			error_data["description"] = "Email sent"
			error_data["object"] = ""

		return error_data
