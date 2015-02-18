package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.GroupTable;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.dialog.ProgressDialogCircular;
import it.familiyparking.app.serverClass.Contact;
import it.familiyparking.app.serverClass.GroupForCall;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.ServerCall;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class DoSaveGroup implements Runnable {

    private String group_name;
    private MainActivity activity;
    private ArrayList<Contact> group;
    private ProgressDialogCircular progressDialogCircular;

    public DoSaveGroup(FragmentActivity activity, String group_name, ArrayList<Contact> group, ProgressDialogCircular progressDialogCircular) {
        this.group_name = group_name;
        this.activity = (MainActivity)activity;
        this.group = group;
        this.progressDialogCircular = progressDialogCircular;
    }

    @Override
    public void run() {
        Looper.prepare();

        DataBaseHelper databaseHelper = new DataBaseHelper(activity);
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();
        User user = UserTable.getUser(db);

        final Result result = ServerCall.createGroup(new GroupForCall(user.getEmail(),user.getCode(),group_name,group));

        if(result.isFlag()) {
            String groupID = (String) result.getObject();
            String timestamp = Tools.getTimestamp();

            for (Contact contact : group)
                GroupTable.insertContact(db, groupID, group_name, contact, timestamp);

        }

        db.close();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.resetProgressDialogCircular(false);
            }
        });

        activity.closeCreateGroup();

        if(result.isFlag()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.createToast(activity, "Group created!", Toast.LENGTH_SHORT);
                }
            });
        }
        else{
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.createToast(activity, result.getDescription(), Toast.LENGTH_SHORT);
                }
            });
        }
    }
}
