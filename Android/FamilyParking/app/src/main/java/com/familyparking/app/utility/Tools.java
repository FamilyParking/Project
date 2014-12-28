package com.familyparking.app.utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import com.familyparking.app.service.LocationService;

/**
 * Created by francesco on 18/12/14.
 */
public class Tools {

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private static double[] getLocationGPS(LocationService locationService,Context context){
        Location gpsLocation = locationService.getLocation(LocationManager.GPS_PROVIDER);

        if (gpsLocation != null) {
            double[] position = {gpsLocation.getLatitude(),gpsLocation.getLongitude()};

            return position;
        }

        return null;
    }

    private static double[] getLocationNetwork(LocationService locationService,Context context){
        Location nwLocation = locationService.getLocation(LocationManager.NETWORK_PROVIDER);

        if (nwLocation != null) {
            double[] position = {nwLocation.getLatitude(),nwLocation.getLongitude()};

            return position;
        }

        return null;
    }

    private static void showSettingsAlert(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("Location services disabled");

        alertDialog.setMessage("FamiliyParking needs to know your position. Active it!");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public static double[] getLocationAlert(LocationService locationService,Context context){
        double[] position;

        position = getLocationGPS(locationService, context);

        if(position == null) {
            position = getLocationNetwork(locationService, context);
            if (position == null) {
                showSettingsAlert(context);
            }
        }

        return position;
    }

    public static double[] getLocation(LocationService locationService,Context context){
        double[] position;

        position = getLocationGPS(locationService, context);

        if(position == null) {
            position = getLocationNetwork(locationService, context);
        }

        return position;
    }
}
