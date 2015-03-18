package it.familiyparking.app.parky;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.BitSet;

/**
 * Created by francesco on 14/03/15.
 */
public class Sampler extends IntentService {

    private static int counter = 0;
    private static int currentState = DetectedActivity.STILL;
    private static BitSet bitSet = new BitSet(60);

    //DetectedActivity.IN_VEHICLE   -->     0
    //DetectedActivity.ON_FOOT      -->     2
    //DetectedActivity.STILL        -->     3
    //DetectedActivity.UNKNOWN      -->     4
    //DetectedActivity.TILTING      -->     5
    //DetectedActivity.WALKING      -->     7

    protected void onHandleIntent(Intent intent) {

        Bundle bundle = intent.getExtras();
        if (bundle != null) {

            ActivityRecognitionResult activityRecognitionResult = bundle.getParcelable("com.google.android.location.internal.EXTRA_ACTIVITY_RESULT");

            DetectedActivity detectedActivity = activityRecognitionResult.getMostProbableActivity();

            Log.e("Sampler",detectedActivity.toString());

            if ((detectedActivity.getType() == DetectedActivity.ON_FOOT) || (detectedActivity.getType() == DetectedActivity.RUNNING) || (detectedActivity.getType() == DetectedActivity.WALKING) || (detectedActivity.getType() == DetectedActivity.STILL)) {

                if (bitSet.cardinality() == 10) {
                    currentState = DetectedActivity.WALKING;

                    counter = 0;
                    bitSet.clear();

                    new Thread(new DoParky(this)).start();

                } else if ((currentState == DetectedActivity.IN_VEHICLE) && (detectedActivity.getConfidence() > 60)) {
                    bitSet.set(counter);
                }

            } else if (detectedActivity.getType() == DetectedActivity.IN_VEHICLE) {
                if (bitSet.cardinality() == 10) {
                    currentState = DetectedActivity.IN_VEHICLE;

                    counter = 0;
                    bitSet.clear();
                }
                if ((currentState != DetectedActivity.IN_VEHICLE) && (detectedActivity.getConfidence() > 60)) {
                    bitSet.set(counter);
                }
            }

            if (counter == 59)
                bitSet = bitSet.get(1, 60);
            else
                counter++;

        }
    }

    public Sampler(){
        super("Sampler");
    }
}
