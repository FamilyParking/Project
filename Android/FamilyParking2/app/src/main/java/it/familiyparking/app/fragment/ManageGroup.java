package it.familiyparking.app.fragment;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import it.familiyparking.app.serverClass.Group;
import it.familiyparking.app.task.DoSaveGroup;
import it.familiyparking.app.task.DoUpdateGroup;
import it.familiyparking.app.utility.Tools;


/**
 * Created by francesco on 15/01/15.
 */
public class ManageGroup extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, TextWatcher, AdapterView.OnItemClickListener {

    final private String[] PROJECTION ={ContactsContract.Contacts._ID,ContactsContract.Contacts.LOOKUP_KEY,ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Email.DATA,ContactsContract.Contacts.PHOTO_ID};

    private String[] selectionArgs = new String[5];
    private String searchString;
    private String lastSearchString;

    private LoaderManager loaderManager;

    private RelativeLayout relativeContact;
    private ListView listContact;
    private CustomCursorAdapter customCursorAdapter;

    private ArrayList<Contact> contactArrayList;
    private CustomHorizontalAdapter customHorizontalAdapter;
    private TwoWayView listGroup;
    private RelativeLayout relativeTwoWayView;

    private EditText editTextFinder;
    private EditText editTextName;

    private Button save_button;

    private RelativeLayout relativeContactDetail;

    private View.OnClickListener listener;

    private View rootView;

    private View circularProgress;
    private View findLens;

    private boolean addButton;
    private boolean resetText;
    private boolean resetTextFromName;

    private Group group;

    public ManageGroup() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_manage_group, container, false);

        lastSearchString = "";

        if(creating())
            setGraphicToCreate();
        else
            setGraphicToModify();

        return rootView;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.group = args.getParcelable("group");
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
    public void onResume() {
        super.onResume();
        resetEditText();
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

        findLens.setVisibility(View.VISIBLE);
        circularProgress.setVisibility(View.GONE);

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
        if(creating())
            manageSaveButton();

        if(resetText){
            resetText = false;
        }
        else {
            searchString = editTextFinder.getText().toString();
            while(searchString.charAt(searchString.length()-1) == ' ')
                searchString = searchString.substring(0,searchString.length()-1);

            if(!lastSearchString.equals(searchString)) {
                lastSearchString = searchString;

                if (searchString.equals("")) {
                    relativeContact.setVisibility(View.GONE);
                } else {
                    findLens.setVisibility(View.GONE);
                    circularProgress.setVisibility(View.VISIBLE);

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

            if(creating())
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

        showContactDetail(contactArrayList.get(position));
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
        if((!editTextName.getText().toString().isEmpty())&&(!contactArrayList.isEmpty())) {
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

        new Thread(new DoSaveGroup(getActivity(),editTextName.getText().toString(),null,contactArrayList,progressDialog)).start();
    }

    private void setGraphicToCreate(){
        contactArrayList = new ArrayList<>();

        setEditTextGroupName(null);
        setEditTextFindContact();
        setHorizontalList();
        setListForResult();
        setPointer();
        setLoader();
        setSaveButton();
        setBoolean();
    }

    private void setGraphicToModify(){
        contactArrayList = group.getContacts();

        setGroupImage();
        setEditTextGroupName(group.getName());
        setEditTextFindContact();
        setHorizontalList();
        setListForResult();
        setPointer();
        setLoader();
        updateSaveButton();
        setBoolean();
    }

    private void setEditTextGroupName(String name){
        editTextName = ((EditText)rootView.findViewById(R.id.new_group_name_et));

        if(name != null)
            editTextName.setText(name);

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
    }

    private void setEditTextFindContact(){
        editTextFinder = ((EditText)rootView.findViewById(R.id.find_contact_edt));
        editTextFinder.addTextChangedListener(this);
    }

    private void setHorizontalList(){
        customHorizontalAdapter = new CustomHorizontalAdapter(getActivity(),contactArrayList);
        listGroup = ((TwoWayView)rootView.findViewById(R.id.group_list));
        listGroup.setAdapter(customHorizontalAdapter);

        listGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showContactDetail(position);
            }
        });

        relativeTwoWayView = ((RelativeLayout)rootView.findViewById(R.id.group_rl));
    }

    private void setListForResult(){
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
    }

    private void setPointer(){
        relativeContactDetail = (RelativeLayout)rootView.findViewById(R.id.contact_detail_container_rl);

        circularProgress = rootView.findViewById(R.id.find_progress_circular);
        findLens = rootView.findViewById(R.id.find_iv);
    }

    private void setLoader(){
        loaderManager = getActivity().getSupportLoaderManager();
        loaderManager.initLoader(0, null, this);
    }

    private void setSaveButton(){
        save_button = (Button) rootView.findViewById(R.id.check_group_bt);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGroup();
            }
        });
    }

    private void updateSaveButton(){
        rootView.findViewById(R.id.check_group_bt).setVisibility(View.GONE);
        rootView.findViewById(R.id.button_rl_group).setVisibility(View.VISIBLE);

        rootView.findViewById(R.id.back_group_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBack();
            }
        });

        rootView.findViewById(R.id.save_group_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGroup();
            }
        });
    }

    private void setBoolean(){
        addButton = false;
        resetText = false;
        resetTextFromName = false;
    }

    private void setGroupImage(){
        TextView group_image = (TextView) rootView.findViewById(R.id.new_group_tv);
        group_image.setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.new_group_iv).setVisibility(View.GONE);
        Tools.setImageForGroup(getActivity(),group_image,group);
    }

    public void onClickBack(){
        ((MainActivity) getActivity()).resetModifyGroup();
    }

    private void updateGroup(){
        Tools.closeKeyboard(rootView,getActivity());

        ProgressDialogCircular progressDialog = new ProgressDialogCircular();
        ((MainActivity)getActivity()).setProgressDialogCircular(progressDialog);

        Bundle bundle = new Bundle();
        bundle.putString("message", "Creating group ...");
        progressDialog.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, progressDialog).commit();

        new Thread(new DoUpdateGroup(getActivity(),editTextName.getText().toString(),contactArrayList,group,progressDialog)).start();
    }

    private boolean creating(){
        return group == null;
    }
}
