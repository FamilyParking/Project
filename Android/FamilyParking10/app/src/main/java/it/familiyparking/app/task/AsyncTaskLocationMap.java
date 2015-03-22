package it.familiyparking.app.task;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import it.familiyparking.app.MainActivity;

/**
 * Created by francesco on 18/12/14.
 */
public class AsyncTaskLocationMap extends AsyncTask<Object,Void,Void> {

    private GoogleMap googleMap;
    private MainActivity activity;
    private ProgressBar progressBar;
    private boolean moveToMyLocation;
    private boolean flag;

    @Override
    protected Void doInBackground(Object... object) {
        googleMap = (GoogleMap)object[0];
        activity = (MainActivity)object[1];
        moveToMyLocation = ((Boolean)object[2]).booleanValue();
        progressBar = (ProgressBar)object[3];

        flag = true;
        int count = 100;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if((progressBar != null) && (!progressBar.isShown()))
                    progressBar.setVisibility(View.VISIBLE);
            }
        });

        while(flag && (count>0)) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateFlag((googleMap.getMyLocation() == null));
                }
            });

            try{
                Thread.sleep(500);

                final int value = ((100 - count) * 10) % 101;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(progressBar != null)
                            progressBar.setProgress(value);
                    }
                });

                count--;
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
            try {
                if(moveToMyLocation)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude()),18.0f));

                activity.setPbutton();

                if(progressBar != null)
                    progressBar.setVisibility(View.GONE);
            }
            catch (Exception e){}

            }
        });
    }

    private void updateFlag(boolean f){
        this.flag = f;
    }
}