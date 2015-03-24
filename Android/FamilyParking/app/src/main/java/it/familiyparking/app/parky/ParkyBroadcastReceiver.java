package it.familiyparking.app.parky;

import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import java.util.ArrayList;

import it.familiyparking.app.FPApplication;
import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.Code;
import it.familiyparking.app.utility.ServerCall;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 23/03/14.
 */
public class ParkyBroadcastReceiver extends BroadcastReceiver implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    private Context globalContext;
    private GoogleApiClient googleApiClient;

    @Override
    public void onReceive(final Context context, Intent intent){

        if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equalsIgnoreCase(Code.CUSTOM_INTENT)){
            globalContext = context;

            if(!disableAPI(context)) {
                FPApplication application = ((FPApplication) context.getApplicationContext());
                googleApiClient = application.getGoogleApiClient();

                if ((googleApiClient == null) || (!googleApiClient.isConnected() && !googleApiClient.isConnecting())) {
                    setGoogleApiClient();
                    application.setGoogleApiClient(googleApiClient);
                }
            }

        }
        else if(intent.getAction().equalsIgnoreCase(BluetoothDevice.ACTION_ACL_CONNECTED)){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            //Log.e("ParkyBroadcastReceiver","Connected");

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString("name", device.getName());
            edit.putString("address", device.getAddress());
            edit.commit();

            //Log.e("ParkyBroadcastReceiver", preferences.getAll().toString());

            unpark(context, device);
        }
        else if(intent.getAction().equalsIgnoreCase(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor edit = preferences.edit();
            edit.clear();
            edit.commit();

            //Log.e("ParkyBroadcastReceiver", "Disconnected");

            park(context,device);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Intent intent = new Intent(globalContext, Sampler.class);

        PendingIntent pendingIntent = PendingIntent.getService(globalContext, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(googleApiClient, Code.API_INTERVAL_SAVE, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("ParkyBroadcastReceiver", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("ParkyBroadcastReceiver", "onConnectionFailed");
    }

    private void setGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(globalContext)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();
    }

    private boolean disableAPI(Context context){
        FPApplication application = ((FPApplication)context.getApplicationContext());

        if((!Tools.isPositionHardwareEnable(context)) || application.allHaveBluetooth() || !application.getUser().isParky()){

            GoogleApiClient googleApiClient = application.getGoogleApiClient();
            if(googleApiClient != null){
                Intent intent = new Intent(context, Sampler.class);
                PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
                ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(googleApiClient,pendingIntent);
            }

            return true;
        }

        return false;
    }

    private void park(final Context context, BluetoothDevice device){
        SQLiteDatabase db = Tools.getDB_Readable(context);
        final User user = UserTable.getUser(db);

        if (user != null) {
            ArrayList<Car> carID = CarTable.getAllCarForBluetoothMAC(db, device.getAddress());
            db.close();

            if (!user.isGhostmode()) {

                double[] position = Tools.getPosition(context);

                for (final Car car : carID) {

                    car.setPosition(position);
                    car.setTimestamp(Tools.getTimestamp());

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Result result = ServerCall.parkCar(user, car);

                            if (result.isFlag()) {
                                SQLiteDatabase db = Tools.getDB_Writable(context);
                                car.setParked(true);
                                car.setLast_driver(user.getEmail());
                                CarTable.updateCar(db, car);

                                FPApplication application= ((FPApplication)context.getApplicationContext());
                                application.setCars(CarTable.getAllCar(db));

                                db.close();

                                if (Tools.isAppRunning(context)) {
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.putExtra("parked",car.getId());
                                    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                                    try {
                                        contentIntent.send();
                                    } catch (PendingIntent.CanceledException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }
                    }).start();

                }
            }

        }
    }

    private void unpark(final Context context, BluetoothDevice device){
        SQLiteDatabase db = Tools.getDB_Readable(context);
        final User user = UserTable.getUser(db);

        if (user != null) {
            ArrayList<Car> carID = CarTable.getAllCarForBluetoothMAC(db, device.getAddress());
            db.close();

            if (!user.isGhostmode()) {

                for (final Car car : carID) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Result result = ServerCall.occupyCar(user, car);

                            if (result.isFlag()) {
                                SQLiteDatabase db = Tools.getDB_Writable(context);
                                car.setParked(false);
                                CarTable.updateCar(db, car);

                                FPApplication application = ((FPApplication) context.getApplicationContext());
                                application.setCars(CarTable.getAllCar(db));

                                db.close();

                                if (Tools.isAppRunning(context)) {
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.putExtra("unparked", car.getId());
                                    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                                    try {
                                        contentIntent.send();
                                    } catch (PendingIntent.CanceledException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }
                    }).start();

                }
            }

        }
    }
}
