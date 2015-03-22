package it.familiyparking.app.parky;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import it.familiyparking.app.R;
import it.familiyparking.app.dialog.ProgressDialogCircular;
import it.familiyparking.app.utility.Code;
import it.familiyparking.app.utility.Tools;


public class StatisticActivity extends FragmentActivity {

    private Tracker tracker;
    private boolean doubleBackToExitPressedOnce = false;
    private StatisticFragment statisticFragment;
    private ProgressDialogCircular progressDialogCircular;
    private String from;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tracker = Tools.activeAnalytic(this);

        from = getIntent().getStringExtra("from");

        statisticFragment = new StatisticFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, statisticFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if(from.equals(Code.SERVICE)) {
            if (doubleBackToExitPressedOnce)
                Tools.closeApp(this);

            doubleBackToExitPressedOnce = true;
            Tools.createToast(this, "Please click BACK again to exit", Toast.LENGTH_SHORT);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
        else if(from.equals(Code.MAIN_ACTIVITY)){
            finish();
        }
    }

    public void updateStatistic(){
        statisticFragment.updateData();
    }

    public void closeDialog(){
        statisticFragment.closeDialog();
    }

    public void setProgressDialogCircular(String message){
        progressDialogCircular = new ProgressDialogCircular();

        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        progressDialogCircular.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.container, progressDialogCircular).commit();
    }

    public void resetProgressDialogCircular(){
        if(progressDialogCircular != null) {
            getSupportFragmentManager().beginTransaction().remove(progressDialogCircular).commit();
            progressDialogCircular = null;
        }
    }

}