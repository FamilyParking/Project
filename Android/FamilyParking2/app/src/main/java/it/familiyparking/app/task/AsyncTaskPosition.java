package it.familiyparking.app.task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;

import it.familiyparking.app.dialog.ProgressCircleDialog;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.utility.ServerCall;

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

        if((response == null) && (response.getDescription() == null))
            Toast.makeText(this.context, "Connection not available. Please, try one more time!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this.context, response.getDescription(), Toast.LENGTH_SHORT).show();
    }
}