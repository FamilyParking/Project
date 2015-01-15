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

    @SerializedName("email")
    private String[] email;

    @SerializedName("ID")
    private String id;

    public Car(){}

    public Car(double[] position,String[] email,String id){
        this.latitude = Double.toString(position[0]);
        this.longitude = Double.toString(position[1]);
        this.email = email;
        this.id = id;
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

    public String getId() {
        return id;
    }
}