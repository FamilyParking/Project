package com.familyparking.app;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.familyparking.app.adapter.CustomHorizontalAdapter;
import com.familyparking.app.serverClass.Contact;
import com.familyparking.app.task.RetrieveGroup;
import com.familyparking.app.adapter.CustomCursorAdapter;
import com.familyparking.app.utility.Tools;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;


/**
 * Created by francesco on 17/03/14.
 */

public class ManageGroupActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>, TextWatcher, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private String[] selectionArgs = new String[5];
    private String searchString;

    private LoaderManager loaderManager;

    private RelativeLayout relativeContact;
    private ListView listContact;
    private CustomCursorAdapter customCursorAdapter;

    private ArrayList<Contact> group;
    private CustomHorizontalAdapter customHorizontalAdapter;
    private TwoWayView listGroup;
    private RelativeLayout relativeTwoWayView;

    public ManageGroupActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_group);

        group = new ArrayList<>();
        customHorizontalAdapter = new CustomHorizontalAdapter(this,group);
        listGroup = ((TwoWayView)findViewById(R.id.group_list));
        listGroup.setAdapter(customHorizontalAdapter);
        listGroup.setOnItemLongClickListener(this);
        relativeTwoWayView = ((RelativeLayout)findViewById(R.id.group_rl));

        (new Thread(new RetrieveGroup(this,relativeTwoWayView,customHorizontalAdapter))).start();

        ((EditText)findViewById(R.id.find_contact_edt)).addTextChangedListener(this);
        relativeContact = ((RelativeLayout)findViewById(R.id.contact_rl));
        listContact = ((ListView)findViewById(R.id.contact_lv));

        customCursorAdapter = new CustomCursorAdapter(this,null,0);
        listContact.setAdapter(customCursorAdapter);
        listContact.setOnItemClickListener(this);

        loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        selectionArgs[0] = "%" + searchString + "%";
        selectionArgs[1] = "%" + searchString + "%";
        selectionArgs[2] = "%@%";
        selectionArgs[3] = "%@%";
        selectionArgs[4] = "%whatsapp%";

        final String[] PROJECTION ={ContactsContract.Contacts._ID,ContactsContract.Contacts.LOOKUP_KEY,ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.CommonDataKinds.Email.DATA,ContactsContract.Contacts.PHOTO_ID};
        final String SELECTION = "( "+ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ? OR " +
                                 ContactsContract.CommonDataKinds.Email.DATA + " LIKE ? ) AND " +
                                 ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " NOT LIKE ? AND " +
                                 ContactsContract.CommonDataKinds.Email.DATA + " LIKE ? AND " +
                                 ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ?";
        final String SORT = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;

        return new CursorLoader(this,ContactsContract.Data.CONTENT_URI,PROJECTION,SELECTION,selectionArgs,SORT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(!relativeContact.isShown() && !Tools.isCursorEmpty(data))
            relativeContact.setVisibility(View.VISIBLE);
        else if(Tools.isCursorEmpty(data))
            relativeContact.setVisibility(View.GONE);

        customCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        customCursorAdapter.swapCursor(null);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        searchString = s.toString();
        loaderManager.restartLoader(0, null, this);
    }

    public void closeActivity(View v) {
        this.finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = customCursorAdapter.getCursor();
        cursor.moveToPosition(position);

        int contact_id = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
        int photo_id = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_ID));
        String email = ((TextView) view.findViewById(R.id.contact_email_tv)).getText().toString();
        boolean photo_flag = false;

        if(photo_id != 0)
            photo_flag = true;

        customHorizontalAdapter.add(new Contact(contact_id,photo_id,email,photo_flag),true);
        customHorizontalAdapter.notifyDataSetChanged();

        if(relativeTwoWayView.getVisibility() == View.GONE)
            relativeTwoWayView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final ImageView photo = ((ImageView) view.findViewById(R.id.group_contact_image_iv));
        photo.setImageResource(android.R.drawable.ic_menu_delete);

        photo.setClickable(true);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customHorizontalAdapter.remove(customHorizontalAdapter.getItem(position));
                customHorizontalAdapter.notifyDataSetChanged();
                photo.setClickable(false);

                if((customHorizontalAdapter.isEmpty())&&(relativeTwoWayView.getVisibility() == View.VISIBLE))
                    relativeTwoWayView.setVisibility(View.GONE);
            }
        });
        return false;
    }
}