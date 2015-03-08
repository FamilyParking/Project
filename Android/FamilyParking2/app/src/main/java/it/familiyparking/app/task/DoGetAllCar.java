package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;

import java.util.ArrayList;

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
public class DoGetAllCar implements Runnable {

    private User user;
    private MainActivity activity;

    public DoGetAllCar(MainActivity activity, User user) {
        this.user = user;
        this.activity = activity;
    }

    @Override
    public void run() {
        Looper.prepare();

        if(Tools.isOnline(activity)) {

            final Result result = ServerCall.getAllCar(user);

            if (result.isFlag()) {
                SQLiteDatabase db = Tools.getDB_Writable(activity);

                CarTable.deleteCarTable(db);
                GroupTable.deleteGroupTable(db);

                final ArrayList<Car> cars = (ArrayList<Car>) result.getObject();
                for (final Car c : cars) {

                    for (User contact : c.getUsers()) {
                        GroupTable.insertContact(db,c.getId(),contact);
                    }

                    if ((!c.getLatitude().equals("0")) || (!c.getLongitude().equals("0"))) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activity.park(c, false);
                            }
                        });
                    }
                }

                activity.resetAllCarRunning();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.updateCarAdapter(cars);
                    }
                });

            } else {
                activity.resetAllCarRunning();
                Tools.manageServerError(result, activity);
            }

        }
    }
}
