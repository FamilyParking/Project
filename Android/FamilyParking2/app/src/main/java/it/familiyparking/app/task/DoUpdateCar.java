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
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.ServerCall;
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

        Result result = null;

        if(!newName.equals(oldCar.getName()) || !newBrand.equals(oldCar.getBrand()) || bluetooth_change){
            oldCar.setName(newName);
            oldCar.setName(newBrand);

            User user = UserTable.getUser(db);
            oldCar.setEmail(user.getEmail());
            oldCar.setCode(user.getCode());

            result = ServerCall.modifyCar(oldCar);

            if(result.isFlag()) {
                CarTable.updateNameCar(db, oldCar.getId(), newName);
                CarTable.updateNameBrand(db, oldCar.getId(), newBrand);

                if (bluetooth_change) {
                    CarTable.updateBluetooth(db, oldCar);
                    Log.e("UpdateBluetooth", oldCar.toString());
                }

                final ArrayList<Car> cars = CarTable.getAllCar(db);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.updateCarAdapter(cars);
                    }
                });
            }
        }

        db.close();

        final Result resultTemp = result;
        activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.resetProgressDialogCircular(false);
                    activity.closeModifyCar();

                    if((resultTemp == null) || resultTemp.isFlag())
                        Tools.createToast(activity, "Car updated!", Toast.LENGTH_SHORT);
                    else
                        Tools.createToast(activity, resultTemp.getDescription(), Toast.LENGTH_SHORT);
        }          });

    }
}
