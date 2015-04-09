package it.familiyparking.app.parky;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.NotifiedTable;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.Code;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 13/02/15.
 */

public class ServiceStatistic extends IntentService {

    protected void onHandleIntent(Intent intent) {
        if((intent != null) && (intent.getAction() != null)) {

            int ID = intent.getIntExtra("notification_ID", -1);

            if(intent.getAction().equals(Code.ACTION_SAVE)){

                SQLiteDatabase db = Tools.getDB_Readable(this);
                User user = UserTable.getUser(db);
                ArrayList<Car> cars = CarTable.getAllCar(db);
                ArrayList<Notified> notifies = NotifiedTable.getAllNotified(db);
                db.close();

                if((cars.size() > 1) || (notifies.size() > 1)){
                    Intent activity = new Intent(getBaseContext(),StatisticActivity.class);
                    activity.putExtra("from",Code.SERVICE);
                    activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(activity);
                }
                else{
                    new Thread(new DoParkByStatisticActivity(null,this,Integer.toString(ID),user,cars.get(0))).start();
                }

            }
            else if(intent.getAction().equals(Code.ACTION_DISCARD)){
                Tools.closeServiceStatistic(this);
            }

            if(ID != -1)
                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(ID);
        }

    }

    public ServiceStatistic(){
        super("ServiceStatistic");
    }


}