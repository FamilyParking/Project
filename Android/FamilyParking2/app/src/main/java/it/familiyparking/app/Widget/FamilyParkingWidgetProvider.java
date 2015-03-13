package it.familiyparking.app.Widget;

/**
 * Created by Obaida on 3/13/2015.
 */
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;

public class FamilyParkingWidgetProvider extends AppWidgetProvider {

    private static final String Park_Button_Clicked_Action = "park_button_clicked";
    private static final String UnPark_Button_Clicked_Action = "unpark_button_clicked";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        super.onUpdate(context,appWidgetManager,appWidgetIds);
        /*int[] appWidgetIds holds ids of multiple instance
        * of your widget meaning you are placing more than one widgets on  your home screen*/
        final int N = appWidgetIds.length;
        for (int i=0; i<N ; i++)
        {
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget);
            //Assign a behaviour to the button according to the number of the cars
            assignBehaviourToButtons(context,remoteView);
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteView);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(Park_Button_Clicked_Action.equals(intent.getAction())) {
            doParking();
            Toast.makeText(context, "Parking done", Toast.LENGTH_SHORT).show();
        }
        else
        if(UnPark_Button_Clicked_Action.equals(intent.getAction())) {
            doUnpraking();
            Toast.makeText(context, "UnParking  done", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

        super.onDeleted(context,appWidgetIds);

        Toast.makeText(context,"see ya sucka",Toast.LENGTH_SHORT).show();

    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    protected int countCarsCurrentUserSharing()
    {
        return 2;
    }

    protected void assignBehaviourToButtons(Context context, RemoteViews remoteView) {
        if (countCarsCurrentUserSharing() > 1) {
            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context,MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            remoteView.setOnClickPendingIntent(R.id.park_button, pendingIntent);
            remoteView.setOnClickPendingIntent(R.id.unpark_button, pendingIntent);




        } else if (countCarsCurrentUserSharing() == 1) {
            remoteView.setOnClickPendingIntent(R.id.park_button,
                    getPendingSelfIntent(context, Park_Button_Clicked_Action));
            remoteView.setOnClickPendingIntent(R.id.unpark_button,
                    getPendingSelfIntent(context, UnPark_Button_Clicked_Action));
        }
    }

    protected void doUnpraking()
    {

    }
    protected void doParking()
    {

    }

}
