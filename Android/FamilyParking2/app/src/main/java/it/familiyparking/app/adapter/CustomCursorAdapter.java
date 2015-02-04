package it.familiyparking.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import it.familiyparking.app.R;
import it.familiyparking.app.utility.Tools;

/**
 * Created by francesco on 30/12/14.
 */
public class CustomCursorAdapter extends CursorAdapter {
    private LayoutInflater layoutInflater;
    private Context context;
    private View.OnClickListener listener;

    public CustomCursorAdapter(Context context, Cursor c, int flags, View.OnClickListener l) {
        super(context, c, flags);
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        listener = l;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return layoutInflater.inflate(R.layout.contact_item, parent, false);
    }


    @Override
    public void bindView(View v, Context context, Cursor c) {

        Button buttonAdd = ((Button) v.findViewById(R.id.add_contact_button_item));
        RelativeLayout relativeContact = ((RelativeLayout) v.findViewById(R.id.contact_rl_item));


        if(c.getInt(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)) != -1){

            relativeContact.setVisibility(View.VISIBLE);
            buttonAdd.setVisibility(View.GONE);

            int index_name = c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
            int index_email = c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA);

            ((TextView) v.findViewById(R.id.contact_name_tv)).setText(c.getString(index_name).trim());
            ((TextView) v.findViewById(R.id.contact_email_tv)).setText(c.getString(index_email).trim());

            Tools.addThumbnail(context, ((ImageView) v.findViewById(R.id.contact_image_iv)), new Integer(c.getInt(c.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_ID))));
        }
        else{
            relativeContact.setVisibility(View.GONE);
            buttonAdd.setVisibility(View.VISIBLE);
            buttonAdd.setClickable(true);
            buttonAdd.setOnClickListener(listener);
        }
    }



}
