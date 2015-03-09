package it.allindustries.myapplication;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Timestamp;

/**
 * Created by francesco on 09/03/15.
 */
public class Sample {
    private String id;
    private double latitude;
    private double longitude;
    private String info;
    private int correct;
    private String timestamp;

    public Sample(Context context,String info) {
        double[] position = getPosition(context);
        this.latitude = position[0];
        this.longitude = position[1];
        this.info = info;
        this.correct = -1;
        this.setTimestamp();
    }

    public Sample(String id, double latitude, double longitude, String info, int correct, String timestamp) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.info = info;
        this.correct = correct;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public int getCorrect() {
        return correct;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public LatLng getPosition(){
        return new LatLng(latitude,longitude);
    }

    public void setTimestamp() {
        this.timestamp = (new Timestamp((new java.util.Date()).getTime())).toString();
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String[] getArray(){
        return new String[]{Double.toString(this.latitude),Double.toString(this.longitude),this.info,Integer.toString(this.correct),this.timestamp};
    }

    private static double[] getPosition(Context context){
        LocationService locationService = new LocationService(context);

        double[] position = null;

        while (position == null) {
            position = getLocationGPS(locationService);

            if (position == null) {
                position = getLocationNetwork(locationService);
            }
        }

        return position;
    }

    private static double[] getLocationGPS(LocationService locationService){
        Location gpsLocation = locationService.getLocation(LocationManager.GPS_PROVIDER);

        if (gpsLocation != null) {
            double[] position = {gpsLocation.getLatitude(),gpsLocation.getLongitude()};

            return position;
        }

        return null;
    }

    private static double[] getLocationNetwork(LocationService locationService){
        Location nwLocation = locationService.getLocation(LocationManager.NETWORK_PROVIDER);

        if (nwLocation != null) {
            double[] position = {nwLocation.getLatitude(),nwLocation.getLongitude()};

            return position;
        }

        return null;
    }
}
