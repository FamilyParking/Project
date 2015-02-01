from Cloud_Storage.user_group import User_group

__author__ = 'Nazzareno'


from Cloud_Storage.user import User

class User_tool():

    @staticmethod
    def check_code(email,code):
        temp_userKey = User.static_querySearch_email(email)
        if temp_userKey.count() == 0:
            return -2
        else:
            temp_user = temp_userKey.get()
            return temp_user.check_code(code)


    @staticmethod
    def return_groups(email):
        temp_key_user = User.static_querySearch_email(email)
        try:
            temp_id_user = temp_key_user.get().key.id()
            id_groups = User_group.getGroupFromUser(temp_id_user)
            return id_groups
        except:
            return -1