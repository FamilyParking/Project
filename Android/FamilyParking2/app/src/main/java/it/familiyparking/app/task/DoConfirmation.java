package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.widget.Toast;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.ServerCall;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class DoConfirmation implements Runnable {

    private String code;
    private MainActivity activity;

    public DoConfirmation(MainActivity activity, String code) {
        this.code = code;
        this.activity = activity;
    }

    @Override
    public void run() {
        Looper.prepare();

        DataBaseHelper databaseHelper = new DataBaseHelper(activity.getApplicationContext());
        final SQLiteDatabase db = databaseHelper.getReadableDatabase();

        User user = UserTable.getUser(db);
        user.setCode(code);

        final Result result = ServerCall.confirmation(user);

        if(result.isFlag()) {

            UserTable.updateUser(db,user);

            db.close();

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.createToast(activity,"Account confirmed!", Toast.LENGTH_SHORT);
                    activity.callToEndConfirmation();
                }
            });
        }
        else{

            UserTable.deleteUser(db);

            db.close();

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.createToast(activity,result.getDescription(), Toast.LENGTH_SHORT);
                    activity.callToEndConfirmationInError();
                }
            });
        }
    }
}
