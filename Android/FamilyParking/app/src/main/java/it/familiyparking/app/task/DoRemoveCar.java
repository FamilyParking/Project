package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import java.util.ArrayList;

import it.familiyparking.app.FPApplication;
import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.GroupTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.ServerCall;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class DoRemoveCar implements Runnable {

    private MainActivity activity;
    private Car car;
    private User user;

    public DoRemoveCar(FragmentActivity activity, Car car, User user) {
        this.activity = (MainActivity)activity;
        this.car = car;
        this.user = user;
    }

    @Override
    public void run() {
        Looper.prepare();

        if(Tools.isOnline(activity)) {

            final Result result = ServerCall.removeCar(user, car);

            if (result.isFlag()) {

                SQLiteDatabase db = Tools.getDB_Writable(activity);

                CarTable.deleteCar(db, car.getId());

                GroupTable.deleteGroup(db, car.getId());

                final ArrayList<Car> cars = CarTable.getAllCar(db);

                ((FPApplication) activity.getApplication()).setCars(cars);

                db.close();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.resetProgressDialogCircular(false);
                        activity.resetModifyCar(true);
                        activity.updateCarAdapter(cars);
                        Tools.createToast(activity, "Car deleted!", Toast.LENGTH_SHORT);
                    }
                });
            } else {
                Tools.manageServerError(result, activity);
            }
        }
        else{
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.endNoConnection();
                }
            });
        }
    }
}