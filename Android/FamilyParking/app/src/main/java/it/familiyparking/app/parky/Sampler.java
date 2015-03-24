package it.familiyparking.app.parky;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.BitSet;

import it.familiyparking.app.FPApplication;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.Code;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 14/03/15.
 */
public class Sampler extends IntentService {

    private static int counter = 0;
    private static int currentState = DetectedActivity.STILL;
    private static int interval = 120;
    private static BitSet bitSet = new BitSet(interval);
    private static boolean firstCar = true;
    private static boolean firstFoot = true;

    //DetectedActivity.IN_VEHICLE   -->     0
    //DetectedActivity.ON_FOOT      -->     2
    //DetectedActivity.STILL        -->     3
    //DetectedActivity.UNKNOWN      -->     4
    //DetectedActivity.TILTING      -->     5
    //DetectedActivity.WALKING      -->     7

    protected void onHandleIntent(Intent intent) {

        disableAPI();

        Bundle bundle = intent.getExtras();
        if (bundle != null) {

            ActivityRecognitionResult activityRecognitionResult = bundle.getParcelable("com.google.android.location.internal.EXTRA_ACTIVITY_RESULT");

            DetectedActivity detectedActivity = activityRecognitionResult.getMostProbableActivity();

            Log.e("Sampler",detectedActivity.toString());

            if ((detectedActivity.getType() == DetectedActivity.ON_FOOT) || (detectedActivity.getType() == DetectedActivity.RUNNING) || (detectedActivity.getType() == DetectedActivity.WALKING) || (detectedActivity.getType() == DetectedActivity.STILL)) {

                if(firstFoot){
                    saveBatteryAPI();
                    firstFoot = false;
                }

                if (bitSet.cardinality() == 10) {
                    currentState = DetectedActivity.WALKING;

                    init();

                    new Thread(new DoParky(this)).start();

                } else if ((currentState == DetectedActivity.IN_VEHICLE) && (detectedActivity.getConfidence() > 60)) {
                    bitSet.set(counter);
                }

            } else if (detectedActivity.getType() == DetectedActivity.IN_VEHICLE) {

                if(firstCar){
                    accuracyAPI();
                    firstCar = false;
                }

                if (bitSet.cardinality() == 10) {
                    currentState = DetectedActivity.IN_VEHICLE;

                    init();
                }
                if ((currentState != DetectedActivity.IN_VEHICLE) && (detectedActivity.getConfidence() > 60)) {
                    bitSet.set(counter);
                }
            }

            if (counter == (interval - 1))
                bitSet = bitSet.get(1, interval);
            else
                counter++;

        }
    }

    public Sampler(){
        super("Sampler");
    }

    private void init(){
        counter = 0;
        firstFoot = true;
        firstCar = true;
        bitSet.clear();
    }

    private void disableAPI(){
        FPApplication application = ((FPApplication)this.getApplicationContext());

        Log.e("Hardware",Boolean.toString(!Tools.isPositionHardwareEnable(this)));
        Log.e("Bluetooth",Boolean.toString(application.allHaveBluetooth()));
        Log.e("Parky",Boolean.toString(!application.getUser().isParky()));
        Log.e("Condiction",Boolean.toString(((!Tools.isPositionHardwareEnable(this)) || application.allHaveBluetooth() || !application.getUser().isParky())));

        if((!Tools.isPositionHardwareEnable(this)) || application.allHaveBluetooth() || !application.getUser().isParky()){

            Log.e("disableAPI","Disable");

            GoogleApiClient googleApiClient = application.getGoogleApiClient();
            if(googleApiClient != null){
                Intent intent = new Intent(this, Sampler.class);
                PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
                ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(googleApiClient,pendingIntent);
            }
        }
        else{
            Log.e("disableAPI","Come on");
        }
    }

    private void saveBatteryAPI(){
        FPApplication application = ((FPApplication)this.getApplicationContext());

        GoogleApiClient googleApiClient = application.getGoogleApiClient();
        if(googleApiClient != null){
            Intent intent = new Intent(this, Sampler.class);

            PendingIntent stopIntent = PendingIntent.getService(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(googleApiClient,stopIntent);

            PendingIntent startIntent = PendingIntent.getService(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(googleApiClient,Code.API_INTERVAL_SAVE,startIntent);
        }
    }

    private void accuracyAPI(){
        FPApplication application = ((FPApplication)this.getApplicationContext());

        GoogleApiClient googleApiClient = application.getGoogleApiClient();
        if(googleApiClient != null){
            Intent intent = new Intent(this, Sampler.class);

            PendingIntent stopIntent = PendingIntent.getService(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(googleApiClient,stopIntent);

            PendingIntent startIntent = PendingIntent.getService(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(googleApiClient,Code.API_INTERVAL_ACCURATE,startIntent);
        }
    }
}
