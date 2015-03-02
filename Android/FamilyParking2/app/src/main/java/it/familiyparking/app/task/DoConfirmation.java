package it.familiyparking.app.task;

import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.widget.Toast;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.DataBaseHelper;
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

        user.setCode(code);

        final Result result = ServerCall.confirmation(user);

        final MainActivity activity = (MainActivity) fragment.getActivity();

        if(result.isFlag()) {

            SQLiteDatabase db = Tools.getDB_Writable(activity);

            UserTable.updateUser(db,user);

            db.close();

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.createToast(activity,"Account confirmed!", Toast.LENGTH_SHORT);
                    fragment.endConfirmation(false);
                }
            });
        }
        else{
            Tools.manageServerError(result,activity);
        }
    }
}
