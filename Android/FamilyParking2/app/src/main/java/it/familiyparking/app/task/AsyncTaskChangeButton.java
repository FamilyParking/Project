package it.familiyparking.app.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

/**
 * Created by francesco on 18/12/14.
 */
public class AsyncTaskChangeButton extends AsyncTask<Object,Void,Void> {

    private Button button;
    private Activity activity;
    private int drawableID;

    @Override
    protected Void doInBackground(Object... object) {
        button = (Button) object[1];
        activity = (Activity) object[2];
        drawableID = ((Integer) object[3]).intValue();
        final boolean clickable = ((Boolean) object[4]).booleanValue();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setClickable(clickable);
            }
        });

        try{
            Thread.sleep(((Long) object[0]).intValue());
        }
        catch(InterruptedException e){
            Log.e("AsyncTaskLocationMap",e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setBackgroundDrawable(activity.getResources().getDrawable(drawableID));
            }
        });

    }

}