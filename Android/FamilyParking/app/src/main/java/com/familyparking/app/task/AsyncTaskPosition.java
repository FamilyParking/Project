package com.familyparking.app.task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.familyparking.app.dialog.ProgressCircleDialog;
import com.familyparking.app.serverClass.Car;
import com.familyparking.app.serverClass.Result;
import com.familyparking.app.utility.ServerCall;

import java.util.ArrayList;

/**
 * Created by francesco on 18/12/14.
 */
public class AsyncTaskPosition extends AsyncTask<ArrayList<Object>,Void, Result> {

    private Context context;
    private Car car;
    private ProgressCircleDialog dialog;

    @Override
    protected Result doInBackground(ArrayList<Object>... object) {
        ArrayList<Object> params = object[0];
        this.context = (Context)params.get(0);
        this.car = (Car)params.get(1);
        this.dialog = (ProgressCircleDialog)params.get(2);

        return ServerCall.sendPosition(this.car);
    }

    @Override
    protected void onPostExecute(Result response) {
        dialog.dismiss();
        Toast.makeText(this.context, response.getDescription(), Toast.LENGTH_SHORT).show();
    }
}