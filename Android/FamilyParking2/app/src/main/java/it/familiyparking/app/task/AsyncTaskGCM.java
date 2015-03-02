package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.Code;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 18/12/14.
 */
public class AsyncTaskGCM extends AsyncTask<Object,Void,Void> {

    private String regID;
    private User user;
    private MainActivity activity;

    @Override
    protected Void doInBackground(Object... params) {

        this.user = (User) params[0];
        this.activity = (MainActivity) params[1];

        registration();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        SQLiteDatabase db = Tools.getDB_Writable(activity);

        UserTable.updateDeviceID(db,regID);
        user.setCode(regID);

        db.close();

        new Thread(new DoUpdateGCM(activity,user)).start();
    }

    private void registration(){
        try {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(activity.getApplicationContext());
            regID = gcm.register(Code.PROJECT_NUMBER);
        } catch (Exception ex) {
            Log.e("GCM Registration",ex.getMessage());

            try {
                Thread.sleep(5000);
            }
            catch (Exception e){}

            registration();
        }
    }
}