package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
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

            activity.setAllCarRunning();

            final Result result = ServerCall.getAllCar(user);

            if (result.isFlag()) {
                SQLiteDatabase db = Tools.getDB_Writable(activity);

                CarTable.deleteCarTable(db);
                GroupTable.deleteGroupTable(db);

                final ArrayList<Car> cars = (ArrayList<Car>) result.getObject();
                for (final Car c : cars) {

                    CarTable.insertCar(db,c);

                    for (User contact : c.getUsers()) {

                        String photo_ID = Tools.getPhotoID_byEmail(activity,contact.getEmail());
                        if(photo_ID != null) {
                            contact.setPhoto_ID(photo_ID);
                            contact.setHas_photo(true);
                        }

                        GroupTable.insertContact(db,c.getId(),contact);
                    }
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.park(cars);
                        activity.updateCarAdapter(cars);
                    }
                });

            }
            else {
                Tools.manageServerError(result, activity);
            }

            activity.resetAllCarRunning();

            if(activity.getLunchWithEmptyList()) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.resetProgressDialogCircular(true);
                    }
                });
            }

        }
    }
}
