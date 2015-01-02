package com.familyparking.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.familyparking.app.utility.Code;
import com.familyparking.app.utility.CustomCursorAdapter;
import com.familyparking.app.utility.Tools;

import java.util.HashSet;
import java.util.Set;


/**
 * Created by francesco on 17/03/14.
 */

public class ManageGroupActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>, TextWatcher,AdapterView.OnItemClickListener {

    private String[] selectionArgs = new String[5];
    private String searchString;

    private LoaderManager loaderManager;

    private RelativeLayout relativeContact;
    private ListView listContact;
    private CustomCursorAdapter customCursorAdapter;

    public ManageGroupActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_group);

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
        SharedPreferences settings = getSharedPreferences(Code.PREFS_NAME, 0);
        Set<String> email_list = settings.getStringSet("email_list", null);

        int size = 0;

        Cursor cursor = customCursorAdapter.getCursor();
        cursor.moveToPosition(position);
        int photo_id = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_ID));

        String email = ((TextView) view.findViewById(R.id.contact_email_tv)).getText().toString();

        if(email_list == null) {
            email_list = new HashSet<>();
        }
        else {
            size = email_list.size();
            email_list.add(photo_id + "-" + email);
        }

        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet("email_list", email_list);
        editor.commit();

        if(email_list.size() > size) {
            ImageView photo = new ImageView(this);

            int dimension = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50, getResources().getDisplayMetrics());
            RelativeLayout.LayoutParams paramsImage = new RelativeLayout.LayoutParams(dimension,dimension);
            photo.setLayoutParams(paramsImage);

            if(photo_id == 0)
                photo.setImageResource(R.drawable.user);
            else
                Tools.addThumbnail(this, photo, photo_id);

            ((LinearLayout) findViewById(R.id.group_ll)).addView(photo);

            (findViewById(R.id.group_rl)).setVisibility(View.VISIBLE);
        }
    }
}