package it.familiyparking.app.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.sql.Timestamp;
import java.util.Objects;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.GroupTable;
import it.familiyparking.app.dao.UserTable;
import it.familiyparking.app.fragment.EditCar;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.Result;
import it.familiyparking.app.serverClass.User;


/**
 * Created by francesco on 18/12/14.
 */
public class Tools {

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void showAlertPosition(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setCancelable(false);

        alertDialog.setTitle("Location services disabled");

        alertDialog.setMessage(getAppName(context)+" needs access to your location. Please turn on location access, otherwise the app will be closed.");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Tools.closeApp(context);
                    }
                });

        alertDialog.show();
    }

    public static boolean isPositionHardwareEnable(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        return (gps_enabled || network_enabled);
    }

    public static void showClosedInfoAlert(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setCancelable(false);

        alertDialog.setTitle("Location services disabled");

        alertDialog.setMessage(getAppName(context)+" can not access to your location. The application will be closed!");

        alertDialog.setNegativeButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Tools.closeApp(context);
                    }
                });

        alertDialog.show();
    }

    public static String getAppName(Context context){
        return context.getResources().getString(R.string.app_name);
    }

    public static void closeApp(Context context){
        ((Activity) context).finish();
    }

    public static boolean isCursorEmpty(Cursor cursor){
        return !(cursor.moveToNext());
    }

    public static void addThumbnail(Context context,ImageView photo_iv,Integer photo_id) {

        if (photo_id != null) {
            final Bitmap thumbnail = fetchThumbnail(context,photo_id);

            if (thumbnail != null) {
                photo_iv.setImageBitmap(thumbnail);
            }
            else{
                photo_iv.setImageResource(R.drawable.user);
            }
        }

    }

    private static Bitmap fetchThumbnail(Context context,Integer photo_id) {

        if(photo_id == -1)
            return null;

        Uri uri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photo_id);

        Cursor cursor = context.getContentResolver().query(uri, new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO}, null, null, null);

        try {
            Bitmap thumbnail = null;
            if (cursor.moveToFirst()) {
                byte[] thumbnailBytes = cursor.getBlob(0);
                if (thumbnailBytes != null) {
                    thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
                }
            }

            if(thumbnail != null)
                return getCroppedBitmap(thumbnail,100);
            else
                return thumbnail;
        }
        finally {
            cursor.close();
        }

    }

    private static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;

        if(bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;

        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),sbmp.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final int color = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(sbmp.getWidth() / 2+0.7f, sbmp.getHeight() / 2+0.7f,sbmp.getWidth() / 2+0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }

    public static void createToast(Context context, CharSequence text, int duration){
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public static String getUniqueDeviceId(Activity activity) {
        String id;

        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        id =  telephonyManager.getDeviceId();

        if(id == null){
            WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wInfo = wifiManager.getConnectionInfo();
            id = wInfo.getMacAddress();
        }

        return id;
    }

    public static String getActivtyName(Activity activity) {
        return activity.getClass().getSimpleName();
    }

    public static void setActionBar(ActionBarActivity activity){

        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
            android.app.ActionBar actionBar = activity.getActionBar();
        } else{
            android.support.v7.app.ActionBar actionBar = activity.getSupportActionBar();
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.drawable.logo_actionbar);
            actionBar.setDisplayUseLogoEnabled(true);
        }
    }

    public static void setUpButtonActionBar(ActionBarActivity activity){
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
            android.app.ActionBar actionBar = activity.getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        } else{
            android.support.v7.app.ActionBar actionBar = activity.getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static void resetUpButtonActionBar(ActionBarActivity activity){
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
            android.app.ActionBar actionBar = activity.getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        } else{
            android.support.v7.app.ActionBar actionBar = activity.getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    public static void setTitleActionBar(ActionBarActivity activity,int id){
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
            android.app.ActionBar actionBar = activity.getActionBar();
            actionBar.setTitle(id);
        } else{
            android.support.v7.app.ActionBar actionBar = activity.getSupportActionBar();
            actionBar.setTitle(id);
        }
    }

    public static Tracker activeAnalytic(Context context){
        GoogleAnalytics ga = GoogleAnalytics.getInstance(context);
        Tracker tr = ga.newTracker(Code.GOOGLE_ANALYTICS);
        tr.enableAutoActivityTracking(true);
        tr.enableExceptionReporting(true);
        return tr;
    }

    public static String removeSpace(String input){
        char[] array = input.toCharArray();
        String output = "";

        for(int i=0; i<array.length; i++){
            if(array[i]!=' ')
                output = output + array[i];
        }

        return output;
    }

    public static void closeKeyboard(View v, Activity activity){
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static int getColor(Activity activity, String name){
        int color =  hashFunction(name) % 4;
        switch (color){
            case 0:
                color = activity.getResources().getColor(R.color.purple);
                break;
            case 1:
                color = activity.getResources().getColor(R.color.dark_red);
                break;
            case 2:
                color = activity.getResources().getColor(R.color.orange);
                break;
            case 3:
                color = activity.getResources().getColor(R.color.dark_yellow);
                break;
            default:
                color = activity.getResources().getColor(R.color.sapienza);
        }
        return color;
    }

    private static int hashFunction(String s){
        int hash=7;

        for (int i=0; i < s.length(); i++) {
            hash = hash*31+s.charAt(i);
        }

        return hash;
    }

    /*public static void setImageForGroup(Activity activity, TextView group_image, Group group){
        Drawable drawable = activity.getResources().getDrawable(R.drawable.circle);
        drawable.setColorFilter(new PorterDuffColorFilter(activity.getResources().getColor(R.color.green),PorterDuff.Mode.SCREEN));
        group_image.setBackgroundDrawable(drawable);
        String initial = ""+group.getName().charAt(0)+"";
        group_image.setText(initial.toUpperCase());
    }*/

    public static void setImageForContact(Activity activity, TextView group_image, User contact){
        Drawable drawable = activity.getResources().getDrawable(R.drawable.circle);
        drawable.setColorFilter(new PorterDuffColorFilter(getColor(activity,contact.getName()),PorterDuff.Mode.SCREEN));
        group_image.setBackgroundDrawable(drawable);
        String initial = getInitial(contact.getName());
        group_image.setText(initial.toUpperCase());
    }

    private static String getInitial(String name){
        String[] splited = name.split("\\s+");
        String ret = ""+splited[0].charAt(0);

        for(int i=1; i < splited.length; i++){
            ret = ret + splited[i].charAt(0);
        }

        return ret;
    }

    public static String getBrand(Spinner spinner, Activity activity){
        String[] array = activity.getResources().getStringArray(R.array.car_brands);
        return array[spinner.getSelectedItemPosition()];
    }

    public static int getBrandIndex(String brand, Activity activity){
        String[] array = activity.getResources().getStringArray(R.array.car_brands);

        for(int i=0; i<array.length; i++)
            if(array[i].equals(brand))
                return i;

        return -1;
    }

    public static String getTimestamp(){
        return (new Timestamp((new java.util.Date()).getTime())).toString();
    }

    public static boolean isBluetoothEnable(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if ((mBluetoothAdapter == null) || (!mBluetoothAdapter.isEnabled()))
                return false;

        return true;
    }

    public static void showAlertBluetooth(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setCancelable(false);

        alertDialog.setTitle("Bluetooth disabled");

        alertDialog.setMessage(getAppName(context)+" needs bluetooth to join the car's device. Please turn it on.");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                        context.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public static void invalidDB(MainActivity activity){
        DataBaseHelper databaseHelper = new DataBaseHelper(activity);
        final SQLiteDatabase db = databaseHelper.getReadableDatabase();

        UserTable.deleteUser(db);
        GroupTable.deleteGroupTable(db);
        CarTable.deleteCarTable(db);

        db.close();
    }

    public static void showAlertBluetoothJoin(final Context context, final EditCar fragment, final String bluetooth_name, final String bluetooth_mac, final Car car, final Button button) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setCancelable(false);

        alertDialog.setTitle("Join bluetooth device");

        if(car.getName() == null)
            alertDialog.setMessage("Do you want link \n"+bluetooth_name+" ("+bluetooth_mac+") ?");
        else
            alertDialog.setMessage("Do you want link \n"+bluetooth_name+" ("+bluetooth_mac+") \n to "+car.getName()+" ?");

        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        fragment.linkBluetoothDevice(bluetooth_name, bluetooth_mac);
                        dialog.cancel();
                    }
                });

        alertDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public static void showAlertBluetoothRemove(final Context context, final EditCar fragment, final Car car) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setCancelable(false);

        alertDialog.setTitle("Disconnect bluetooth device");

        alertDialog.setMessage("Do you want unlink \n "+car.getBluetoothName()+" ("+car.getBluetoothMac()+") \n from "+car.getName()+"?");

        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        fragment.unlinkBluetoothDevice();
                        dialog.cancel();
                    }
                });

        alertDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public static double[] getPoistion(LocationService locationService){
        double[] position = null;

        while (position == null) {
            position = getLocationGPS(locationService);

            if (position == null) {
                position = getLocationNetwork(locationService);
            }
        }

        return position;
    }

    private static double[] getLocationGPS(LocationService locationService){
        Location gpsLocation = locationService.getLocation(LocationManager.GPS_PROVIDER);

        if (gpsLocation != null) {
            double[] position = {gpsLocation.getLatitude(),gpsLocation.getLongitude()};

            return position;
        }

        return null;
    }

    private static double[] getLocationNetwork(LocationService locationService){
        Location nwLocation = locationService.getLocation(LocationManager.NETWORK_PROVIDER);

        if (nwLocation != null) {
            double[] position = {nwLocation.getLatitude(),nwLocation.getLongitude()};

            return position;
        }

        return null;
    }

    public static void sendNotification(Context context, String name, String type){

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);

            notificationBuilder.setContentTitle(getAppName(context));

            String note = "";
            if (type == Code.TYPE_GROUP) {
                note = "Updated group " + name;
            } else if (type == Code.TYPE_PARK) {
                note = name+" parked!";
            }

            notificationBuilder.setContentText(note);

            notificationBuilder.setTicker(note);
            notificationBuilder.setWhen(System.currentTimeMillis());
            notificationBuilder.setSmallIcon(R.drawable.ic_notification);
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.logo));

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
            notificationBuilder.setContentIntent(contentIntent);

            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

            mNotificationManager.notify(0, notificationBuilder.build());
    }

    public static SQLiteDatabase getDB_Readable(Context context){
        DataBaseHelper databaseHelper = new DataBaseHelper(context);
         return databaseHelper.getReadableDatabase();
    }

    public static SQLiteDatabase getDB_Writable(Context context){
        DataBaseHelper databaseHelper = new DataBaseHelper(context);
        return databaseHelper.getWritableDatabase();
    }

    public static String getPhotoID_byEmail(Context context, String email){
        ContentResolver resolver = context.getContentResolver();
        Cursor c = resolver.query(ContactsContract.Data.CONTENT_URI, new String[]{ContactsContract.Contacts.PHOTO_ID}, ContactsContract.CommonDataKinds.Email.DATA + "= ?", new String[]{email}, null);

        if(c.moveToNext())
            return c.getString(0);

        return null;
    }

    public static void manageServerError(final Result result, final MainActivity activity){
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
