package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.CarGroupRelationTable;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.fragment.CarFragment;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.GroupForCall;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.ServerCall;
import it.familiyparking.app.utility.Tools;

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

        DataBaseHelper databaseHelper = new DataBaseHelper(activity);
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();

        User user = UserTable.getUser(db);

        final Result result = ServerCall.deleteCar(new Car(carID, user.getEmail(), user.getCode()));

        if(result.isFlag()) {
            CarTable.deleteCar(db, carID);
            CarGroupRelationTable.deleteCar(db, carID);

            final ArrayList<Car> cars = CarTable.getAllCar(db);
            boolean emptyCar = cars.isEmpty();

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    carFragment.updateAdapter(cars);
                    activity.resetProgressDialogCircular(true);
                    Tools.createToast(activity, "Car removed!", Toast.LENGTH_SHORT);
                }
            });

            if (emptyCar)
                activity.removeCarFragment(emptyCar);
        }
        else{
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.removeCarFragment(false);
                    activity.resetProgressDialogCircular(true);
                    Tools.createToast(activity, result.getDescription(), Toast.LENGTH_SHORT);
                }
            });
        }

        db.close();
    }
}
