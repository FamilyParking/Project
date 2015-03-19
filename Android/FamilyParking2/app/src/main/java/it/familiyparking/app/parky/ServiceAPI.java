package it.familiyparking.app.parky;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import it.familiyparking.app.utility.Code;

/**
 * Created by francesco on 13/02/15.
 */

public class ServiceAPI extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient googleApiClient;

    public ServiceAPI(){}

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("ServiceAPI","Call method to block service if necessary");

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        final Tracker t = analytics.newTracker("UA-58079755-2");

        setGoogleApiClient();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent i, int flags, int start){
        return super.onStartCommand(i,flags,start);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Intent intent = new Intent(this, Sampler.class);

        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(googleApiClient, Code.API_INTERVAL, pendingIntent);
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