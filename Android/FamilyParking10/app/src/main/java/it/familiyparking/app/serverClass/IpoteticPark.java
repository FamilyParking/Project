package it.familiyparking.app.serverClass;

import com.google.gson.annotations.SerializedName;

/**
 * Created by francesco on 03/03/15.
 */
public class IpoteticPark {

    @SerializedName("User")
    private User user;

    @SerializedName("Latitude")
    private String latitude;

    @SerializedName("Longitude")
    private String longitude;

    @SerializedName("Timestamp")
    private String timestamp;

    public IpoteticPark(User user, double[] position, String timestamp) {
        this.user = user;
        this.latitude = Double.toString(position[0]);
        this.longitude = Double.toString(position[1]);
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
