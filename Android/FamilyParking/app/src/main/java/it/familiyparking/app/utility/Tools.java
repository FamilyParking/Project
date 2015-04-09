package it.familiyparking.app.utility;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
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
import android.os.Process;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.adapter.CustomAdapterCarDialog;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.fragment.EditCar;
import it.familiyparking.app.parky.ServiceStatistic;
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

    public static AlertDialog showAlertPosition(final Context context) {
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

        AlertDialog dialog = alertDialog.create();
        dialog.show();

        return dialog;
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

    public static void addThumbnail(Context context,ImageView image, TextView text, User contact) {

        boolean setText = true;

        if (contact.has_photo()) {
            final Bitmap thumbnail = fetchThumbnail(context,Integer.valueOf(contact.getPhoto_ID()));

            if (thumbnail != null) {
                image.setImageBitmap(thumbnail);
                image.setVisibility(View.VISIBLE);
                text.setVisibility(View.GONE);
                setText = false;
            }
        }

        if(setText){
            Tools.setImageForContact(context,text,contact.getName());
            image.setVisibility(View.GONE);
            text.setVisibility(View.VISIBLE);
        }

    }

    public static void addThumbnail(Context context,ImageView image, TextView text, String name, Integer photo_id) {

        boolean setText = true;

        if (photo_id != null) {
            final Bitmap thumbnail = fetchThumbnail(context,photo_id);

            if (thumbnail != null) {
                image.setImageBitmap(thumbnail);
                image.setVisibility(View.VISIBLE);
                text.setVisibility(View.GONE);
                setText = false;
            }
        }

        if(setText){
            Tools.setImageForContact(context,text,name);
            image.setVisibility(View.GONE);
            text.setVisibility(View.VISIBLE);
        }

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

        if(photo_id.intValue() < 0)
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
            if(actionBar != null) {
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setLogo(R.drawable.logo_actionbar);
                actionBar.setDisplayUseLogoEnabled(true);
            }
        }
    }

    public static void resetTabActionBar(ActionBarActivity activity){
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
            android.app.ActionBar actionBar = activity.getActionBar();
            if(actionBar != null)
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        } else{
            android.support.v7.app.ActionBar actionBar = activity.getSupportActionBar();
            if(actionBar != null)
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }
    }

    public static void setUpButtonActionBar(ActionBarActivity activity){
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
            android.app.ActionBar actionBar = activity.getActionBar();
            if(actionBar != null)
                actionBar.setDisplayHomeAsUpEnabled(true);
        } else{
            android.support.v7.app.ActionBar actionBar = activity.getSupportActionBar();
            if(actionBar != null)
                actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static void resetUpButtonActionBar(ActionBarActivity activity){
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
            android.app.ActionBar actionBar = activity.getActionBar();
            if(actionBar != null)
                actionBar.setDisplayHomeAsUpEnabled(false);
        } else{
            android.support.v7.app.ActionBar actionBar = activity.getSupportActionBar();
            if(actionBar != null)
                actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    public static void setTitleActionBar(ActionBarActivity activity,int id){
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
            android.app.ActionBar actionBar = activity.getActionBar();
            if(actionBar != null)
                actionBar.setTitle(id);
        } else{
            android.support.v7.app.ActionBar actionBar = activity.getSupportActionBar();
            if(actionBar != null)
                actionBar.setTitle(id);
        }
    }

    public static void setTitleActionBar(ActionBarActivity activity,String title){
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
            android.app.ActionBar actionBar = activity.getActionBar();
            if(actionBar != null)
                actionBar.setTitle(title);
        } else{
            android.support.v7.app.ActionBar actionBar = activity.getSupportActionBar();
            if(actionBar != null)
                actionBar.setTitle(title);
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
        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void closeKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static int getColor(Context context, String name){
        int color =  hashFunction(name) % 4;
        switch (color){
            case 0:
                color = context.getResources().getColor(R.color.purple);
                break;
            case 1:
                color = context.getResources().getColor(R.color.dark_red);
                break;
            case 2:
                color = context.getResources().getColor(R.color.orange);
                break;
            case 3:
                color = context.getResources().getColor(R.color.dark_yellow);
                break;
            default:
                color = context.getResources().getColor(R.color.sapienza);
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

    public static void setImageForContact(Activity activity, TextView group_image, User contact){
        Drawable drawable = activity.getResources().getDrawable(R.drawable.circle);
        drawable.setColorFilter(new PorterDuffColorFilter(getColor(activity,contact.getName()),PorterDuff.Mode.SCREEN));
        group_image.setBackgroundDrawable(drawable);
        String initial = getInitial(contact.getName());
        group_image.setText(initial.toUpperCase());
    }

    public static void setImageForContact(Context context, TextView group_image, String name){
        Drawable drawable = context.getResources().getDrawable(R.drawable.circle);
        drawable.setColorFilter(new PorterDuffColorFilter(getColor(context,name),PorterDuff.Mode.SCREEN));
        group_image.setBackgroundDrawable(drawable);
        String initial = getInitial(name);

        if(initial.length() > 2)
            group_image.setTextSize(20.0f);

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

    public static void invalidDB(Context context){
        DataBaseHelper databaseHelper = new DataBaseHelper(context);
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();
        databaseHelper.invalidateDB(db);
        db.close();
    }

    public static void showAlertBluetoothJoin(final Context context, final EditCar fragment, final String bluetooth_name, final String bluetooth_mac, final Car car, final Button button) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setCancelable(false);

        alertDialog.setTitle("Join bluetooth device");

        if(car.getName() == null)
            alertDialog.setMessage("Do you want to link \n"+bluetooth_name+" ("+bluetooth_mac+") ?");
        else
            alertDialog.setMessage("Do you want to link \n"+bluetooth_name+" ("+bluetooth_mac+") \n to "+car.getName()+" ?");

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

        if(car.getName() != null)
            alertDialog.setMessage("Do you want to unlink \n"+car.getBluetoothName()+" ("+car.getBluetoothMac()+")\n"+"from "+car.getName()+"?");
        else
            alertDialog.setMessage("Do you want to unlink \n"+car.getBluetoothName()+" ("+car.getBluetoothMac()+")?");

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

    public static double[] getPosition(Context context){
        LocationService locationService = new LocationService(context);

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

    public static int getNotificationID(){
        Date now = new Date();
        String format = new SimpleDateFormat("ddHHmmss").format(now);
        return Integer.parseInt(format);
    }

    public static void sendNotification(Context context, String message, String car_id){
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);

        notificationBuilder.setContentTitle(getAppName(context));
        notificationBuilder.setContentText(message);

        notificationBuilder.setContentText(message);

        notificationBuilder.setTicker(message);
        notificationBuilder.setWhen(System.currentTimeMillis());
        notificationBuilder.setSmallIcon(R.drawable.ic_notification);
        notificationBuilder.setColor(context.getResources().getColor(R.color.green));

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("car_id",car_id);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(contentIntent);

        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

        mNotificationManager.notify(0, notificationBuilder.build());
    }

    public static void sendNotificationForStatics(Context context, int notification_ID){
        String message = "Did you park the car?";

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);

        notificationBuilder.setContentTitle(getAppName(context));
        notificationBuilder.setContentText(message);

        notificationBuilder.setWhen(System.currentTimeMillis());
        notificationBuilder.setSmallIcon(R.drawable.ic_notification);
        notificationBuilder.setColor(context.getResources().getColor(R.color.green));

        Intent dismissIntent = new Intent(context, ServiceStatistic.class);
        dismissIntent.setAction(Code.ACTION_DISCARD);
        dismissIntent.putExtra("notification_ID", notification_ID);
        PendingIntent discardPending = PendingIntent.getService(context, 0, dismissIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent acceptIntent = new Intent(context, ServiceStatistic.class);
        acceptIntent.setAction(Code.ACTION_SAVE);
        acceptIntent.putExtra("notification_ID", notification_ID);
        PendingIntent savePending = PendingIntent.getService(context, 0, acceptIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        notificationBuilder.setContentIntent(savePending);

        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(message))
                .addAction (R.drawable.ic_cancel, "No", discardPending)
                .addAction(R.drawable.ic_check, "Yes", savePending);

        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

        Notification notification = notificationBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        mNotificationManager.notify(notification_ID, notification);
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

        String id = null;
        while(c.moveToNext()) {
            id = c.getString(0);
            if(id != null)
                break;
        }

        c.close();

        return id;
    }

    public static String getName_byEmail(Context context, String email){
        ContentResolver resolver = context.getContentResolver();
        Cursor c = resolver.query(ContactsContract.Data.CONTENT_URI, new String[]{ContactsContract.Contacts.DISPLAY_NAME}, ContactsContract.CommonDataKinds.Email.DATA + "= ?", new String[]{email}, null);

        String name = null;
        while(c.moveToNext()) {
            name = c.getString(0);
            if((name != null) && (!name.contains("@")))
                break;
        }

        c.close();

        return name;
    }

    public static void manageServerError(final Result result, final MainActivity activity){

        Double temp = (Double)result.getObject();

        if((temp.doubleValue() == 3) || (temp.doubleValue() == 2)){

            invalidDB(activity);

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

    public static String getIntervalDataServer(String data){

        String[] mex = data.split(" ");
        String[] first = mex[0].split("-");
        String[] second = mex[1].split(":");
        String third = second[2].substring(0,second.length-1);

        DateTime myBirthDate = new DateTime(Integer.parseInt(first[0]), Integer.parseInt(first[1]), Integer.parseInt(first[2]), Integer.parseInt(second[0]), Integer.parseInt(second[1]), Integer.parseInt(third), 0);
        DateTime now = new DateTime();
        Period period = new Period(myBirthDate, now);

        PeriodFormatter formatter = new PeriodFormatterBuilder().appendYears().appendSuffix("years").printZeroNever().toFormatter();
        String temp = formatter.print(period);
        if(temp==""){
            formatter = new PeriodFormatterBuilder().appendMonths().appendSuffix("months").printZeroNever().toFormatter();
            temp = formatter.print(period);
            if(temp==""){
                formatter = new PeriodFormatterBuilder().appendWeeks().appendSuffix("weeks").printZeroNever().toFormatter();
                temp = formatter.print(period);
                if(temp==""){
                    formatter = new PeriodFormatterBuilder().appendDays().appendSuffix("days").printZeroNever().toFormatter();
                    temp = formatter.print(period);
                    if(temp==""){
                        formatter = new PeriodFormatterBuilder().appendHours().appendSuffix("hours").printZeroNever().toFormatter();
                        temp = formatter.print(period);
                        if(temp==""){
                            formatter = new PeriodFormatterBuilder().appendMinutes().appendSuffix("min").printZeroNever().toFormatter();
                            temp = formatter.print(period);
                            if(temp==""){
                                formatter = new PeriodFormatterBuilder().appendSeconds().appendSuffix("sec").printZeroNever().toFormatter();
                                temp = formatter.print(period);
                                if(temp==""){
                                    temp = "Now";
                                }
                            }
                        }
                    }
                }
            }
        }

        return temp;
    }

    public static AlertDialog showAlertParking(final Activity activity, ArrayList<Car> cars, User user, final boolean destroy, String notificationID){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        alertDialog.setCancelable(true);

        alertDialog.setTitle("Pick the car");

        alertDialog.setAdapter(new CustomAdapterCarDialog(activity,cars,user,destroy,notificationID),null);

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialogClass = alertDialog.create();

        alertDialogClass.show();

        return alertDialogClass;
    }

    public static String getFormatedData(String allInfo){
        String[] starter = allInfo.split(" ");
        String[] hour = starter[1].split(":");
        String[] day = starter[0].split("-");

        return hour[0]+":"+hour[1]+"\t"+day[2]+" "+new DateFormatSymbols(Locale.ENGLISH).getMonths()[Integer.parseInt(day[1])-1]+" "+day[0];
    }

    public static String[] getDataTime(String data){

        String[] all = data.split(" ");
        String[] time = all[1].split(":");

        return new String[]{ all[0],time[0]+":"+time[1]};
    }

    public static boolean isAppRunning(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task_list = activityManager.getRunningTasks(Integer.MAX_VALUE);

        String pathPackage = "ComponentInfo{"+context.getPackageName()+"/"+context.getPackageName();
        String mainActivityPackage = pathPackage+".MainActivity}";
        String statisticActivityPackage = pathPackage+".StatisticActivity}";

        for(ActivityManager.RunningTaskInfo task : task_list){

            if(task.topActivity.toString().equals(mainActivityPackage))  return true;
            else if(task.baseActivity.toString().equals(mainActivityPackage))  return true;

            else if(task.topActivity.toString().equals(statisticActivityPackage))  return true;
            else if(task.baseActivity.toString().equals(statisticActivityPackage))  return true;

        }

        return false;
    }

    public static void closeServiceStatistic(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

                Iterator<ActivityManager.RunningAppProcessInfo> i = runningAppProcesses.iterator();

                while(i.hasNext()){
                    ActivityManager.RunningAppProcessInfo next = i.next();

                    String pricessName = context.getPackageName() + ":statistics";

                    if(next.processName.equals(pricessName)){
                        Process.killProcess(next.pid);
                        break;
                    }
                }
            }
        }).start();
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static void isNumber(String string) throws NumberFormatException{
        Integer.parseInt(string);
    }

    public static int convertMarkerColor(float color){
        if(color == BitmapDescriptorFactory.HUE_BLUE)
            return Color.BLUE;
        else if(color == BitmapDescriptorFactory.HUE_CYAN)
            return Color.CYAN;
        else if(color == BitmapDescriptorFactory.HUE_GREEN)
            return Color.GREEN;
        else if(color == BitmapDescriptorFactory.HUE_MAGENTA)
            return Color.MAGENTA;
        else if(color == BitmapDescriptorFactory.HUE_RED)
            return Color.RED;
        else if(color == BitmapDescriptorFactory.HUE_YELLOW)
            return Color.YELLOW;

        return -1;
    }

    public static ArrayList<Float> getMarkerColor(){
        ArrayList<Float> colors = new ArrayList<>();

        colors.add(BitmapDescriptorFactory.HUE_BLUE);
        colors.add(BitmapDescriptorFactory.HUE_CYAN);
        colors.add(BitmapDescriptorFactory.HUE_GREEN);
        colors.add(BitmapDescriptorFactory.HUE_MAGENTA);
        colors.add(BitmapDescriptorFactory.HUE_RED);
        colors.add(BitmapDescriptorFactory.HUE_YELLOW);

        return colors;
    }

}
