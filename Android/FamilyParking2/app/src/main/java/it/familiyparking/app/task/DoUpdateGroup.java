package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.CarGroupRelationTable;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.GroupTable;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.Contact;
import it.familiyparking.app.serverClass.CreateRelationGroupCar;
import it.familiyparking.app.serverClass.Group;
import it.familiyparking.app.serverClass.GroupForCall;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.ServerCall;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class DoUpdateGroup implements Runnable {

    private String newName;
    private String newCarName;
    private String newCarBrand;
    private Car newCar;
    private Group group;
    private MainActivity activity;
    private ArrayList<Contact> newArray;

    public DoUpdateGroup(FragmentActivity activity, String newName, ArrayList<Contact> newArray, Car newCar, Group group) {
        this.newName = newName;
        this.newCarName = newCar.getName();
        this.newCarBrand = newCar.getBrand();
        this.newCar = newCar;
        this.newArray = newArray;
        this.activity = (MainActivity)activity;
        this.group = group;
    }

    @Override
    public void run() {
        Looper.prepare();

        ArrayList<Contact> toAdd = new ArrayList<>();

        for(Contact newContact : newArray){
            boolean add = true;
            for(Contact oldContact : group.getContacts()){
                if(newContact.equals(oldContact)){
                    add = false;
                    break;
                }
            }
            if(add) toAdd.add(newContact);
        }

        ArrayList<Contact> toRemove = new ArrayList<>();

        for(Contact oldContact : group.getContacts()){
            boolean remove = true;
            for(Contact newContact : newArray){
                if(oldContact.equals(newContact)){
                    remove = false;
                    break;
                }
            }
            if(remove) toRemove.add(oldContact);
        }

        DataBaseHelper databaseHelper = new DataBaseHelper(activity);
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();

        User user = UserTable.getUser(db);

        boolean notifyAdapter = false;

        if(!toRemove.isEmpty()){
            final Result resultAdd = ServerCall.removeContactFromGroup(new GroupForCall(group.getId(),user.getEmail(), user.getCode(), group.getName(), toRemove));

            if(resultAdd.isFlag()) {
                for (Contact contact : toRemove) {
                    GroupTable.deleteContact(db, contact.getEmail(), group.getId());
                    group.removeContact(contact);
                }

                notifyAdapter = true;
            }
            else{
                Log.e("UpdateGroup[REMOVE]",resultAdd.getDescription());
            }
        }

        if(!toAdd.isEmpty()){

            final Result resultAdd = ServerCall.addContactFromGroup(new GroupForCall(group.getId(), user.getEmail(), user.getCode(), group.getName(), toAdd));

            if(resultAdd.isFlag()){
                for (Contact contact : toAdd) {
                    GroupTable.insertContact(db, group.getId(), group.getName(), contact, "");
                    group.addContact(contact);
                }

                notifyAdapter = true;
            }
            else{
                Log.e("UpdateGroup[ADD]",resultAdd.getDescription());
            }
        }

        if(!(newName.equals(group.getName()))){

            final Result resultName = ServerCall.updateGroupName(new GroupForCall(group.getId(), user.getEmail(), user.getCode(), newName, null));

            if(resultName.isFlag()) {
                GroupTable.updateNameGroup(db, group.getName(), newName);

                group.setName(newName);

                notifyAdapter = true;
            }
            else{
                Log.e("UpdateGroup[NAME]",resultName.getDescription());
            }
        }

        Car car = group.getCar();

        if((!newCarName.equals("empty") || !newCarBrand.equals("empty"))) {
            if ((car == null) || (!newCarName.equals(car.getName()) || !newCarBrand.equals(car.getBrand()))) {

                if (car == null) {
                    car = newCar;

                    callServer(car, user);

                    CarGroupRelationTable.insertRelation(db, car.getId(), group.getId());

                    group.setCar(car);
                } else {
                    car.setName(newCarName);
                    car.setName(newCarBrand);

                    callServer(car, user);

                    CarTable.updateNameCar(db, car.getId(), newCarName);
                    CarTable.updateNameBrand(db, car.getId(), newCarBrand);
                }

                notifyAdapter = true;
            }
        }


        final ArrayList<String> list_groupID = GroupTable.getAllGroup(db);
        final boolean notifyAdapterF = notifyAdapter;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(notifyAdapterF)
                    activity.updateGroupAdapter(list_groupID);

                activity.resetProgressDialogCircular(false);
                activity.closeModifyGroup();
                Tools.createToast(activity, "Group updated!", Toast.LENGTH_SHORT);
            }
        });

        db.close();
    }

    private void callServer(Car car, User user){
        CreateRelationGroupCar createRelationGroupCar = new CreateRelationGroupCar(user.getEmail(),user.getCode(),car.getId(),group.getId());
        Result result = ServerCall.insertCarToGroup(createRelationGroupCar);
        Log.e("DoUpdateGroup [CAR]",result.toString());
    }
}
