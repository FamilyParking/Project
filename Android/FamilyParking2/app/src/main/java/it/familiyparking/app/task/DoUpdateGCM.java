package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.util.Log;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.utility.ServerCall;

/**
 * Created by francesco on 02/01/15.
 */
public class DoUpdateGCM implements Runnable {

    private MainActivity activity;

    public DoUpdateGCM(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        Looper.prepare();

        DataBaseHelper databaseHelper = new DataBaseHelper(activity.getApplicationContext());
        final SQLiteDatabase db = databaseHelper.getReadableDatabase();

        final Result result = ServerCall.updateGoogleCode(UserTable.getUser(db));

        db.close();

        if(result.isFlag()) {
            Log.e("UpdateGMC","OK");
        }
        else{
            Log.e("UpdateGMC", "KO");
        }
    }
}
