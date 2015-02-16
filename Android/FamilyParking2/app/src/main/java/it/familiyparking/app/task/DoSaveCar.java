package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.serverClass.Car;

/**
 * Created by francesco on 02/01/15.
 */
public class DoSaveCar implements Runnable {

    private MainActivity activity;
    private Car car;

    public DoSaveCar(FragmentActivity activity, Car car) {
        this.activity = (MainActivity)activity;
        this.car = car;
    }

    @Override
    public void run() {
        Looper.prepare();

        DataBaseHelper databaseHelper = new DataBaseHelper(activity);
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();

        /***************/
        /* CALL SERVER */
        /***************/
        /**************************************/
        /**/String carID = "0123456789";    /**/
        /**/String timestamp = "0123456789";/**/
        /**************************************/
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        car.setId(carID);

        CarTable.insertCar(db,car,timestamp);

        db.close();

        activity.resetProgressDialogCircular(false);
        activity.closeCreateCar();
    }
}
