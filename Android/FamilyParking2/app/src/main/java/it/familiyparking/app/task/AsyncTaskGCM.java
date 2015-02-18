package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.utility.Code;

/**
 * Created by francesco on 18/12/14.
 */
public class AsyncTaskGCM extends AsyncTask<Object,Void,Void> {

    String regID;
    MainActivity activity;

    @Override
    protected Void doInBackground(Object... params) {

        this.activity = (MainActivity) params[0];

        try {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(activity.getApplicationContext());
            regID = gcm.register(Code.PROJECT_NUMBER);

            Log.e("GCM ID","Valuer: "+regID);
        } catch (IOException ex) {
            Log.e("GCM Registration",ex.getMessage());

        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        DataBaseHelper databaseHelper = new DataBaseHelper(activity.getApplicationContext());
        final SQLiteDatabase db = databaseHelper.getReadableDatabase();

        UserTable.updateDeviceID(db,regID);

        db.close();

        new Thread(new DoUpdateGCM(activity)).start();
    }
}