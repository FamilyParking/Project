package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;

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

        ArrayList<Contact> difference = new ArrayList<>();

        for(Contact newContact : newArray){
            boolean add = true;
            for(Contact oldContact : group.getContacts()){
                if(newContact.equals(oldContact)){
                    add = false;
                    break;
                }
            }
            if(add) difference.add(newContact);
        }

        DataBaseHelper databaseHelper = new DataBaseHelper(activity);
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();

        boolean notifyAdapter = false;

        if(!difference.isEmpty()){
            /***************/
            /* CALL SERVER */
            /***************/
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (Contact contact : difference)
                GroupTable.deleteContact(db,contact.getEmail(),group.getId());

            notifyAdapter = true;
        }

        if(!(newName.equals(group.getName()))){
            /***************/
            /* CALL SERVER */
            /***************/
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            GroupTable.updateNameGroup(db,group.getName(),newName);

            notifyAdapter = true;
        }

        if(notifyAdapter)
            activity.updateAdapterGroup();

        db.close();

        activity.getSupportFragmentManager().beginTransaction().remove(progressDialogCircular).commit();
        activity.closeModifyGroup();
    }
}
