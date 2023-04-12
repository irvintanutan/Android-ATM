package com.novigosolutions.certiscisco.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.adapters.GridAdapter;
import com.novigosolutions.certiscisco.applications.CertisCISCO;
import com.novigosolutions.certiscisco.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.recivers.NetworkChangeReceiver;
import com.novigosolutions.certiscisco.service.AuditService;
import com.novigosolutions.certiscisco.utils.NetworkUtil;
import com.novigosolutions.certiscisco.utils.Preferences;
import com.novigosolutions.certiscisco.webservices.ApiCallback;
import com.novigosolutions.certiscisco.webservices.SyncCaller;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity implements ApiCallback, NetworkChangekListener {
    GridView gridview;
    GridAdapter gridadapter;
    List<Integer> countList;
    protected MenuItem refreshItem = null;
    CoordinatorLayout cl;
    ImageView imgnetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setuptoolbar();
        initializeviews();
        setactions();
        NetworkChangeReceiver.changekListener = this;
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        startService(new Intent(getApplicationContext(), AuditService.class));
    }

    private void setuptoolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("HOME");
        imgnetwork = (ImageView) toolbar.findViewById(R.id.imgnetwork);
    }

    private void initializeviews() {
        cl = findViewById(R.id.cl);
        gridview = findViewById(R.id.grid_view);
    }

    private void setactions() {
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = null;


                switch (position) {
                    case 0:
                        intent = new Intent(HomeActivity.this, JobListActivity.class);
                        intent.putExtra("status", "ALL");
                        break;
                    case 1:
                        intent = new Intent(HomeActivity.this, JobListActivity.class);
                        intent.putExtra("status", "READY TO DELIVER");
                        break;
                    case 2:
                        intent = new Intent(HomeActivity.this, JobListActivity.class);
                        intent.putExtra("status", "DELIVERED");
                        break;
                    case 3:
                        intent = new Intent(HomeActivity.this, BufferListActivity.class);
                        break;
                    case 4:
                        intent = new Intent(HomeActivity.this, JobListActivity.class);
                        intent.putExtra("status", "OFFLINE");

                        break;
                    case 5:
                        intent = new Intent(HomeActivity.this, SettingActivity.class);
                        break;
                    default:
                        break;
                }
                if (intent != null) startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CertisCISCO.setCurrentactvity(this);
        refresh();
        NetworkChangeReceiver.changekListener = this;
        onNetworkChanged();
        LocalBroadcastManager.getInstance(this).registerReceiver(syncReceiver,
                new IntentFilter("syncreciverevent"));
        LocalBroadcastManager.getInstance(this).registerReceiver(offlineupdateReceiver,
                new IntentFilter("offlinereciverevent"));
        startService(new Intent(getApplicationContext(), AuditService.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        CertisCISCO.setCurrentactvity(null);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(syncReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(offlineupdateReceiver);
    }

    private BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };
    private BroadcastReceiver offlineupdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            hideProgressDialog();
            Toast.makeText(HomeActivity.this, "Offline job updated", Toast.LENGTH_SHORT).show();
            refresh();
        }
    };

    public void refresh() {
        if (gridadapter == null) {
            countList = new ArrayList<Integer>();
            countList.add(Job.getbyOrderMode("SCHEDULED").size());
            countList.add(Job.getByStatus("SCHEDULED", "READY TO DELIVER").size());
            countList.add(Job.getByStatus("SCHEDULED", "DELIVERED").size());
            countList.add(Job.getbyOrderMode("BUFFER").size());
            countList.add(Job.getOfflinelist().size());
            countList.add(0);
            gridadapter = new GridAdapter(HomeActivity.this, countList);
            gridview.setAdapter(gridadapter);
        } else {
            countList.clear();
            countList.add(Job.getbyOrderMode("SCHEDULED").size());
            countList.add(Job.getByStatus("SCHEDULED", "READY TO DELIVER").size());
            countList.add(Job.getByStatus("SCHEDULED", "DELIVERED").size());
            countList.add(Job.getbyOrderMode("BUFFER").size());
            countList.add(Job.getOfflinelist().size());
            countList.add(0);
            gridadapter.notifyDataSetChanged();
        }

        List<Job> job = Job.getAll();
        for (Job jobs : job) {
            Log.e("COUNTLIST", jobs.ATMOrderId + " " + jobs.Status);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_sync, menu);
        setRefreshItem(menu.findItem(R.id.action_sync));
        refreshItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(refreshItem);
            }
        });
        menu.findItem(R.id.action_user_name).setTitle(Preferences.getString("UserName", HomeActivity.this));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
        } else if (item.getItemId() == R.id.action_sync) {
            if (NetworkUtil.getConnectivityStatusString(this)) {
                SyncCaller.instance().Sync(this, this);
                runRefresh();
            } else {
                raiseInternetSnakbar();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getString(R.string.pressback), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    protected void setRefreshItem(MenuItem item) {
        refreshItem = item;
    }

    protected void stopRefresh() {
        if (refreshItem != null) {
            refreshItem.getActionView().clearAnimation();
            //refreshItem.setActionView(null);
        }
    }

    protected void runRefresh() {
        if (refreshItem != null) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //ImageView iv = (ImageView) inflater.inflate(R.layout.refresh_action_view, null);
            Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
            rotation.setRepeatCount(Animation.INFINITE);
            refreshItem.getActionView().startAnimation(rotation);
            //refreshItem.setActionView(iv);
        }
    }

    private void logout() {
        if (Job.isOfflineExist()) {
            raiseSnakbar("Complete offline job to logout");
        } else {
            alert();
        }
    }

    private void alert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Confirm");
        alertDialog.setMessage("Are you sure you want to logout?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Preferences.saveBoolean("LoggedIn", false, HomeActivity.this);
                finish();
                Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    @Override
    public void onResult(int result_code, String messege) {
        stopRefresh();
        if (result_code == 409) {
            authalert(this);
        } else {
            raiseSnakbar(messege);
            refresh();
        }
    }

    @Override
    public void onNetworkChanged() {

        if (NetworkUtil.getConnectivityStatusString(HomeActivity.this))
            imgnetwork.setVisibility(View.VISIBLE);
        else
            imgnetwork.setVisibility(View.GONE);
    }
}
