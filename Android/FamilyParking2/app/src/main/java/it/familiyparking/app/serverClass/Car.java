package it.familiyparking.app.serverClass;

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

    @SerializedName("ID")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("brand")
    private String brand;

    public Car(){}

    public Car(String latitude, String longitude, String id, String name, String brand) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
        this.name = name;
        this.brand = brand;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }
}