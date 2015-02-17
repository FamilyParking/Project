package it.familiyparking.app.task;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.fragment.ManageCar;
import it.familiyparking.app.fragment.ManageGroup;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.ServerCall;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class DoBluetoothJoin implements Runnable {

    private MainActivity activity;
    private Car car;
    private Button button;
    private ManageCar fragment;

    public DoBluetoothJoin(Activity activity, Car car, Button button, ManageCar fragment) {
        this.activity = (MainActivity)activity;
        this.car = car;
        this.button = button;
        this.fragment = fragment;
    }

    @Override
    public void run() {
        Looper.prepare();

        while(PreferenceManager.getDefaultSharedPreferences(activity).getAll().isEmpty()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Tools.showAlertBluetoothJoin(activity,fragment,preferences.getString("name","name"),preferences.getString("address","address"),car,button);
                activity.resetProgressDialogCircular(false);
            }
        });

    }
}
