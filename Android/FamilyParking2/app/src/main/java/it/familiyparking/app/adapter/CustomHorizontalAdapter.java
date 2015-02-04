package it.familiyparking.app.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;

import it.familiyparking.app.R;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.GroupTable;
import it.familiyparking.app.serverClass.Contact;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 02/01/15.
 */
public class CustomHorizontalAdapter extends ArrayAdapter<Contact> {

    private HashSet<String> set;

    public CustomHorizontalAdapter(Context context, ArrayList<Contact> contacts) {
        super(context, 0, contacts);

        set = new HashSet<>();
        for(Contact contact : contacts)
            set.add(contact.getEmail());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Contact contact = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.group_item, parent, false);
        }

        ImageView photo = (ImageView) convertView.findViewById(R.id.group_contact_image_iv);

        if(contact.hasPhoto())
            Tools.addThumbnail(getContext(), photo, new Integer(contact.getPhoto_Id()));
        else
            photo.setImageResource(R.drawable.user);

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