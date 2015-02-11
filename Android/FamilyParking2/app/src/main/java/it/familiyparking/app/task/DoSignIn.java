package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.DataBaseHelper;

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

        DataBaseHelper databaseHelper = new DataBaseHelper(activity.getApplicationContext());
        final SQLiteDatabase db = databaseHelper.getReadableDatabase();
        db.close();

        /***************/
        /* CALL SERVER */
        /***************/

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.callToEndSignIn();
            }
        });
    }
}
