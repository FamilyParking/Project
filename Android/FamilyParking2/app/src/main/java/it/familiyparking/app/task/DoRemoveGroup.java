package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.GroupTable;
import it.familiyparking.app.dialog.ProgressDialogCircular;
import it.familiyparking.app.fragment.GroupFragment;
import it.familiyparking.app.serverClass.Group;

/**
 * Created by francesco on 02/01/15.
 */
public class DoRemoveGroup implements Runnable {

    private String groupID;
    private MainActivity activity;
    private ProgressDialogCircular progressDialogCircular;
    private ArrayList<Group> groups;
    private GroupFragment groupFragment;

    public DoRemoveGroup(FragmentActivity activity, Fragment fragment, String groupID, ArrayList<Group> groups, ProgressDialogCircular progressDialogCircular) {
        this.groupID = groupID;
        this.activity = (MainActivity)activity;
        this.progressDialogCircular = progressDialogCircular;
        this.groups = groups;
        groupFragment = (GroupFragment) fragment;
    }

    @Override
    public void run() {
        Looper.prepare();

        /***************/
        /* CALL SERVER */
        /***************/

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DataBaseHelper databaseHelper = new DataBaseHelper(activity);
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();

        if(groupID != null) {
            GroupTable.deleteGroup(db, groupID);
        }

        for(int i=0; i<groups.size(); i++){
            if(groups.get(i).getId().equals(groupID)){
                groups.remove(i);
                break;
            }
        }

        activity.getSupportFragmentManager().beginTransaction().remove(progressDialogCircular).commit();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                groupFragment.updateAdapter();
            }
        });

        activity.removeGroupFragment(true);
    }
}
