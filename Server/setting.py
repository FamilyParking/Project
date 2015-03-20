__author__ = 'Nazzareno'


class static_variable():

    # True = enable the print debug
    DEBUG = True

    # Key to use the API of google
    google_api_key = "AIzaSyAN2KZpzIpWmPQidczGiyo3ZlV4j1ERe2U"

    # Email from send the notification and code
    sender = "Family Parking <familyparkingapp@gmail.com>"

    # Number of value stored from with we analyzed the data of get notification
    min_value = 7

    # Percentage over that we send notification
    percentage_not = 0.3

    # Radius of value that we consider when we analyzed all stored value
    radius = 1.0      # 1 kilometre

    # Range of month that we consider to return the weight of history
    range_month = 3

    # Radius of google place check
    google_radius = 200

    # Type of google place
    google_types = "subway_station|train_station|parking"