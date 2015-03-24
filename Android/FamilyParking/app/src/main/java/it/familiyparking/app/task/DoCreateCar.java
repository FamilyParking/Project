package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
public class DoCreateCar implements Runnable {

    private MainActivity activity;
    private Car car;
    private User user;

    public DoCreateCar(FragmentActivity activity, Car car, User user) {
        this.activity = (MainActivity)activity;
        this.car = car;
        this.user = user;
    }

    @Override
    public void run() {
        Looper.prepare();

        if(Tools.isOnline(activity)) {

            final Result result = ServerCall.createCar(user, car);

            if (result.isFlag()) {

                SQLiteDatabase db = Tools.getDB_Writable(activity);

                car.setId(((Double) result.getObject()).toString());

                CarTable.insertCar(db, car);

                GroupTable.deleteGroup(db, car.getId());
                for (User contact : car.getUsers())
                    GroupTable.insertContact(db, car.getId(), contact);

                final ArrayList<Car> carArrayList = CarTable.getAllCar(db);
                ((FPApplication) activity.getApplication()).setCars(carArrayList);

                db.close();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.resetLunchWithEmptyList();
                        activity.resetCreateCar();
                        activity.resetProgressDialogCircular(false);
                        activity.setCar();
                        activity.setTabCar();
                        activity.updateCarAdapter(carArrayList);
                        Tools.createToast(activity, "Car created!", Toast.LENGTH_SHORT);
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
