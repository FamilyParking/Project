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
public class DoFixPosition implements Runnable {

    private MainActivity activity;
    private Car car;
    private User user;

    public DoFixPosition(MainActivity activity, User user, Car car) {
        this.activity = activity;
        this.car = car;
        this.user = user;
    }

    @Override
    public void run() {
        Looper.prepare();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.setProgressDialogCircular("Updating position ...");
            }
        });

        if(Tools.isOnline(activity)) {

            final Result result = ServerCall.parkCar(user, car);

            if (result.isFlag()) {

                SQLiteDatabase db = Tools.getDB_Writable(activity);
                CarTable.updateCar(db,car);
                db.close();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Tools.createToast(activity, "Car updated!", Toast.LENGTH_SHORT);
                        activity.park(car, true);
                        activity.updateCarDetailPosition();
                        activity.resetProgressDialogCircular(true);
                        activity.resetFixPosition();
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
