package it.familiyparking.app.parky;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.widget.Toast;

import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.NotifiedTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.ServerCall;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class DoParkByStatisticActivity implements Runnable {

    private StatisticActivity activity;
    private Context context;
    private String notification_ID;
    private Car car;
    private User user;

    public DoParkByStatisticActivity(StatisticActivity activity, Context context, String notification_ID,User user, Car car) {
        this.car = car;
        this.user = user;
        this.context = context;
        this.activity = activity;
        this.notification_ID = notification_ID;
    }

    @Override
    public void run() {
        Looper.prepare();

        if(Tools.isOnline(context)) {

            SQLiteDatabase db = Tools.getDB_Writable(context);
            Notified notified = NotifiedTable.getNotified_ByID(db,notification_ID);

            car.setLatitude(notified.getLatitude());
            car.setLongitude(notified.getLongitude());
            car.setTimestamp(notified.getTimestamp());

            car.setLast_driver(user.getEmail());

            final Result result = ServerCall.parkCar(user, car);

            if (result.isFlag()) {
                NotifiedTable.deleteNotified(db,notification_ID);

                activity.updateStatistic();

                car.setParked(true);
                CarTable.updateCar(db,car);

                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Tools.createToast(activity, "Car parked!", Toast.LENGTH_SHORT);
                        }
                    });
                }
            }
            else {
                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Tools.createToast(activity, "Server not available!", Toast.LENGTH_SHORT);
                        }
                    });
                }
            }

            db.close();

        }
        else{
            if(activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Tools.createToast(activity, "No connection available!", Toast.LENGTH_SHORT);
                    }
                });
            }
        }
    }

}
