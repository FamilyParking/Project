package it.familiyparking.app.widget;

/**
 * Created by francesco on 18/03/15.
 */

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.Code;
import it.familiyparking.app.utility.ServerCall;
import it.familiyparking.app.utility.Tools;


public class Widget extends AppWidgetProvider {

    private static int result_code = 0;

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
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();

        if((action.equalsIgnoreCase(Code.TYPE_PARK)) || (action.equalsIgnoreCase(Code.TYPE_UNPARK))){

            SQLiteDatabase db = Tools.getDB_Readable(context);
            User user = UserTable.getUser(db);
            ArrayList<Car> cars = carSize(db);
            db.close();

            if (user != null) {
                if (cars.isEmpty()) {
                    callMainActivity(context, "No car available");
                }
                else {
                    if(cars.size() == 1) {

                        if (action.equals(Code.TYPE_PARK))
                            doPark(context, cars.get(0), user);
                        else if (action.equals(Code.TYPE_UNPARK))
                            doUnpark(context, cars.get(0), user);

                        while (result_code == 0);
                        handlerCode(context);
                    }
                    else{
                        callWidgetActivity(context,action);
                    }
                }
            } else {
                callMainActivity(context, "Please register your account");
            }

        }
    }

    private PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private ArrayList<Car> carSize(SQLiteDatabase db){
        return CarTable.getAllCar(db);
    }

    private void callMainActivity(Context context, String message){
        Tools.createToast(context, message, Toast.LENGTH_LONG);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        if(pendingIntent != null) {
            try{
                pendingIntent.send();
            }
            catch (Exception e){}
        }
    }

    private void callWidgetActivity(Context context, String action){
        Intent intent = new Intent(context, WidgetActivity.class);
        intent.putExtra("action",action);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if(pendingIntent != null) {
            try{
                pendingIntent.send();
            }
            catch (Exception e){}
        }
    }

    private void doPark(final Context context, final Car car, final User user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                if(Tools.isOnline(context)) {

                    double[] position = Tools.getPosition(context);
                    car.setPosition(position);
                    car.setTimestamp(Tools.getTimestamp());
                    car.setLast_driver(user.getEmail());

                    Result result = ServerCall.parkCar(user, car);

                    if (result.isFlag()) {
                        car.setParked(true);

                        SQLiteDatabase db = Tools.getDB_Writable(context);
                        CarTable.updateCar(db,car);
                        db.close();

                        result_code = 1;
                    }
                    else {
                        result_code = 2;
                    }

                }
                else{
                    result_code = 3;
                }
            }
        }).start();
    }

    private void doUnpark(final Context context, final Car car, final User user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                if(Tools.isOnline(context)) {
                    Result result = ServerCall.occupyCar(user, car);

                    if(result.isFlag()){
                        SQLiteDatabase db = Tools.getDB_Writable(context);
                        car.setParked(false);
                        CarTable.updateCar(db, car);
                        db.close();

                        result_code = 4;
                    }
                    else {
                        result_code = 2;
                    }

                }
                else{
                    result_code = 3;
                }

            }
        }).start();
    }

    private void handlerCode(Context context){
        switch (result_code){
            case 1: Tools.createToast(context,"Car parked",Toast.LENGTH_LONG); break;
            case 2: Tools.createToast(context, "Server not available!", Toast.LENGTH_SHORT); break;
            case 3: Tools.createToast(context, "No connection available!", Toast.LENGTH_SHORT); break;
            case 4: Tools.createToast(context,"Car occupied",Toast.LENGTH_LONG); break;

            default: Tools.createToast(context,"Widget error!",Toast.LENGTH_LONG);
        }
    }

}
