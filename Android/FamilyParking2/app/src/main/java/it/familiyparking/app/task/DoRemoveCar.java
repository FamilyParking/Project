package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.CarGroupRelationTable;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.fragment.CarFragment;
import it.familiyparking.app.serverClass.Car;

/**
 * Created by francesco on 02/01/15.
 */
public class DoRemoveCar implements Runnable {

    private MainActivity activity;
    private CarFragment carFragment;
    private String carID;

    public DoRemoveCar(FragmentActivity activity, Fragment fragment, String carID) {
        this.activity = (MainActivity)activity;
        this.carID = carID;
        this.carFragment = (CarFragment) fragment;
    }

    @Override
    public void run() {
        Looper.prepare();

        /***************/
        /* CALL SERVER */
        /***************/

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DataBaseHelper databaseHelper = new DataBaseHelper(activity);
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();


        CarTable.deleteCar(db,carID);
        CarGroupRelationTable.deleteCar(db,carID);

        final ArrayList<Car> cars = CarTable.getAllCar(db);
        boolean emptyCar = cars.isEmpty();

        db.close();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                carFragment.updateAdapter(cars);
                activity.resetProgressDialogCircular(true);
            }
        });

        if(emptyCar)
            activity.removeCarFragment(emptyCar);
    }
}
