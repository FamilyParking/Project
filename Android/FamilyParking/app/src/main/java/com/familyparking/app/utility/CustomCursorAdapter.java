package com.familyparking.app.utility;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.familyparking.app.R;

/**
 * Created by francesco on 30/12/14.
 */
public class CustomCursorAdapter extends CursorAdapter {
    private LayoutInflater layoutInflater;
    private Context context;

    public CustomCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return layoutInflater.inflate(R.layout.contact_item, parent, false);
    }


    @Override
    public void bindView(View v, Context context, Cursor c) {
        int index = c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
        String name = c.getString(index).trim();
        ((TextView) v.findViewById(R.id.contact_name_tv)).setText(name);

        Tools.addThumbnail(context,((ImageView) v.findViewById(R.id.contact_image_iv)), new Integer(c.getInt(c.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_ID))));
    }



}
