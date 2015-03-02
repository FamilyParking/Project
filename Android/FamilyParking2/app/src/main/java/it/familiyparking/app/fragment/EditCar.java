package it.familiyparking.app.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.adapter.CustomAdapterCarBrand;
import it.familiyparking.app.adapter.CustomCursorAdapter;
import it.familiyparking.app.adapter.CustomHorizontalAdapter_4CarEdit;
import it.familiyparking.app.dao.CarTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.User;
import it.familiyparking.app.task.DoBluetoothJoin;
import it.familiyparking.app.task.DoRemoveCar;
import it.familiyparking.app.task.DoSaveCar;
import it.familiyparking.app.task.DoUpdateCar;
import it.familiyparking.app.utility.Tools;


/**
 * Created by francesco on 15/01/15.
 */
public class EditCar extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, TextWatcher, AdapterView.OnItemClickListener {

    private View rootView;
    private User user;
    private Car car;
    private boolean isCreation;

    private CustomAdapterCarBrand adapterCarBrand;
    private Spinner brand_spinner;

    private ArrayList<User> contactListAdapter;
    private CustomHorizontalAdapter_4CarEdit customHorizontalAdapter;
    private TwoWayView listGroup;

    private String[] selectionArgs = new String[5];
    private String searchString;
    private String lastSearchString;
    private LoaderManager loaderManager;
    final private String[] PROJECTION ={    ContactsContract.Contacts._ID,
                                            ContactsContract.Contacts.LOOKUP_KEY,
                                            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                                            ContactsContract.CommonDataKinds.Email.DATA,
                                            ContactsContract.Contacts.PHOTO_ID              };

    private RelativeLayout relativeResultFinder;
    private ListView listResultFinder;
    private CustomCursorAdapter customCursorAdapter;
    private EditText editTextFinder;
    private boolean addButton;

    private Button bluetooth_button;
    private Button save_button;
    private Button remove_button;

    private EditText car_name;
    private EditText car_register;

    private Car oldCar;

