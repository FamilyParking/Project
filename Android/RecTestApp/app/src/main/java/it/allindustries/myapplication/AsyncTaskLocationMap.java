package it.allindustries.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by francesco on 18/12/14.
 */
public class AsyncTaskLocationMap extends AsyncTask<Object,Void,Void> {

    private GoogleMap googleMap;
    private MainActivity activity;
    private boolean flag;

    @Override
    protected Void doInBackground(Object... object) {
        googleMap = (GoogleMap)object[0];
        activity = (MainActivity)object[1];
        flag = true;

        while(flag) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateFlag((googleMap.getMyLocation() == null));
                }
            });

            try{
                Thread.sleep(500);
            }
            catch(InterruptedException e){
                Log.e("AsyncTaskLocationMap",e.getMessage());
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude()), 18.0f));
            }
        });
    }

    private void updateFlag(boolean f){
        this.flag = f;
    }
}