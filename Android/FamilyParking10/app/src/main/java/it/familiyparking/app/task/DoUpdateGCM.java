package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.ServerCall;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class DoUpdateGCM implements Runnable {

    private MainActivity activity;
    private User user;

    public DoUpdateGCM(MainActivity activity, User user) {
        this.activity = activity;
        this.user = user;
    }

    @Override
    public void run() {
        Looper.prepare();

        if(Tools.isOnline(activity)) {

            final Result result = ServerCall.updateGoogleCode(user);

            if (result.isFlag()) {
                SQLiteDatabase db = Tools.getDB_Writable(activity);
                UserTable.updateGCM_ID(db, user.getGoogle_cloud_messaging());
                db.close();
            } else {
                Tools.manageServerError(result, activity);
            }

        }

    }
}
