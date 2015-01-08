__author__ = 'Nazzareno'

class Position():
    latitude = ""
    longitude = ""

    def __init__(self,lat,long):
        self.longitude = long
        self.latitude = lat

    def getLatitude(self):
        return self.latitude
    def getLongitude(self):
        return self.longitude