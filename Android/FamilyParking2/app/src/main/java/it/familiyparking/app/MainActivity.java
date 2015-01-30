package it.familiyparking.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import it.familiyparking.app.fragment.Map;
import it.familiyparking.app.utility.Tools;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Tools.activeAnalytic(this);

        Tools.setActionBar(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new Map()).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_cars:
                Log.e(Tools.getActivtyName(this),"Car!");
                return true;

            case R.id.action_groups:
                Log.e(Tools.getActivtyName(this),"Group!");
                return true;

            case R.id.action_ghostmode:
                Log.e(Tools.getActivtyName(this),"Ghostmode!");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
