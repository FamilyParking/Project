package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.widget.Toast;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.fragment.Confirmation;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.ServerCall;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class DoConfirmation implements Runnable {

    private String code;
    private User user;
    private Confirmation fragment;

    public DoConfirmation(Confirmation fragment, User user, String code) {
        this.code = code;
        this.user = user;
        this.fragment = fragment;
    }

    @Override
    public void run() {
        Looper.prepare();

        final MainActivity activity = (MainActivity) fragment.getActivity();

        try {
            if (Tools.isOnline(activity)) {

                Tools.isNumber(code);

                user.setCode(code);

                final Result result = ServerCall.confirmation(user);

                if (result.isFlag()) {

                    SQLiteDatabase db = Tools.getDB_Writable(activity);

                    UserTable.updateUser(db, user);

                    db.close();

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Tools.createToast(activity, "Account confirmed!", Toast.LENGTH_SHORT);
                            fragment.endConfirmation(false);
                        }
                    });
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Tools.createToast(activity, "Wrong code. Try again.", Toast.LENGTH_SHORT);
                            fragment.endConfirmation(true);
                        }
                    });
                }
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Tools.createToast(activity, activity.getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT);
                        fragment.endConfirmation(true);
                    }
                });
            }

        }
        catch (NumberFormatException e){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.createToast(activity, "Wrong code. Try again.", Toast.LENGTH_SHORT);
                    fragment.endConfirmation(true);
                }
            });
        }
        catch (Exception e){
            SQLiteDatabase db = Tools.getDB_Writable(activity);
            UserTable.deleteUser(db);
            db.close();

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.createToast(activity, activity.getResources().getString(R.string.exception_confirmation), Toast.LENGTH_SHORT);
                    Tools.closeApp(activity);
                }
            });
        }
    }
}
