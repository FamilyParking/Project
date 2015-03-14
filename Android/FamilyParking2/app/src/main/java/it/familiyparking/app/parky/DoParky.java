package it.familiyparking.app.parky;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.widget.Toast;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.IpoteticPark;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.ServerCall;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class DoParky implements Runnable {

    private Context context;

    public DoParky(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        Looper.prepare();

        if(Tools.isOnline(context)) {

            SQLiteDatabase db = Tools.getDB_Readable(context);
            final User user = UserTable.getUser(db);
            db.close();

            if(user != null) {

                final double[] position = Tools.getPosition(context);

                Result result = ServerCall.isNotification(new IpoteticPark(user, position, Tools.getTimestamp()));

                if((result.isFlag()) && ((boolean) result.getObject())){
                    Tools.sendNotificationForStatics(context);
                }

            }

        }
        else{
            Tools.sendNotificationForStatics(context);
        }
    }

}
