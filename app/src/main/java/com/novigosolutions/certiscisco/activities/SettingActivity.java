package com.novigosolutions.certiscisco.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco.recivers.NetworkChangeReceiver;
import com.novigosolutions.certiscisco.utils.CommonMethods;
import com.novigosolutions.certiscisco.utils.NetworkUtil;
import com.novigosolutions.certiscisco.utils.Preferences;

public class SettingActivity extends AppCompatActivity implements NetworkChangekListener {
    //    SeekBar seekBar;
//    TextView interval;
//    int intervaltime = 5;
    ImageView imgnetwork;

    TextView interval, servertime;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setuptoolbar();
        interval = (TextView) findViewById(R.id.txttimeinterval);
        interval.setText(Preferences.getInt("INTERVAL", this) + "sec");

        servertime = (TextView) findViewById(R.id.txtservertime);
        Log.e("servertime", CommonMethods.getCurrentDateTime(this));
//        seekBar = (SeekBar) findViewById(R.id.timeinterval);
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (progress == 0)
//                    intervaltime = 1;
//                else
//                    intervaltime = progress * 5;
//                interval.setText(intervaltime + "m");
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//        loadPreferences();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
      /* do what you need to do */
                servertime.setText(CommonMethods.getCurrentDateTime(SettingActivity.this));
      /* and here comes the "trick" */
                handler.postDelayed(this, 1000);
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 0);

    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkChangeReceiver.changekListener = this;
        onNetworkChanged();
    }

    private void setuptoolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("SETTINGS");
        imgnetwork = (ImageView) toolbar.findViewById(R.id.imgnetwork);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //savePreferences();
    }

//    private void loadPreferences() {
//        intervaltime = Preferences.getIntervalInt("INTERVAL", this);
//        interval.setText(intervaltime + "m");
//        if (intervaltime == 1)
//            seekBar.setProgress(0);
//        else
//            seekBar.setProgress(intervaltime / 5);
//    }
//
//    private void savePreferences() {
//        Preferences.saveInt("INTERVAL", intervaltime, this);
//        Intent intent = new Intent("com.novigosolutions.certiscisco.intervalchanged");
//        sendBroadcast(intent);
//    }

    @Override
    public boolean onSupportNavigateUp() {
        //savePreferences();
        onBackPressed();
        return true;
    }

    @Override
    public void onNetworkChanged() {
        if (NetworkUtil.getConnectivityStatusString(SettingActivity.this))
            imgnetwork.setVisibility(View.VISIBLE);
        else
            imgnetwork.setVisibility(View.GONE);
    }
}
