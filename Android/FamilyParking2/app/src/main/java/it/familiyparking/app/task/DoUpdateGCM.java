package it.familiyparking.app.task;

import android.os.Looper;
import android.util.Log;

import it.familiyparking.app.MainActivity;
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

        final Result result = ServerCall.updateGoogleCode(user);

        if(result.isFlag()) {
            Log.e("UpdateGMC","OK");
        }
        else{
            Tools.manageServerError(result, activity);
        }
    }
}
