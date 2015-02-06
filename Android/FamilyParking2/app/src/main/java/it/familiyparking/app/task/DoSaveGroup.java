package it.familiyparking.app.task;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.GroupTable;
import it.familiyparking.app.dialog.ProgressDialogCircular;
import it.familiyparking.app.serverClass.Contact;

/**
 * Created by francesco on 02/01/15.
 */
public class DoSaveGroup implements Runnable {

    private String group_name;
    private String groupID;
    private MainActivity activity;
    private ArrayList<Contact> group;
    private ProgressDialogCircular progressDialogCircular;

    public DoSaveGroup(FragmentActivity activity, String group_name, String groupID, ArrayList<Contact> group, ProgressDialogCircular progressDialogCircular) {
        this.group_name = group_name;
        this.groupID = groupID;
        this.activity = (MainActivity)activity;
        this.group = group;
        this.progressDialogCircular = progressDialogCircular;
    }

    @Override
    public void run() {
        Looper.prepare();

        while(!progressDialogCircular.updateMessage("Contact server ..."));

        /***************/
        /* CALL SERVER */
        /***************/
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while(!progressDialogCircular.updateMessage("Save data ..."));

        DataBaseHelper databaseHelper = new DataBaseHelper(activity);
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();

        /*GroupTable.deleteGroup(db, groupID, group_name);

        for(Contact contact : group)
            GroupTable.insertContact(db,groupID,group_name,contact);*/

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        db.close();

        while(!progressDialogCircular.updateMessage("Finish!"));

        activity.getSupportFragmentManager().beginTransaction().remove(progressDialogCircular).commit();
        activity.closeCreateGroup();
    }
}
