package it.familiyparking.app.parky;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.widget.Toast;

import it.familiyparking.app.R;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.NotifiedTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.Code;
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

        try {

            boolean done = false;

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.closeDialog();
                    activity.setProgressDialogCircular(activity.getResources().getString(R.string.park_car));
                }
            });

            for(int i=0; i< Code.TRIALS; i++) {

                if (Tools.isOnline(context)) {

                    SQLiteDatabase db = Tools.getDB_Writable(context);
                    Notified notified = NotifiedTable.getNotified_ByID(db, notification_ID);

                    car.setLatitude(notified.getLatitude());
                    car.setLongitude(notified.getLongitude());
                    car.setTimestamp(notified.getTimestamp());

                    car.setLast_driver(user.getEmail());

                    final Result result = ServerCall.parkCar(user, car);

                    if (result.isFlag()) {
                        NotifiedTable.deleteNotified(db, notification_ID);

                        car.setParked(true);
                        CarTable.updateCar(db, car);
                        db.close();

                        if (activity != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    activity.updateStatistic();
                                    activity.resetProgressDialogCircular();
                                    Tools.createToast(activity, activity.getResources().getString(R.string.parked_car), Toast.LENGTH_SHORT);
                                }
                            });
                        }

                        done = true;
                        break;

                    } else {
                        db.close();

                        if (activity != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    activity.resetProgressDialogCircular();
                                    Tools.createToast(activity, activity.getResources().getString(R.string.no_server), Toast.LENGTH_SHORT);
                                }
                            });
                        }

                        done = true;
                        break;
                    }

                } else {
                    Thread.sleep(100);
                }

            }

            if(!done){
                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.resetProgressDialogCircular();
                            Tools.createToast(activity, activity.getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT);
                        }
                    });
                }
            }

            Tools.closeServiceStatistic(context);

        }
        catch (Exception e){

        }

    }

}
