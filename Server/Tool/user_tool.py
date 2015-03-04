import json
import logging
from Class.statusReturn import StatusReturn
from Cloud_Storage.user_group import User_group

__author__ = 'Nazzareno'

from Cloud_Storage.user import User


class User_tool():
    @staticmethod
    def check_code(email, code):
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

    @staticmethod
    def return_ID_from_email(email):
        temp_key_user = User.static_querySearch_email(email)
        try:
            temp_id_user = temp_key_user.get().key.id()
            return temp_id_user
        except:
            return -1

    @staticmethod
    def check_before_start(method_name, result):
        try:
            logging.debug("Test_before nome del metodo: "+str(method_name))
            data = json.loads(result.request.body)
            user_class = data["User"]
            code = int(user_class["Code"])
            try:
                result_check_code = User_tool.check_code(user_class["Email"], code)

                if result_check_code == -2:
                    result.error(500)
                    error = StatusReturn(2, method_name)
                    result.response.write(error.print_general_error())
                elif result_check_code == -1:
                    result.error(500)
                    error = StatusReturn(3, method_name)
                    result.response.write(error.print_general_error())
                elif result_check_code < 0:
                    result.error(500)
                    error = StatusReturn(5, method_name)
                    result.response.write(error.print_general_error())
                else:
                    return result_check_code

            except:
                result.error(500)
                error = StatusReturn(4, method_name)
                result.response.write(error.print_general_error())
        except:
            result.error(500)
            error = StatusReturn(1, method_name)
            result.response.write(error.print_general_error())

