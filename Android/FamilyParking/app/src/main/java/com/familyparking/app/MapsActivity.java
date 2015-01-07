package com.familyparking.app;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.familyparking.app.adapter.CustomHorizontalAdapter;
import com.familyparking.app.dao.DataBaseHelper;
import com.familyparking.app.dao.GroupTable;
import com.familyparking.app.dialog.ContactDetailDialog;
import com.familyparking.app.dialog.ProgressCircleDialog;
import com.familyparking.app.serverClass.Car;
import com.familyparking.app.serverClass.Contact;
import com.familyparking.app.service.LocationService;
import com.familyparking.app.task.AsyncTaskPosition;
import com.familyparking.app.task.RetrieveGroup;
import com.familyparking.app.utility.Code;
import com.familyparking.app.utility.Tools;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity {

    private GoogleMap googleMap;
    private LocationService locationService;
    private double[] position;
    private int position_attempt = 30;
    private Tracker tr;

    private CustomHorizontalAdapter customHorizontalAdapter;
    private RelativeLayout relativeTwoWay;
    private TwoWayView listGroup;
    private RelativeLayout relativeTwoWayView;

    private ArrayList<Contact> group;

    //This flag is helpful to check if it's the first time that we pass inside onResume or we come back from onPause
    private int retrieve_position_count = 0;
    private boolean retrieve_position_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleAnalytics ga = GoogleAnalytics.getInstance(this);
        tr = ga.newTracker(Code.GOOGLE_ANALYTICS);
        tr.enableAutoActivityTracking(true);
        tr.enableExceptionReporting(true);

        setContentView(R.layout.activity_maps);

        setUpFragmentMap();

        locationService = new LocationService(this);

        position = Tools.getLocationAlert(locationService, this);

        if(position != null){
            setUpMap();
        }
        else{
            Tools.showSettingsAlert(this);
            retrieve_position_flag = true;
            retrieve_position_count++;
        }

        relativeTwoWayView = ((RelativeLayout)findViewById(R.id.group_rl_main_activity));
        listGroup = ((TwoWayView)findViewById(R.id.group_list_main_activity));
        group = new ArrayList<>();
        customHorizontalAdapter = new CustomHorizontalAdapter(this,group);
        listGroup.setAdapter(customHorizontalAdapter);
        listGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactDetailDialog dialog = new ContactDetailDialog(customHorizontalAdapter,position,relativeTwoWayView);
                dialog.show(getFragmentManager(), "");

            }
        });
        (new Thread(new RetrieveGroup(this,relativeTwoWayView,customHorizontalAdapter))).start();

    }

    @Override
    protected void onResume() {
        super.onResume();

        relativeTwoWayView.setVisibility(View.GONE);
        (new Thread(new RetrieveGroup(this,relativeTwoWayView,customHorizontalAdapter))).start();

        //Only if come back from onPause we need to check position
        if((retrieve_position_count > 1) && (retrieve_position_flag)){

            retrieve_position_flag = false;

            boolean forcedClosure = true;

            for (int i = 0; i < position_attempt; i++) {
                if (position == null) {

                    position = Tools.getLocation(locationService, this);

                    if (position != null) {
                        setUpMap();
                        forcedClosure = false;
                        retrieve_position_count = 0;
                        break;
                    }
                }
            }

            if(forcedClosure){
                Tools.showInfoAlert(this);
            }
        }
        else{
            retrieve_position_count++;
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #googleMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpFragmentMap() {
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        }
    }

    private void setUpMap() {
        LatLng position = new LatLng(this.position[0],this.position[1]);
        googleMap.addMarker(new MarkerOptions().position(position).title("My car"));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 18.0f));
    }

    public void sendPosition(View v) {

        tr.send(new HitBuilders.EventBuilder()
                .setCategory("SendigPosition")
                .setAction("Send")
                .setLabel("Ppushed")
                .build());


        if(Tools.isOnline(this)) {
            Bundle bundle = new Bundle();
            bundle.putString("message","Wait ...");
            ProgressCircleDialog dialog = new ProgressCircleDialog();
            dialog.setArguments(bundle);
            dialog.show(getFragmentManager(), "");


            DataBaseHelper databaseHelper = new DataBaseHelper(this);
            final SQLiteDatabase db = databaseHelper.getReadableDatabase();
            String[] email = GroupTable.getEmailGroup(db);

            db.close();

            if (email == null) {
                dialog.dismiss();
                Tools.createToast(this, "Email not sent, group is empty!", Toast.LENGTH_LONG);
            } else {

                if(this.position == null){

                    //Tools.createToast(this, "Looking for your position ...", Toast.LENGTH_LONG);

                    for (int i = 0; i < position_attempt; i++) {
                        if (position == null) {
                            position = Tools.getLocation(locationService, this);

                            if (position != null)  break;
                        }
                    }
                }

                if(this.position == null){
                    dialog.dismiss();
                    Tools.createToast(this, "Your position is not available, try later!", Toast.LENGTH_LONG);
                }
                else {
                    Car car = new Car(this.position, email,Tools.getUniqueDeviceId(this));

                    ArrayList<Object> params = new ArrayList<>();
                    params.add(this);
                    params.add(car);
                    params.add(dialog);

                    new AsyncTaskPosition().execute(params);
                }
            }
        }
        else{
            Tools.createToast(this, "No available connection!", Toast.LENGTH_LONG);
        }
    }

    public void manageGroup(View v) {
        Intent toManageGroup = new Intent(this,ManageGroupActivity.class);
        startActivity(toManageGroup);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
