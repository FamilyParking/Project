package it.familiyparking.app.utility;

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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.User;

/**
 * Created by mauropiva on 13/02/15.
 */
public class ServiceBluetooth extends Service{

    private MainActivity activity;

    public ServiceBluetooth(){}

    @Override
    public void onCreate() {
        super.onCreate();
        onHandleIntent();
    }

    protected void onHandleIntent(){
        this.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
        this.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString("name", device.getName());
                edit.putString("address", device.getAddress());
                edit.commit();

                Log.e("Bluetooth","Connected");

                //Log.e("Preferences",preferences.getAll().toString());
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit = preferences.edit();
                edit.clear();
                edit.commit();

                Log.e("Bluetooth","Disconnected");

                DataBaseHelper databaseHelper = new DataBaseHelper(getApplicationContext());
                final SQLiteDatabase db = databaseHelper.getReadableDatabase();

                if(!UserTable.getGhostMode(db)) {

                    ArrayList<Car> carID = CarTable.getAllCarForBluetoothMAC(db, device.getAddress());

                    LocationService locationService = new LocationService(getApplicationContext());
                    double[] position = Tools.getPoistion(locationService);

                    User user = UserTable.getUser(db);

                    for (final Car car : carID) {

                        car.setLatitude(Double.toString(position[0]));
                        car.setLongitude(Double.toString(position[1]));
                        car.setCode(user.getCode());
                        car.setEmail(user.getEmail());

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ServerCall.updatePosition(car);
                                Intent intent = new Intent(Code.INTENT_TAG);
                                intent.putExtra("car", car);
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                            }
                        }).start();


                    }
                }

                db.close();

                //Log.e("Preferences",preferences.getAll().toString());
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