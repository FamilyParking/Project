class FamError:

	def __init__(self, number, index):
		self.number = number
		self.index = index
		
	def toString():
		if self.number == 1:
			return "Can't load json"
		if self.number == 2:
			return "Can't send the position"
		if self.number == 3:
			return "Can't send the " + self.index + "email"
		if self.number == 4:
			return "Can't find the email to send"