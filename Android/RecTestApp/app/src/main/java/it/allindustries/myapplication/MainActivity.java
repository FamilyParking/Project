package it.allindustries.myapplication;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        final Tracker t = analytics.newTracker("UA-58079755-2");

        setUpGoogleApi();
        setUpMap();
        setMarker();

        ArrayList<Sample> samples = SamplesTable.getAllSamples(this);
        for(Sample sample : samples)
            Log.e("Sample",sample.toString());
    }

    private void setUpGoogleApi(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();
    }

    private void setUpMap(){
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            googleMap.setMyLocationEnabled(true);

            final Context context = this;

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker marker) {

                    final Sample sample = SamplesTable.getSample_ByID(context,marker.getSnippet());

                    if((sample != null) && (sample.getCorrect() == -1)) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                        alertDialog.setCancelable(true);

                        alertDialog.setTitle("Is it correct?");

                        alertDialog.setMessage("[" + sample.getTimestamp() + "] \n " + sample.getInfo());

                        alertDialog.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        updateInfo(sample.getId(), true);
                                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                        dialog.cancel();
                                    }
                                });

                        alertDialog.setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        updateInfo(sample.getId(), false);
                                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                        dialog.cancel();
                                    }
                                });

                        alertDialog.show();

                        return true;
                    }

                    return false;
                }
            });

            new AsyncTaskLocationMap().execute(googleMap, this);
        }
    }

    private void setMarker(){
        ArrayList<Sample> samples = SamplesTable.getAllSamplesParked(this);

        for(Sample sample : samples)
            addMarker(sample,this);
    }

    private void addMarker(final Sample sample,final Context context){

        float color = BitmapDescriptorFactory.HUE_YELLOW;

        if(sample.getCorrect() == 0)
            color = BitmapDescriptorFactory.HUE_RED;
        else if(sample.getCorrect() == 1)
            color = BitmapDescriptorFactory.HUE_GREEN;

        googleMap.addMarker(new MarkerOptions()
                .position(sample.getPosition())
                .title(sample.getTimestamp())
                .snippet(sample.getId())
                .icon(BitmapDescriptorFactory.defaultMarker(color)));
    }

    private void updateInfo(String id, boolean flag){
        if(flag)
            SamplesTable.updateSample(this,id,1);
        else
            SamplesTable.updateSample(this,id,0);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Intent intent = new Intent(this, Signal.class);

        PendingIntent pendingIntent = PendingIntent.getService(this, 579, intent,PendingIntent.FLAG_CANCEL_CURRENT);

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(googleApiClient, 1000, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
