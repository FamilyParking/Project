package it.familiyparking.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;

import it.familiyparking.app.R;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class CustomHorizontalAdapter_4CarEdit extends ArrayAdapter<User> {

    private HashSet<String> set;
    private Activity activity;

    public CustomHorizontalAdapter_4CarEdit(Activity activity, ArrayList<User> contacts) {
        super(activity.getApplicationContext(), 0, contacts);

        this.activity = activity;

        set = new HashSet<>();
        for(User contact : contacts)
            set.add(contact.getEmail());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_4edit_item, parent, false);

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

        return convertView;
    }

    public void add(User contact,boolean flag_toast) {
        if(!set.contains(contact.getEmail())) {
            set.add(contact.getEmail());
            super.add(contact);

            if(flag_toast)
                Tools.createToast(getContext(),"Contact added to group!", Toast.LENGTH_SHORT);
        }
        else{
            if(flag_toast)
                Tools.createToast(getContext(),"Contact already in the group!", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void remove(User contact) {
        super.remove(contact);
        set.remove(contact.getEmail());
    }
}