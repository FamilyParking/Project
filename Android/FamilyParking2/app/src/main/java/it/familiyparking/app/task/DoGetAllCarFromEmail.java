package it.familiyparking.app.task;

import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.ServerCall;

/**
 * Created by francesco on 02/01/15.
 */
public class DoGetAllCarFromEmail implements Runnable {

    private User user;
    private MainActivity activity;

    public DoGetAllCarFromEmail(MainActivity activity, User user) {
        this.user = user;
        this.activity = activity;
    }

    @Override
    public void run() {
        Looper.prepare();

        final Result result = ServerCall.getAllCarCreatedByMe(user);

        if(result.isFlag()){
            ArrayList<Car> cars = (ArrayList<Car>) result.getObject();
            for(final Car c : cars) {
                if((!c.getLatitude().equals("0"))||(!c.getLongitude().equals("0"))) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.park(c);
                        }
                    });
                }
            }
        }
        else{
            Log.e("DoGetAllCarFromEmail",result.getDescription());
        }
    }
}