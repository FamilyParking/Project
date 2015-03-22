package it.familiyparking.app.serverClass;

import com.google.gson.annotations.SerializedName;

/**
 * Created by francesco on 03/03/15.
 */
public class Container {

    @SerializedName("User")
    private User user;

    @SerializedName("Car")
    private Car car;

    public Container(User user) {
        this.user = user;
    }

    public Container(User user, Car car) {
        this.user = user;
        this.car = car;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
