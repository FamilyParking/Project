package it.familiyparking.app.parky;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.ServerCall;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 13/02/15.
 */

public class ServiceBluetooth extends Service{

    public ServiceBluetooth(){}

    @Override
    public void onCreate() {

        Log.e("ServiceBluetooth","Call method to block service if necessary");

        super.onCreate();
        onHandleIntent();
    }

    protected void onHandleIntent(){
        this.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
        this.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            final String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString("name", device.getName());
                edit.putString("address", device.getAddress());
                edit.commit();

                Log.e("Bluetooth","Connected");

                Log.e("Preferences",preferences.getAll().toString());
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit = preferences.edit();
                edit.clear();
                edit.commit();

                Log.e("Bluetooth","Disconnected");

                SQLiteDatabase db = Tools.getDB_Readable(context);
                final User user = UserTable.getUser(db);

                if(user != null) {
                    ArrayList<Car> carID = CarTable.getAllCarForBluetoothMAC(db, device.getAddress());
                    db.close();

                    if (!user.isGhostmode()) {

                        double[] position = Tools.getPosition(getApplicationContext());

                        for (final Car car : carID) {

                            car.setPosition(position);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Result result = ServerCall.parkCar(user, car);

                                    if (result.isFlag()) {
                                        SQLiteDatabase db = Tools.getDB_Writable(context);
                                        car.setParked(true);
                                        car.setLast_driver(user.getEmail());
                                        CarTable.updateCar(db,car);;
                                        db.close();
                                    }

                                }
                            }).start();

                        }
                    }
                }

                Log.e("Preferences",preferences.getAll().toString());
            }
        }
    };

    @Override
    public void onDestroy(){
        unregisterReceiver(mReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        onHandleIntent();
        return null;
    }

    public int onStartCommand(Intent i, int flags, int start){
        onHandleIntent();
        return START_STICKY;
    }

}