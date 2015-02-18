package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.CarGroupRelationTable;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.GroupTable;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.CreateRelationGroupCar;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.ServerCall;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class DoSaveCar implements Runnable {

    private MainActivity activity;
    private Car car;
    private String groupID;

    public DoSaveCar(FragmentActivity activity, Car car, String groupID) {
        this.activity = (MainActivity)activity;
        this.car = car;
        this.groupID = groupID;
    }

    @Override
    public void run() {
        Looper.prepare();

        DataBaseHelper databaseHelper = new DataBaseHelper(activity);
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();


        String timestamp = Tools.getTimestamp();

        final User user = UserTable.getUser(db);
        car.setCode(user.getCode());
        car.setEmail(user.getEmail());

        final Result result = ServerCall.createCar(car);

        if(result.isFlag()) {

            car.setId((String)result.getObject());

            CarTable.insertCar(db, car, timestamp);

            if (groupID != null) {
                CreateRelationGroupCar createRelationGroupCar = new CreateRelationGroupCar(user.getEmail(),user.getCode(),car.getId(),groupID);
                final Result resultCarGroup = ServerCall.insertCarToGroup(createRelationGroupCar);

                if(resultCarGroup.isFlag())
                    CarGroupRelationTable.insertRelation(db, car.getId(), groupID);
                else
                    Log.e("DoSaveCar","Error CarGroupRelation: "+resultCarGroup.getDescription());
            }

            final ArrayList<String> list_groupID = GroupTable.getAllGroup(db);

            db.close();

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.resetProgressDialogCircular(false);
                }
            });

            activity.closeCreateCar();

            if (groupID != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.updateGroupAdapter(list_groupID);
                        Tools.createToast(activity, "Car created and added to group!", Toast.LENGTH_SHORT);
                    }
                });
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Tools.createToast(activity, "Car created!", Toast.LENGTH_SHORT);
                    }
                });
            }
        }
        else{
            Double temp = (Double)result.getObject();

            if(temp.doubleValue() == 3){

                activity.resetAppDB();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.resetProgressDialogCircular(false);
                        Tools.createToast(activity, "Your account is invalid, please signIn!", Toast.LENGTH_SHORT);
                        activity.resetAppGraphic();
                    }
                });
            }
            else{
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.resetProgressDialogCircular(false);
                        Tools.createToast(activity,result.getDescription(), Toast.LENGTH_SHORT);
                    }
                });
            }
        }
    }
}
