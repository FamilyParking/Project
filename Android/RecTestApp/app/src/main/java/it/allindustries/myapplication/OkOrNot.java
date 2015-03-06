package it.allindustries.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class OkOrNot extends ActionBarActivity {

    private Button yes;
    private Button no;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_or_not);
        final String str = getIntent().getAction();
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        final Tracker t = analytics.newTracker("UA-58079755-2");



        final Button yes=(Button)findViewById(R.id.yesBt);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("API")
                        .setAction(str)
                        .setLabel("YES")
                        .build());
            }
        });

        final Button no=(Button)findViewById(R.id.noBt);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                t.send(new HitBuilders.EventBuilder()
                        .setCategory("API")
                        .setAction("str")
                        .setLabel("NO")
                        .build());
            }


        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ok_or_not, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
