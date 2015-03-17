package it.familiyparking.app.parky;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import it.familiyparking.app.utility.LocationService;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 09/03/15.
 */
public class Notified {

    private String id;
    private double latitude;
    private double longitude;
    private String timestamp;

    public Notified(Context context) {
        this.id = Integer.toString(Tools.getNotificationID());

        double[] position = getPosition(context);
        this.latitude = position[0];
        this.longitude = position[1];

        this.timestamp = Tools.getTimestamp();
    }

    public Notified(String id, double latitude, double longitude, String timestamp) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getLatitude() {
        return Double.toString(latitude);
    }

    public String getLongitude() {
        return Double.toString(longitude);
    }

    public double[] getPosition(){
        return new double[]{latitude,longitude};
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String[] toArray(){
        return new String[]{this.id,Double.toString(this.latitude),Double.toString(this.longitude),this.timestamp};
    }

    private double[] getPosition(Context context){
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

    private double[] getLocationGPS(LocationService locationService){
        Location gpsLocation = locationService.getLocation(LocationManager.GPS_PROVIDER);

        if (gpsLocation != null) {
            double[] position = {gpsLocation.getLatitude(),gpsLocation.getLongitude()};

            return position;
        }

        return null;
    }

    private double[] getLocationNetwork(LocationService locationService){
        Location nwLocation = locationService.getLocation(LocationManager.NETWORK_PROVIDER);

        if (nwLocation != null) {
            double[] position = {nwLocation.getLatitude(),nwLocation.getLongitude()};

            return position;
        }

        return null;
    }

    @Override
    public String toString() {
        return "Notified{" +
                "id='" + id + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
