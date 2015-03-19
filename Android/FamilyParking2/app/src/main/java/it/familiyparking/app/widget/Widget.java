package it.familiyparking.app.widget;

/**
 * Created by francesco on 18/03/15.
 */

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import it.familiyparking.app.R;
import it.familiyparking.app.utility.Code;


public class Widget extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

            views.setViewVisibility(R.id.toParkWidget, View.VISIBLE);
            views.setOnClickPendingIntent(R.id.toParkWidget,getPendingSelfIntent(context, Code.TYPE_PARK));

            views.setViewVisibility(R.id.toUnparkWidget, View.VISIBLE);
            views.setOnClickPendingIntent(R.id.toUnparkWidget,getPendingSelfIntent(context, Code.TYPE_UNPARK));

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if(intent.getAction().equals(Code.TYPE_PARK)) {
            Log.e("Widget","Park");
        }
        else  if(intent.getAction().equals(Code.TYPE_UNPARK)) {
            Log.e("Widget","Unpark");
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
