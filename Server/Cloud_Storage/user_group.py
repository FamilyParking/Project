__author__ = 'Nazzareno'


from google.appengine.ext import ndb

class User_group(ndb.Model):
    id_user = ndb.IntegerProperty()
    id_group = ndb.IntegerProperty()

    def getGroupFromUser(id_group):
        result_group = User_group.query(User_group.id_group == id_group)
        return result_group

    def getUserFromGroup(id_user):
        result_user = User_group.query(User_group.id_user == id_user)
        return result_user