package it.familiyparking.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.dialog.ContactDetailDialog;
import it.familiyparking.app.dialog.ProgressDialogCircular;
import it.familiyparking.app.fragment.GroupFragment;
import it.familiyparking.app.fragment.ManageGroup;
import it.familiyparking.app.serverClass.Contact;
import it.familiyparking.app.serverClass.Group;
import it.familiyparking.app.task.DoRemoveGroup;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class CustomAdapterCarBrand extends ArrayAdapter<String> {

    private MainActivity activity;

    public CustomAdapterCarBrand(Activity activity) {
        super(activity.getApplicationContext(), 0, activity.getResources().getStringArray(R.array.car_brands));
        this.activity = (MainActivity) activity;
    }

    @Override public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
        return getCustomView(position, cnvtView, prnt);
    }

    @Override public View getView(int pos, View cnvtView, ViewGroup prnt) {
        return getCustomView(pos, cnvtView, prnt);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.car_brand_item, parent, false);
        }

        ImageView brand_icon = (ImageView) convertView.findViewById(R.id.car_logo_item_iv);
        brand_icon.setBackgroundDrawable(activity.getResources().getDrawable(activity.getResources().getIdentifier(getItem(position),"drawable",activity.getPackageName())));

        return convertView;
    }

}