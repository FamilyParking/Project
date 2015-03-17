package it.familiyparking.app.task;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.fragment.EditCar;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class DoBluetoothJoin implements Runnable {

    private MainActivity activity;
    private Car car;
    private Button button;
    private EditCar fragment;

    public DoBluetoothJoin(Activity activity, Car car, Button button, EditCar fragment) {
        this.activity = (MainActivity)activity;
        this.car = car;
        this.button = button;
        this.fragment = fragment;
    }

    @Override
    public void run() {
        Looper.prepare();

        boolean bluetooth_found = false;

        for(int i=0; i<30; i++) {

            while (PreferenceManager.getDefaultSharedPreferences(activity).getAll().isEmpty()) {
                try {
                    Thread.sleep(100);
                    Log.e("Preferences", "Sleep");
                    Log.e("Preferences", "Preferences" + PreferenceManager.getDefaultSharedPreferences(activity).getAll().toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
            Log.e("DoBluetoothJoin", "Preference :" + preferences.getAll().toString());

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.showAlertBluetoothJoin(activity, fragment, preferences.getString("name", "name"), preferences.getString("address", "address"), car, button);
                    activity.resetProgressDialogCircular(false);
                }
            });

            bluetooth_found = true;
            break;
        }

        if(!bluetooth_found){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.createToast(activity,"Device not detected! Try again!", Toast.LENGTH_LONG);
                    activity.resetProgressDialogCircular(false);
                }
            });
        }

    }
}
