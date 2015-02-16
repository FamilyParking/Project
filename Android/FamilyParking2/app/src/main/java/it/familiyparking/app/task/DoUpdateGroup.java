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
import it.familiyparking.app.dialog.ProgressDialogCircular;
import it.familiyparking.app.fragment.GroupFragment;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.Contact;
import it.familiyparking.app.serverClass.Group;

/**
 * Created by francesco on 02/01/15.
 */
public class DoUpdateGroup implements Runnable {

    private String newName;
    private String newCarName;
    private String newCarBrand;
    private Group group;
    private MainActivity activity;
    private ArrayList<Contact> newArray;
    private GroupFragment groupFragment;

    public DoUpdateGroup(FragmentActivity activity, Fragment fragment, String newName, ArrayList<Contact> newArray, Car newCar, Group group) {
        this.newName = newName;
        this.newCarName = newCar.getName();
        this.newCarBrand = newCar.getBrand();
        this.newArray = newArray;
        this.activity = (MainActivity)activity;
        this.group = group;
        this.groupFragment = (GroupFragment) fragment;
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

        boolean notifyAdapter = false;

        if(!toRemove.isEmpty()){
            /***************/
            /* CALL SERVER */
            /***************/
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (Contact contact : toRemove) {
                GroupTable.deleteContact(db, contact.getEmail(), group.getId());
                group.removeContact(contact);
            }

            notifyAdapter = true;
        }

        if(!toAdd.isEmpty()){
            /***************/
            /* CALL SERVER */
            /***************/
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (Contact contact : toAdd) {
                GroupTable.insertContact(db, group.getId(), group.getName(), contact, "");
                group.addContact(contact);
            }

            notifyAdapter = true;
        }

        if(!(newName.equals(group.getName()))){
            /***************/
            /* CALL SERVER */
            /***************/
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            GroupTable.updateNameGroup(db,group.getName(),newName);

            group.setName(newName);

            notifyAdapter = true;
        }

        Car car = group.getCar();
        if(!newCarName.equals(car.getName()) || !newCarBrand.equals(car.getBrand())){
            car.setName(newCarName);
            car.setName(newCarBrand);

            /***************/
            /* CALL SERVER */
            /***************/
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            CarTable.updateNameCar(db,car.getId(),newCarName);
            CarTable.updateNameBrand(db,car.getId(),newCarBrand);

            notifyAdapter = true;
        }

        if(notifyAdapter) {
            final ArrayList<String> list_groupID = GroupTable.getAllGroup(db);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    groupFragment.updateAdapter(list_groupID);
                }
            });
        }

        db.close();

        activity.resetProgressDialogCircular(false);
        activity.closeModifyGroup();
    }
}
