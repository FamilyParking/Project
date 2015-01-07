package com.familyparking.app.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.FragmentManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.familyparking.app.R;
import com.familyparking.app.dialog.ContactDetailDialog;
import com.familyparking.app.serverClass.Contact;
import com.familyparking.app.service.LocationService;

/**
 * Created by francesco on 18/12/14.
 */
public class Tools {

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private static double[] getLocationGPS(LocationService locationService,Context context){
        Location gpsLocation = locationService.getLocation(LocationManager.GPS_PROVIDER);

        if (gpsLocation != null) {
            double[] position = {gpsLocation.getLatitude(),gpsLocation.getLongitude()};

            return position;
        }

        return null;
    }

    private static double[] getLocationNetwork(LocationService locationService,Context context){
        Location nwLocation = locationService.getLocation(LocationManager.NETWORK_PROVIDER);

        if (nwLocation != null) {
            double[] position = {nwLocation.getLatitude(),nwLocation.getLongitude()};

            return position;
        }

        return null;
    }

    public static void showSettingsAlert(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setCancelable(false);

        alertDialog.setTitle("Location services disabled");

        alertDialog.setMessage(getAppName(context)+" needs access to your location. Please turn on location access.");

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

    public static void showInfoAlert(final Context context) {
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

    public static double[] getLocationAlert(LocationService locationService,Context context){
        double[] position;

        position = getLocationGPS(locationService, context);

        if(position == null) {
            position = getLocationNetwork(locationService, context);
        }

        return position;
    }

    public static double[] getLocation(LocationService locationService,Context context){
        double[] position;

        position = getLocationGPS(locationService, context);

        if(position == null) {
            position = getLocationNetwork(locationService, context);
        }

        return position;
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
                photo_iv.setImageResource(R.drawable.userw);
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

    public static void createContactDetailDialog(Contact contact,FragmentManager fragmentManager){
        Bundle bundle = new Bundle();
        bundle.putParcelable("contact",contact);
        ContactDetailDialog dialog = new ContactDetailDialog();
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, "");
    }
}
