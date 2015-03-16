package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.widget.Toast;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.ServerCall;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class DoOccupy implements Runnable {

    private MainActivity activity;
    private Car car;
    private User user;

    public DoOccupy(MainActivity activity, User user, Car car) {
        this.activity = activity;
        this.car = car;
        this.user = user;
    }

    @Override
    public void run() {
        Looper.prepare();

        if(Tools.isOnline(activity)) {

            final Result result = ServerCall.occupyCar(user, car);

            SQLiteDatabase db = Tools.getDB_Writable(activity);
            car.setParked(false);
            CarTable.updateCar(db,car);
            db.close();

            if (result.isFlag()) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Tools.createToast(activity, "Car occupied!", Toast.LENGTH_SHORT);
                        activity.unPark();
                        activity.resetTabFragment();
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
