package it.familiyparking.app.parky;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import it.familiyparking.app.dao.NotifiedTable;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.serverClass.IpoteticPark;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.Code;
import it.familiyparking.app.utility.ServerCall;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class DoParky implements Runnable {

    private Context context;
    private Notified notified;

    public DoParky(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        Looper.prepare();

        if(Tools.isPositionHardwareEnable(context)){

            notified = new Notified(context);

            try {

                boolean done = false;

                for(int i=0; i<Code.TRIALS; i++) {

                    if (Tools.isOnline(context)) {

                        SQLiteDatabase db = Tools.getDB_Readable(context);
                        final User user = UserTable.getUser(db);
                        db.close();

                        if (user != null) {
                            Result result = ServerCall.isNotification(new IpoteticPark(user, notified.getPosition(), notified.getTimestamp()));

                            if ((result.isFlag()) && (((Boolean) result.getObject()).booleanValue())) {
                                sendNotification();
                            }

                            break;
                        }

                        done = true;

                    }
                    else {
                        Thread.sleep(100);
                    }

                }

                if(!done)
                    sendNotification();

            }
            catch (Exception e){}
        }
    }

    private void sendNotification(){
        SQLiteDatabase db = Tools.getDB_Writable(context);
        NotifiedTable.insertNotified(db,notified);
        db.close();

        Tools.sendNotificationForStatics(context,Integer.parseInt(notified.getId()));
    }

}
