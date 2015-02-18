package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import it.familiyparking.app.MainActivity;
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
public class DoPark implements Runnable {

    private MainActivity activity;
    private Car car;
    private boolean flag;

    public DoPark(MainActivity activity, Car car) {
        this.activity = activity;
        this.car = car;
        this.flag = true;
    }

    @Override
    public void run() {
        Looper.prepare();

        DataBaseHelper databaseHelper = new DataBaseHelper(activity.getApplicationContext());
        final SQLiteDatabase db = databaseHelper.getReadableDatabase();

        User user = UserTable.getUser(db);
        car.setEmail(user.getEmail());
        car.setCode(user.getCode());

        db.close();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                car.setLatitude(activity.getLatitude());
                car.setLongitude(activity.getLongitude());
                updateFlag(false);
            }
        });

        while(flag);

        final Result result = ServerCall.updatePosition(car);

        if(result.isFlag()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.createToast(activity,"Car parked!", Toast.LENGTH_SHORT);
                    activity.park(car);
                    activity.resetProgressDialogCircular(true);
                }
            });
        }
        else{
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.createToast(activity,result.getDescription(), Toast.LENGTH_SHORT);
                    activity.resetProgressDialogCircular(true);
                }
            });
        }
    }

    private void updateFlag(boolean f){
        this.flag = f;
    }
}
