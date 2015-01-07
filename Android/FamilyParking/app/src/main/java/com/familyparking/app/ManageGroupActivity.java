package com.familyparking.app;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
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
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.familyparking.app.adapter.CustomCursorAdapter;
import com.familyparking.app.adapter.CustomHorizontalAdapter;
import com.familyparking.app.dialog.ContactDetailDialog;
import com.familyparking.app.serverClass.Contact;
import com.familyparking.app.task.RetrieveGroup;
import com.familyparking.app.utility.Tools;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;


/**
 * Created by francesco on 17/03/14.
 */

public class ManageGroupActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>, TextWatcher, AdapterView.OnItemClickListener {

    final private String[] PROJECTION ={ContactsContract.Contacts._ID,ContactsContract.Contacts.LOOKUP_KEY,ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Email.DATA,ContactsContract.Contacts.PHOTO_ID};

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

    private EditText editText;

    private boolean addButton;
    private boolean resetText;

    public ManageGroupActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_group);

        group = new ArrayList<>();
        customHorizontalAdapter = new CustomHorizontalAdapter(this,group);
        listGroup = ((TwoWayView)findViewById(R.id.group_list));
        listGroup.setAdapter(customHorizontalAdapter);

        listGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showContactDetail(position);
            }
        });

        relativeTwoWayView = ((RelativeLayout)findViewById(R.id.group_rl));

        (new Thread(new RetrieveGroup(this,relativeTwoWayView,customHorizontalAdapter))).start();

        editText = ((EditText)findViewById(R.id.find_contact_edt));
        editText.addTextChangedListener(this);
        relativeContact = ((RelativeLayout)findViewById(R.id.contact_rl));
        listContact = ((ListView)findViewById(R.id.contact_lv));

        customCursorAdapter = new CustomCursorAdapter(this,null,0);
        listContact.setAdapter(customCursorAdapter);
        listContact.setOnItemClickListener(this);

        addButton = false;
        resetText = false;

        loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(0, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        (new Thread(new RetrieveGroup(this,relativeTwoWayView,customHorizontalAdapter))).start();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        selectionArgs[0] = "%" + searchString + "%";
        selectionArgs[1] = "%" + searchString + "%";
        selectionArgs[2] = "%@%";
        selectionArgs[3] = "%@%";
        selectionArgs[4] = "%whatsapp%";

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
        if(addButton){
            MatrixCursor extras = new MatrixCursor(PROJECTION);
            extras.addRow(new String[] {"-1","-1","-1","-1","-1"});
            Cursor[] cursors = { data,extras };
            Cursor extendedCursor = new MergeCursor(cursors);
            data = extendedCursor;
        }

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
        if(resetText){
            resetText = false;
        }
        else {
            searchString = s.toString();

            if(searchString.equals("")) {
                relativeContact.setVisibility(View.GONE);
            }
            else {
                loaderManager.restartLoader(0, null, this);

                if (searchString.contains("@"))
                    addButton = true;
                else
                    addButton = false;
            }
        }
    }

    public void closeActivity(View v) {
        this.finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(!view.findViewById(R.id.add_contact_button_item).isShown()) {
            Cursor cursor = customCursorAdapter.getCursor();
            cursor.moveToPosition(position);

            int contact_id = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
            int photo_id = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_ID));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
            boolean photo_flag = false;

            if (photo_id != 0)
                photo_flag = true;

            customHorizontalAdapter.add(new Contact(contact_id, name, email, photo_flag, photo_id), true, true);
            customHorizontalAdapter.notifyDataSetChanged();

            if (relativeTwoWayView.getVisibility() == View.GONE)
                relativeTwoWayView.setVisibility(View.VISIBLE);
        }

    }

    public void addNewContact(View v) {
        String email = editText.getText().toString();

        customHorizontalAdapter.add(new Contact(-1, email, email, false, -1), true, true);
        customHorizontalAdapter.notifyDataSetChanged();

        if (relativeTwoWayView.getVisibility() == View.GONE)
            relativeTwoWayView.setVisibility(View.VISIBLE);

        resetEditText();

        relativeContact.setVisibility(View.GONE);
    }

    private void showContactDetail(int position){
        Tools.createContactDetailDialog(group.get(position), getFragmentManager());
        relativeContact.setVisibility(View.GONE);
        resetEditText();
    }

    private void resetEditText(){
        resetText = true;
        editText.setText("");
    }
}