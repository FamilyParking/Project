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
public class DoSignIn implements Runnable {

    private String name;
    private String email;
    private MainActivity activity;

    public DoSignIn(MainActivity activity, String name, String email) {
        this.name = name;
        this.email = email;
        this.activity = activity;
    }

    @Override
    public void run() {
        Looper.prepare();

        User user = new User(name,email, Tools.getUniqueDeviceId(activity));
        final Result result = ServerCall.signIn(user);

        if(result.isFlag()) {
            DataBaseHelper databaseHelper = new DataBaseHelper(activity.getApplicationContext());
            final SQLiteDatabase db = databaseHelper.getReadableDatabase();

            UserTable.insertUser(db,user);

            db.close();

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.createToast(activity,"Confirmation code sent to your email!", Toast.LENGTH_SHORT);
                    activity.callToEndSignIn();
                }
            });
        }
        else{
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.createToast(activity,result.getDescription(), Toast.LENGTH_SHORT);
                    activity.callToEndSignInError();
                }
            });
        }
    }
}