package com.familyparking.app.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.familyparking.app.R;
import com.familyparking.app.dao.DataBaseHelper;
import com.familyparking.app.dao.GroupTable;
import com.familyparking.app.serverClass.Contact;
import com.familyparking.app.utility.Tools;

import java.util.ArrayList;
import java.util.HashSet;

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

    public void add(Contact contact,boolean flag_db) {
        if(!set.contains(contact.getEmail())) {
            set.add(contact.getEmail());
            super.add(contact);

            Tools.createToast(getContext(),"Contact added to group!", Toast.LENGTH_LONG);

            if(flag_db) {
                DataBaseHelper databaseHelper = new DataBaseHelper(getContext());
                final SQLiteDatabase db = databaseHelper.getWritableDatabase();
                GroupTable.insertContact(db,contact);
                db.close();
            }
        }
        else{
            Tools.createToast(getContext(),"Contact already in the group!", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void remove(Contact contact) {
        super.remove(contact);
        set.remove(contact.getEmail());
        DataBaseHelper databaseHelper = new DataBaseHelper(getContext());
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();
        GroupTable.deleteContact(db,contact.getEmail());
        db.close();
    }
}