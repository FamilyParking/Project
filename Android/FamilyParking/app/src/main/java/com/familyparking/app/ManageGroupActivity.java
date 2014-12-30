package com.familyparking.app;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.familyparking.app.utility.CustomCursorAdapter;
import com.familyparking.app.utility.Tools;


/**
 * Created by francesco on 17/03/14.
 */

public class ManageGroupActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>, TextWatcher{

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
        Log.e("onLoadFinished", DatabaseUtils.dumpCursorToString(data));

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
        loaderManager.restartLoader(0,null,this);
    }

    public void closeActivity(View v) {
        this.finish();
    }
}