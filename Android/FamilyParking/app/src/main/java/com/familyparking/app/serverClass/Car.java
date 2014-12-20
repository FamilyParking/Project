package com.familyparking.app.serverClass;

/**
 * Created by francesco on 20/12/14.
 */
import com.google.gson.annotations.SerializedName;

/**
 * Created by francesco on 25/03/14.
 */

public class Car{

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("email")
    private String[] email;

    public Car(){}

    public Car(double[] position,String[] email){
        this.latitude = Double.toString(position[0]);
        this.longitude = Double.toString(position[1]);
        this.email = email;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String[] getEmail() {
        return email;
    }
}