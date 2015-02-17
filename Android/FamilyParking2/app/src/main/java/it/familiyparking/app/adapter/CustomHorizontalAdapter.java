package it.familiyparking.app.adapter;

import android.app.Activity;
import android.content.Context;
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
import it.familiyparking.app.serverClass.Contact;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class CustomHorizontalAdapter extends ArrayAdapter<Contact> {

    private HashSet<String> set;
    private Activity activity;

    public CustomHorizontalAdapter(Activity activity, ArrayList<Contact> contacts) {
        super(activity.getApplicationContext(), 0, contacts);

        this.activity = activity;

        set = new HashSet<>();
        for(Contact contact : contacts)
            set.add(contact.getEmail());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Contact contact = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_group_item, parent, false);
        }

        ImageView photo = (ImageView) convertView.findViewById(R.id.group_contact_image_iv);
        TextView textView = (TextView) convertView.findViewById(R.id.group_contact_image_tv);

        if(contact.hasPhoto()) {
            photo.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
            Tools.addThumbnail(getContext(), photo, new Integer(contact.getPhoto_Id()));
        }
        else {
            photo.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            Tools.setImageForContact(activity,textView,contact);
        }

        return convertView;
    }

    public void add(Contact contact,boolean flag_toast) {
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
    public void remove(Contact contact) {
        super.remove(contact);
        set.remove(contact.getEmail());
    }
}