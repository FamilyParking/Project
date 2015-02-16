package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.GroupTable;
import it.familiyparking.app.fragment.CarFragment;
import it.familiyparking.app.fragment.GroupFragment;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.Contact;
import it.familiyparking.app.serverClass.Group;

/**
 * Created by francesco on 02/01/15.
 */
public class DoUpdateCar implements Runnable {

    private String newName;
    private String newBrand;
    private Car oldCar;
    private MainActivity activity;
    private CarFragment carFragment;

    public DoUpdateCar(FragmentActivity activity, Fragment fragment, String newName, String newBrand, Car oldCar) {
        this.newName = newName;
        this.newBrand = newBrand;
        this.oldCar = oldCar;
        this.activity = (MainActivity)activity;
        this.carFragment = (CarFragment) fragment;
    }

    @Override
    public void run() {
        Looper.prepare();

        DataBaseHelper databaseHelper = new DataBaseHelper(activity);
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();

        boolean notifyAdapter = false;

        if(!newName.equals(oldCar.getName()) || !newBrand.equals(oldCar.getBrand())){
            oldCar.setName(newName);
            oldCar.setName(newBrand);

            /***************/
            /* CALL SERVER */
            /***************/
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            CarTable.updateNameCar(db,oldCar.getId(),newName);
            CarTable.updateNameBrand(db,oldCar.getId(),newBrand);

            final ArrayList<Car> cars = CarTable.getAllCar(db);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    carFragment.updateAdapter(cars);
                }
            });
        }

        db.close();

        activity.resetProgressDialogCircular(false);
        activity.closeModifyGroup();
    }
}
