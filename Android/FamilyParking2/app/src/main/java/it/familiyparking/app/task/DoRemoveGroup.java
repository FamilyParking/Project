package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.CarGroupRelationTable;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.GroupTable;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.fragment.GroupFragment;
import it.familiyparking.app.serverClass.GroupForCall;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.ServerCall;
import it.familiyparking.app.utility.Tools;

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

        DataBaseHelper databaseHelper = new DataBaseHelper(activity);
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();

        User user = UserTable.getUser(db);

        final Result result = ServerCall.deleteGroup(new GroupForCall(groupID,user.getEmail(),user.getCode()));

        if(result.isFlag()) {
            GroupTable.deleteGroup(db, groupID);
            CarGroupRelationTable.deleteGroup(db, groupID);

            final ArrayList<String> list_groupID = GroupTable.getAllGroup(db);
            boolean emptyGroup = list_groupID.isEmpty();

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    groupFragment.updateAdapter(list_groupID);
                    activity.resetProgressDialogCircular(true);
                    Tools.createToast(activity, "Group removed!", Toast.LENGTH_SHORT);
                }
            });

            if (emptyGroup)
                activity.removeGroupFragment(emptyGroup);
        }
        else{
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.removeGroupFragment(false);
                    activity.resetProgressDialogCircular(true);
                    Tools.createToast(activity, result.getDescription(), Toast.LENGTH_SHORT);
                }
            });
        }

        db.close();
    }
}
