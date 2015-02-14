package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.GroupTable;
import it.familiyparking.app.dialog.ProgressDialogCircular;
import it.familiyparking.app.serverClass.Contact;
import it.familiyparking.app.serverClass.Group;

/**
 * Created by francesco on 02/01/15.
 */
public class DoUpdateGroup implements Runnable {

    private String newName;
    private Group group;
    private MainActivity activity;
    private ArrayList<Contact> newArray;
    private ProgressDialogCircular progressDialogCircular;

    public DoUpdateGroup(FragmentActivity activity, String newName, ArrayList<Contact> newArray, Group group, ProgressDialogCircular progressDialogCircular) {
        this.newName = newName;
        this.newArray = newArray;
        this.activity = (MainActivity)activity;
        this.group = group;
        this.progressDialogCircular = progressDialogCircular;
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
        }

        db.close();

        activity.updateAdapterGroup();
        activity.resetProgressDialogCircular();
        activity.closeModifyGroup();
    }
}
