package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import java.util.ArrayList;

import it.familiyparking.app.FPApplication;
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
public class DoUpdateCar implements Runnable {

    private MainActivity activity;
    private User user;
    private Car newCar;
    private Car oldCar;

    public DoUpdateCar(FragmentActivity activity, Car newCar, Car oldCar, User user) {
        this.activity = (MainActivity)activity;
        this.newCar = newCar;
        this.oldCar = oldCar;
        this.user = user;
    }

    @Override
    public void run() {
        Looper.prepare();

        if(Tools.isOnline(activity)) {

            SQLiteDatabase db = Tools.getDB_Writable(activity);

            boolean updateCar = updateCar(db);
            boolean addUser = addUsers(db);
            boolean removeUser = removeUsers(db);

            if(updateCar || addUser || removeUser)
                success();

            db.close();
        }
        else{
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.endNoConnection();
                }
            });
        }
    }

    private boolean updateCar(SQLiteDatabase db){
        if(!newCar.equals(oldCar)) {
            Result result = ServerCall.updateCar(user, newCar);

            if (result.isFlag()) {
                CarTable.updateCar(db, newCar);
            } else {
                Tools.manageServerError(result, activity);

                return false;
            }
        }

        return true;
    }

    private boolean addUsers(SQLiteDatabase db){
        ArrayList<User> toAdd = new ArrayList<>();

        for(User newContact : newCar.getUsers()){
            boolean add = true;
            for(User oldContact : oldCar.getUsers()){
                if(newContact.equals(oldContact)){
                    add = false;
                    break;
                }
            }
            if(add)
                toAdd.add(newContact);
        }

        if(toAdd.isEmpty())
            return true;

        Result result = ServerCall.addCarUsers(user, oldCar, toAdd);

        if(result.isFlag()) {
            for(User contact : toAdd)
                GroupTable.insertContact(db, oldCar.getId(), contact);
        }
        else{
            Tools.manageServerError(result,activity);

            return false;
        }

        return true;
    }

    private boolean removeUsers(SQLiteDatabase db){
        ArrayList<User> toRemove = new ArrayList<>();

        for(User oldContact : oldCar.getUsers()){
            boolean remove = true;
            for(User newContact : newCar.getUsers()){
                if(oldContact.equals(newContact)){
                    remove = false;
                    break;
                }
            }

            if(remove)
                toRemove.add(oldContact);
        }

        if(toRemove.isEmpty())
            return true;

        Result result = ServerCall.removeCarUsers(user, oldCar, toRemove);

        if(result.isFlag()) {
            for(User contact : toRemove)
                GroupTable.deleteContact(db, contact.getEmail(), oldCar.getId());
        }
        else{
            Tools.manageServerError(result,activity);

            return false;
        }

        return true;
    }

    private void success(){
        SQLiteDatabase db = Tools.getDB_Readable(activity);
        ((FPApplication) activity.getApplication()).setCars(CarTable.getAllCar(db));
        db.close();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.resetProgressDialogCircular(false);
                activity.endUpdateCar(newCar);
                activity.resetModifyCar(false);
                Tools.createToast(activity, activity.getResources().getString(R.string.updated_car), Toast.LENGTH_SHORT);
            }
        });
    }
}
