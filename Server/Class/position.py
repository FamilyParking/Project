__author__ = 'Nazzareno'

class Position():
    latitude = ""
    longitude = ""

    def __init__(self,lat,long):
        self.longitude = long
        self.latitude = lat

    def __str__(self):
     return "Latitude = "+self.latitude+" Longitude = "+self.longitude

    def getLatitude(self):
        return self.latitude
    def getLongitude(self):
        return self.longitude
