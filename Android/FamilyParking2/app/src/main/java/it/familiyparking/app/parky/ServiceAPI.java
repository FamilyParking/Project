package it.familiyparking.app.parky;

import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import java.util.ArrayList;

import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.User;

/**
 * Created by francesco on 13/02/15.
 */

public class ServiceAPI extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient googleApiClient;

    public ServiceAPI(){}

    @Override
    public void onCreate() {
        super.onCreate();

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        final Tracker t = analytics.newTracker("UA-58079755-2");

        setGoogleApiClient();

        Log.e("ServiceAPI","onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("ServiceAPI","onBind");
        return null;
    }

    public int onStartCommand(Intent i, int flags, int start){
        Log.e("ServiceAPI","onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Intent intent = new Intent(this, Sampler.class);

        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(googleApiClient, 500, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*NEED TEST*/
        //setGoogleApiClient();
    }

    private void setGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();
    }
}