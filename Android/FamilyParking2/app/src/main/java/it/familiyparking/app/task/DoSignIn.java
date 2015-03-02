package it.familiyparking.app.task;

import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.widget.Toast;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.fragment.SignIn;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.ServerCall;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class DoSignIn implements Runnable {

    private String name;
    private String email;
    private SignIn fragment;

    public DoSignIn(SignIn fragment, String name, String email) {
        this.name = name;
        this.email = email;
        this.fragment = fragment;
    }

    @Override
    public void run() {
        Looper.prepare();

        User user = new User(name,email);

        final Result result = ServerCall.signIn(user);

        final MainActivity activity = (MainActivity)fragment.getActivity();

        if(result.isFlag()) {
            SQLiteDatabase db = Tools.getDB_Writable(activity);

            String photoID = Tools.getPhotoID_byEmail(activity,user.getEmail());
            if(photoID != null){
                user.setHas_photo(true);
                user.setPhoto_ID(photoID);
            }

            UserTable.insertUser(db,user);
            activity.setUser(user);

            db.close();

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.createToast(activity,"Confirmation code sent to your email!", Toast.LENGTH_SHORT);
                    fragment.endSignIn(false);
                }
            });
        }
        else{
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.createToast(activity,result.getDescription(), Toast.LENGTH_SHORT);
                    fragment.endSignIn(true);
                }
            });
        }
    }
}
