package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class DoUpdateCar implements Runnable {

    private String newName;
    private String newBrand;
    private boolean bluetooth_change;
    private Car oldCar;
    private MainActivity activity;

    public DoUpdateCar(FragmentActivity activity, String newName, String newBrand, Car oldCar, boolean bluetooth_change) {
        this.newName = newName;
        this.newBrand = newBrand;
        this.oldCar = oldCar;
        this.activity = (MainActivity)activity;
        this.bluetooth_change = bluetooth_change;
    }

    @Override
    public void run() {
        Looper.prepare();

        DataBaseHelper databaseHelper = new DataBaseHelper(activity);
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();

        if(!newName.equals(oldCar.getName()) || !newBrand.equals(oldCar.getBrand()) || bluetooth_change){
            oldCar.setName(newName);
            oldCar.setName(newBrand);

            /***************/
            /* CALL SERVER */
            /***************/
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            CarTable.updateNameCar(db,oldCar.getId(),newName);
            CarTable.updateNameBrand(db,oldCar.getId(),newBrand);

            if(bluetooth_change) {
                CarTable.updateBluetooth(db, oldCar);
                Log.e("UpdateBluetooth",oldCar.toString());
            }

            final ArrayList<Car> cars = CarTable.getAllCar(db);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.updateCarAdapter(cars);
                }
            });
        }

        db.close();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.resetProgressDialogCircular(false);
                activity.closeModifyCar();
                Tools.createToast(activity, "Car updated!", Toast.LENGTH_SHORT);
            }
        });
    }
}
