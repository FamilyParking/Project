import logging

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
    def check_user_exist(id_user, id_group):
        temp_user = User_group.getGroupFromUser(id_user)
        if temp_user.count() == 0:
            return 1
        else:
            for group in temp_user:
                if group.id_group == id_group:
                    return -1
        return 1

    @staticmethod
    def delete_contact_group(id_user, id_group):
        delete_entry = User_group.getGroupFromUser(id_user)
        for group in delete_entry:
            #logging.debug("ID del gruppo da controllare: "+str(group.id_group)+" id del gruppo da eliminare: "+str(id_group))
            if long(group.id_group) == long(id_group):
                #logging.debug("Gruppo da eliminare: "+str(group))
                group.key.delete()
        return 0;
