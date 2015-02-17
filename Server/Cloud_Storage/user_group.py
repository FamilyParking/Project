__author__ = 'Nazzareno'


from google.appengine.ext import ndb

class User_group(ndb.Model):
    id_user = ndb.IntegerProperty()
    id_group = ndb.IntegerProperty()

    @staticmethod
    def getUserFromGroup(id_group):
        result_group = User_group.query(User_group.id_group == id_group)
        return result_group

    @staticmethod
    def getGroupFromUser(id_user):
        result_user = User_group.query(User_group.id_user == id_user)
        return result_user

    @staticmethod
    def check_user_exist(id_user):
        temp_user = User_group.getGroupFromUser(id_user)
        if temp_user.count() == 0:
            return 1
        else:
            return -1

    @staticmethod
    def delete_contact_group(id_user):
        delete_entry = User_group.getGroupFromUser(id_user)
        delete_entry.get().key.delete()
        return 0;
