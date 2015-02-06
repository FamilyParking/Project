package it.familiyparking.app.fragment;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.adapter.CustomCursorAdapter;
import it.familiyparking.app.adapter.CustomHorizontalAdapter;
import it.familiyparking.app.dialog.ProgressDialogCircular;
import it.familiyparking.app.serverClass.Contact;
import it.familiyparking.app.task.DoSaveGroup;
import it.familiyparking.app.utility.Tools;


/**
 * Created by francesco on 15/01/15.
 */
public class CreateGroup extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, TextWatcher, AdapterView.OnItemClickListener {

    final private String[] PROJECTION ={ContactsContract.Contacts._ID,ContactsContract.Contacts.LOOKUP_KEY,ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Email.DATA,ContactsContract.Contacts.PHOTO_ID};

    private String[] selectionArgs = new String[5];
    private String searchString;
    private String lastSearchString;

    private LoaderManager loaderManager;

    private RelativeLayout relativeContact;
    private ListView listContact;
    private CustomCursorAdapter customCursorAdapter;

    private ArrayList<Contact> group;
    private CustomHorizontalAdapter customHorizontalAdapter;
    private TwoWayView listGroup;
    private RelativeLayout relativeTwoWayView;

    private EditText editTextFinder;
    private EditText editTextName;

    private Button save_button;

    private RelativeLayout relativeContactDetail;

    private View.OnClickListener listener;

    private View rootView;

    private boolean addButton;
    private boolean resetText;
    private boolean resetTextFromName;

    public CreateGroup() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_create_group, container, false);

        lastSearchString = "";

        group = new ArrayList<>();
        customHorizontalAdapter = new CustomHorizontalAdapter(getActivity(),group);
        listGroup = ((TwoWayView)rootView.findViewById(R.id.group_list));
        listGroup.setAdapter(customHorizontalAdapter);

        listGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showContactDetail(position);
            }
        });

        relativeTwoWayView = ((RelativeLayout)rootView.findViewById(R.id.group_rl));

        editTextName = ((EditText)rootView.findViewById(R.id.new_group_name_et));
        editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!resetTextFromName) {
                    resetEditText();
                    relativeContact.setVisibility(View.GONE);
                    resetTextFromName = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                resetTextFromName = false;
                manageSaveButton();
            }
        });

        editTextFinder = ((EditText)rootView.findViewById(R.id.find_contact_edt));
        editTextFinder.addTextChangedListener(this);
        relativeContact = ((RelativeLayout)rootView.findViewById(R.id.contact_rl));
        listContact = ((ListView)rootView.findViewById(R.id.contact_lv));

        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewContact();
            }
        };

        customCursorAdapter = new CustomCursorAdapter(getActivity(),null,0,listener);
        listContact.setAdapter(customCursorAdapter);
        listContact.setOnItemClickListener(this);

        addButton = false;
        resetText = false;
        resetTextFromName = false;

        loaderManager = getActivity().getSupportLoaderManager();
        loaderManager.initLoader(0, null, this);

        save_button = (Button) rootView.findViewById(R.id.check_group_bt);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGroup();
            }
        });

        relativeContactDetail = (RelativeLayout)rootView.findViewById(R.id.contact_detail_container_rl);

        return rootView;
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

        return new CursorLoader(getActivity(),ContactsContract.Data.CONTENT_URI,PROJECTION,SELECTION,selectionArgs,SORT);
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
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        manageSaveButton();

        if(resetText){
            resetText = false;
        }
        else {
            searchString = editTextFinder.getText().toString();

            if(!lastSearchString.equals(searchString)) {
                lastSearchString = searchString;

                if (searchString.equals("")) {
                    relativeContact.setVisibility(View.GONE);
                } else {
                    loaderManager.restartLoader(0, null, this);

                    if (searchString.contains("@"))
                        addButton = true;
                    else
                        addButton = false;
                }
            }
        }
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

            customHorizontalAdapter.add(new Contact(contact_id, name, email, photo_flag, photo_id), true);
            customHorizontalAdapter.notifyDataSetChanged();

            manageSaveButton();

            if (relativeTwoWayView.getVisibility() == View.GONE)
                relativeTwoWayView.setVisibility(View.VISIBLE);
        }

    }

    public void addNewContact() {
        String email = editTextFinder.getText().toString();

        customHorizontalAdapter.add(new Contact(-1, email, email, false, -1), true);
        customHorizontalAdapter.notifyDataSetChanged();

        manageSaveButton();

        if (relativeTwoWayView.getVisibility() == View.GONE)
            relativeTwoWayView.setVisibility(View.VISIBLE);

        resetEditText();

        relativeContact.setVisibility(View.GONE);
    }

    private void showContactDetail(int position){
        Tools.closeKeyboard(rootView,getActivity());
        relativeContact.setVisibility(View.GONE);
        resetEditText();

        showContactDetail(group.get(position));
    }

    private void resetEditText(){
        resetText = true;
        editTextFinder.setText("");
    }

    public void removeContactGroup(Contact contact){
        customHorizontalAdapter.remove(contact);
        customHorizontalAdapter.notifyDataSetChanged();
        if(customHorizontalAdapter.isEmpty())
            relativeTwoWayView.setVisibility(View.GONE);
    }

    private void manageSaveButton(){
        if((!editTextName.getText().toString().isEmpty())&&(!group.isEmpty())) {
            save_button.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.ic_check_green));
            save_button.setClickable(true);
        }
        else {
            save_button.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.ic_check_grey));
            save_button.setClickable(false);
        }
    }

    private void showContactDetail(final Contact contact){
        if(contact.getEmail().equals(contact.getName()))
            rootView.findViewById(R.id.contact_email_tv_detail).setVisibility(View.GONE);
        else
            ((TextView) rootView.findViewById(R.id.contact_email_tv_detail)).setText(contact.getEmail());

        ((TextView) rootView.findViewById(R.id.contact_name_tv_detail)).setText(contact.getName());


        Tools.addThumbnail(getActivity().getApplicationContext(), ((ImageView) rootView.findViewById(R.id.contact_image_iv_detail)), contact.getPhoto_Id());

        rootView.findViewById(R.id.back_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeContactDetail.setVisibility(View.GONE);
            }
        });


        rootView.findViewById(R.id.delete_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeContactGroup(contact);
                relativeContactDetail.setVisibility(View.GONE);
            }
        });

        relativeContactDetail.setVisibility(View.VISIBLE);
    }

    private void saveGroup(){
        Tools.closeKeyboard(rootView,getActivity());

        ProgressDialogCircular progressDialog = new ProgressDialogCircular();
        ((MainActivity)getActivity()).setProgressDialogCircular(progressDialog);

        Bundle bundle = new Bundle();
        bundle.putString("message", "Creating group ...");
        progressDialog.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, progressDialog).commit();

        new Thread(new DoSaveGroup(getActivity(),editTextName.getText().toString(),null,group,progressDialog)).start();
    }
}