    public EditCar() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_edit_car, container, false);

        contactListAdapter = new ArrayList<>();

        Tools.setUpButtonActionBar((MainActivity)getActivity());

        setSpinner();
        setEditText();
        setSaveButton();

        if(isCreation){
            Tools.setTitleActionBar((MainActivity)getActivity(),R.string.create_car);
            contactListAdapter.add(user);
        }
        else{
            Tools.setTitleActionBar((MainActivity)getActivity(),R.string.edit_car);
            setRemoveButton();
            oldCar = car.clone();
            contactListAdapter = car.getUsers();
        }

        setHorizontalList();
        setLoader();
        setFinder();
        setBluetooth();


        return rootView;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        user = args.getParcelable("user");
        car = args.getParcelable("car");

        isCreation = (car == null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        selectionArgs[0] = "%" + searchString + "%";
        selectionArgs[1] = "%" + searchString + "%";
        selectionArgs[2] = "%@%";
        selectionArgs[3] = "%@%";
        selectionArgs[4] = "%whatsapp%";

        final String SELECTION = "( "+  ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ? OR " +
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

        if(relativeResultFinder.isShown()) {
            relativeResultFinder.setVisibility(View.GONE);
        }
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

        customCursorAdapter.swapCursor(data);

        if(!relativeResultFinder.isShown() && !Tools.isCursorEmpty(data)) {
            relativeResultFinder.setVisibility(View.VISIBLE);
            relativeResultFinder.requestFocus();
        }
        else if(Tools.isCursorEmpty(data)) {
            relativeResultFinder.setVisibility(View.GONE);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        customCursorAdapter.swapCursor(null);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        searchString = editTextFinder.getText().toString();

        while((searchString.length() > 0) && (searchString.charAt(searchString.length()-1) == ' '))
            searchString = searchString.substring(0,searchString.length()-1);

        if(!lastSearchString.equals(searchString)) {

            lastSearchString = searchString;

            if (searchString.equals("")) {
                relativeResultFinder.setVisibility(View.GONE);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(!view.findViewById(R.id.add_contact_button_item).isShown()) {
            Cursor cursor = customCursorAdapter.getCursor();
            cursor.moveToPosition(position);

            int photo_id = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_ID));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
            boolean photo_flag = false;

            if (photo_id != 0)
                photo_flag = true;

            customHorizontalAdapter.add(new User(name, email, photo_flag, Integer.toString(photo_id)), true);
            customHorizontalAdapter.notifyDataSetChanged();
        }
    }

    private void setSpinner(){
        brand_spinner = (Spinner) rootView.findViewById(R.id.brand_s);
        adapterCarBrand = new CustomAdapterCarBrand(getActivity());
        brand_spinner.setAdapter(adapterCarBrand);
    }

    private void setHorizontalList(){
        customHorizontalAdapter = new CustomHorizontalAdapter_4CarEdit(getActivity(),contactListAdapter);
        listGroup = ((TwoWayView)rootView.findViewById(R.id.group_list));
        listGroup.setAdapter(customHorizontalAdapter);

        listGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity)getActivity()).setContactDetailDialog(contactListAdapter.get(position));
            }
        });
    }

    private void setLoader(){
        loaderManager = getActivity().getSupportLoaderManager();
        loaderManager.initLoader(0, null, this);
    }

    private void setFinder(){
        addButton = false;
        lastSearchString = "";

        editTextFinder = (EditText) rootView.findViewById(R.id.car_finder_et);
        editTextFinder.addTextChangedListener(this);

        relativeResultFinder = ((RelativeLayout)rootView.findViewById(R.id.car_finder_result_relative));
        listResultFinder = ((ListView)rootView.findViewById(R.id.car_finder_lv));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewContact();
            }
        };

        customCursorAdapter = new CustomCursorAdapter(getActivity(),null,0,listener);
        listResultFinder.setAdapter(customCursorAdapter);
        listResultFinder.setOnItemClickListener(this);

        listResultFinder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    private void addNewContact() {
        String email = editTextFinder.getText().toString();

        customHorizontalAdapter.add(new User(email, email, false, null), true);
        customHorizontalAdapter.notifyDataSetChanged();
    }

    public void removeContact(User contact){
        customHorizontalAdapter.remove(contact);
        customHorizontalAdapter.notifyDataSetChanged();
    }

    private void setBluetooth(){
        bluetooth_button = (Button) rootView.findViewById(R.id.car_bluetooth_b);

        if(!isCreation)
            bluetooth_button.setText(car.getBluetoothName()+"("+car.getBluetoothMac()+")");

        final EditCar fragment = this;
        bluetooth_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCreation){
                    if (Tools.isBluetoothEnable())
                        searchBluetoothDevice();
                    else
                        Tools.showAlertBluetooth(getActivity());
                }
                else{
                    Tools.showAlertBluetoothRemove(getActivity(),fragment,car);
                }
            }
        });
    }

    public void unlinkBluetoothDevice(){
        car.setBluetoothName(null);
        car.setBluetoothMac(null);

        SQLiteDatabase db = Tools.getDB_Writable(getActivity());
        CarTable.updateBluetooth(db, car);

        bluetooth_button.setText(getActivity().getResources().getString(R.string.add_bluetooth));
    }

    public void linkBluetoothDevice(String bluetooth_name, String bluetooth_mac){
        car.setBluetoothName(bluetooth_name);
        car.setBluetoothMac(bluetooth_mac);

        bluetooth_button.setText(getActivity().getResources().getString(R.string.remove_bluetooth));
    }

    private void searchBluetoothDevice(){
        ((MainActivity)getActivity()).setProgressDialogCircular("Looking for bluetooth device ...");

        if(isCreation)
            car = new Car();

        new Thread(new DoBluetoothJoin(getActivity(),car,bluetooth_button,this)).start();
    }

    private void setSaveButton(){
        save_button = (Button) rootView.findViewById(R.id.car_save_b);

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((car_name.getText().toString() == null)||(car_name.getText().toString().equals(""))){
                    car_name.requestFocus();
                    car_name.setHint("Name is mandatory");
                    car_name.setHintTextColor(getActivity().getResources().getColor(R.color.red));
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(car_name, InputMethodManager.SHOW_IMPLICIT);
                }
                else{
                    if(isCreation)
                        ((MainActivity)getActivity()).setProgressDialogCircular("Creating car ...");
                    else
                        ((MainActivity)getActivity()).setProgressDialogCircular("Updating  car ...");

                    if(car == null)
                        car = new Car();

                    car.setName(car_name.getText().toString());
                    car.setRegister(car_register.getText().toString());
                    car.setBrand(Tools.getBrand(brand_spinner,getActivity()));
                    car.setUsers(contactListAdapter);


                    Runnable runnable;
                    if(isCreation)
                        runnable = new DoSaveCar(getActivity(),car,user);
                    else
                        runnable = new DoUpdateCar(getActivity(),car,oldCar,user);

                    new Thread(runnable).start();
                }
            }
        });
    }

    private void setRemoveButton(){
        rootView.findViewById(R.id.car_delete_v).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.car_delete_relative).setVisibility(View.VISIBLE);

        save_button.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.rectangle_green));

        remove_button = (Button) rootView.findViewById(R.id.car_delete_b);
        remove_button.setVisibility(View.VISIBLE);
        remove_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setProgressDialogCircular("Remove car ...");

                new Thread(new DoRemoveCar(getActivity(),car,user));
            }
        });
    }

    private void setEditText(){
        car_name = (EditText) rootView.findViewById(R.id.car_name_et);
        car_register = (EditText) rootView.findViewById(R.id.car_register_et);
    }
}
