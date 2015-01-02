package com.familyparking.app.task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.familyparking.app.serverClass.Car;
import com.familyparking.app.utility.ServerCall;
import com.familyparking.app.utility.Tools;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by francesco on 18/12/14.
 */
public class AsyncTaskPosition extends AsyncTask<ArrayList<Object>,Void, String> {

    private Context context;
    private Car car;

    @Override
    protected String doInBackground(ArrayList<Object>... object) {
        ArrayList<Object> params = object[0];
        this.context = (Context)params.get(0);
        this.car = (Car)params.get(1);

        if(Tools.isOnline(this.context))
            return ServerCall.sendPosition(this.car);
        else
            return "No connection!";
    }

    @Override
    protected void onPostExecute(String response) {
        Toast.makeText(this.context, response, Toast.LENGTH_SHORT).show();
    }
}