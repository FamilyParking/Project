package it.familiyparking.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.familiyparking.app.R;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class CustomHorizontalAdapter_4CarItem extends ArrayAdapter<User> {

    private Activity activity;

    public CustomHorizontalAdapter_4CarItem(Activity activity, ArrayList<User> contacts) {
        super(activity.getApplicationContext(), 0, contacts);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_4car_item, parent, false);
        }

        setData(position, convertView);

        return convertView;
    }

    private void setData(int position, View convertView){
        User contact = getItem(position);

        ImageView photo = (ImageView) convertView.findViewById(R.id.group_contact_image_iv);
        TextView textView = (TextView) convertView.findViewById(R.id.group_contact_image_tv);

        if(contact.has_photo()) {
            photo.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
            Tools.addThumbnail(getContext(), photo, new Integer(contact.getPhoto_ID()));
        }
        else {
            photo.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            Tools.setImageForContact(activity,textView,contact);
        }
    }
}