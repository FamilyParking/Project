package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.CarGroupRelationTable;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.GroupTable;
import it.familiyparking.app.fragment.GroupFragment;

/**
 * Created by francesco on 02/01/15.
 */
public class DoRemoveGroup implements Runnable {

    private String groupID;
    private MainActivity activity;
    private GroupFragment groupFragment;

    public DoRemoveGroup(FragmentActivity activity, Fragment fragment, String groupID) {
        this.groupID = groupID;
        this.activity = (MainActivity)activity;
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

        GroupTable.deleteGroup(db, groupID);
        CarGroupRelationTable.deleteGroup(db, groupID);

        final ArrayList<String> list_groupID = GroupTable.getAllGroup(db);
        boolean emptyGroup = list_groupID.isEmpty();

        db.close();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                groupFragment.updateAdapter(list_groupID);
                activity.resetProgressDialogCircular(true);
            }
        });

        if(emptyGroup)
            activity.removeGroupFragment(emptyGroup);
    }
}
