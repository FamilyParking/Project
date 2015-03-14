__author__ = 'Nazzareno'


class static_variable():

    # True = enable the print debug
    DEBUG = True

    # Key to use the API of google
    google_api_key = "AIzaSyAN2KZpzIpWmPQidczGiyo3ZlV4j1ERe2U"

    # Email from send the notification and code
    sender = "Family Parking <familyparkingapp@gmail.com>"

    # Number of value stored from with we analyzed the data of get notification
    min_value = 10

    # Percentage over that we send notification
    percentage_not = 0.3

    # Radius of value that we consider when we analyzed all stored value
    radius = 1.0      # 1 kilometre
