package it.familiyparking.app.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;

import it.familiyparking.app.MainActivity;
import it.familiyparking.app.R;
import it.familiyparking.app.adapter.CustomAdapterGroup;
import it.familiyparking.app.dao.DataBaseHelper;
import it.familiyparking.app.dao.GroupTable;
import it.familiyparking.app.serverClass.Car;
import it.familiyparking.app.serverClass.Group;
import it.familiyparking.app.utility.Tools;


/**
 * Created by francesco on 15/01/15.
 */
public class GroupFragment extends Fragment{

    private ListView group_list;
    private ArrayList<Group> groups;
    private CustomAdapterGroup customAdapterGroup;

    public GroupFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group, container, false);

        group_list = (ListView) rootView.findViewById(R.id.group_lv);

        Tools.setUpButtonActionBar((ActionBarActivity) getActivity());

        DataBaseHelper databaseHelper = new DataBaseHelper(getActivity());
        final SQLiteDatabase db = databaseHelper.getReadableDatabase();

        groups = new ArrayList<>();

        ArrayList<String> list_groupID = getArguments().getStringArrayList("groupsID");

        Iterator<String> iterator = list_groupID.iterator();
        while(iterator.hasNext()){
            String id = iterator.next();
            String name = GroupTable.getGroupNamebyID(db,id);

            Car car = new Car("null","null","null","null","null");

            groups.add(new Group(id,name,car,GroupTable.getGroup(db,id)));
        }

        db.close();

        customAdapterGroup = new CustomAdapterGroup(this,getActivity(),groups,getActivity());
        group_list.setAdapter(customAdapterGroup);
        customAdapterGroup.notifyDataSetChanged();

        return rootView;
    }

    public void updateAdapter(){
        customAdapterGroup.notifyDataSetChanged();
    }
}
