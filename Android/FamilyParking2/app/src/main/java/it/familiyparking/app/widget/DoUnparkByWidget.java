package it.familiyparking.app.widget;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.widget.Toast;

import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.ServerCall;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class DoUnparkByWidget implements Runnable {

    private WidgetActivity activity;
    private Car car;
    private User user;

    public DoUnparkByWidget(WidgetActivity activity, User user, Car car) {
        this.activity = activity;
        this.car = car;
        this.user = user;
    }

    @Override
    public void run() {
        Looper.prepare();

        if(Tools.isOnline(activity)) {

            final Result result = ServerCall.occupyCar(user, car);

            if (result.isFlag()) {
                SQLiteDatabase db = Tools.getDB_Writable(activity);
                car.setParked(false);
                CarTable.updateCar(db,car);
                db.close();

                sendMessage("Car occupied!");
            }
            else {
                sendMessage("Server not available!");
            }

        }
        else{
            sendMessage("No connection available!");
        }
    }

    private void sendMessage(final String message){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Tools.createToast(activity, message, Toast.LENGTH_SHORT);
                activity.closeDialog();
                activity.finish();
            }
        });
    }

}
