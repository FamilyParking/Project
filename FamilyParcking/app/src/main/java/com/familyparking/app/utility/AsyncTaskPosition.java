package com.familyparking.app.utility;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by francesco on 18/12/14.
 */
public class AsyncTaskPosition extends AsyncTask<Context,Void, String> {

    Context context;

    @Override
    protected String doInBackground(Context... context) {
        this.context = context[0];

        if(Tools.isOnline(this.context))
            return ServerInteraction.postPosition();
        else
            return "No connection!";
    }

    @Override
    protected void onPostExecute(String response) {
        Toast.makeText(this.context, response, Toast.LENGTH_SHORT).show();
    }
}