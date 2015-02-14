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

        DataBaseHelper databaseHelper = new DataBaseHelper(activity);
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();

        if(groupID != null) {
            GroupTable.deleteGroup(db, groupID);
        }

        /***************/
        /* CALL SERVER */
        /***************/
        /**************************************/
        /**/groupID = "0123456789";         /**/
        /**/String timestamp = "0123456789";/**/
        /**************************************/
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Contact contact : group)
            GroupTable.insertContact(db, groupID, group_name, contact, timestamp);

        db.close();

        activity.resetProgressDialogCircular();
        activity.closeCreateGroup();
    }
}
