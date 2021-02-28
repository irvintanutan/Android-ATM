package com.novigosolutions.certiscisco.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.adapters.JobListAdapter;
import com.novigosolutions.certiscisco.interfaces.IOnScannerData;
import com.novigosolutions.certiscisco.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco.interfaces.RecyclerViewClickListener;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.recivers.NetworkChangeReceiver;
import com.novigosolutions.certiscisco.utils.CustomDialogClass;
import com.novigosolutions.certiscisco.utils.NetworkUtil;
import com.novigosolutions.certiscisco.webservices.ATMOfflineListUpdateCaller;
import com.novigosolutions.certiscisco.webservices.ApiCallback;
import com.novigosolutions.certiscisco.webservices.OfflineCallback;
import com.novigosolutions.certiscisco.webservices.SyncCaller;

import java.util.List;

import static com.novigosolutions.certiscisco.R.id.recyclerview;

public class JobListActivity extends BarCodeScanActivity implements IOnScannerData, RecyclerViewClickListener, View.OnClickListener, ApiCallback, OfflineCallback, NetworkChangekListener {
    private RecyclerView recyclerView;
    private JobListAdapter mAdapter;
    String status = "";
    List<Job> jobList;
    CardView cardscan, cardnodata;
    protected MenuItem refreshItem = null;
    LinearLayout llmain;
    ImageView imgnetwork;
    CoordinatorLayout cl;
    Button submitall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_list);
        setuptoolbar();
        initializeviews();
    }

    private void setuptoolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("JOB LIST");
        imgnetwork = (ImageView) toolbar.findViewById(R.id.imgnetwork);
    }

    private void initializeviews() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.scanll);
        ll.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(recyclerview);
        cardscan = (CardView) findViewById(R.id.cardscan);
        cardnodata = (CardView) findViewById(R.id.cardviewnodata);
        llmain = (LinearLayout) findViewById(R.id.llmain);
        cl = (CoordinatorLayout) findViewById(R.id.cl);
        submitall = (Button) findViewById(R.id.btn_submit);
        submitall.setOnClickListener(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
//        recyclerView.addItemDecoration(dividerItemDecoration);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            status = extras.getString("status");
        }

        if ((status.equals("OFFLINE") || status.equals("DELIVERED"))) {
            cardscan.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void recyclerViewListClicked(int orderno) {
        gotoprocessjob(orderno);
    }

    private void gotoprocessjob(int orderno) {
        Job job = Job.getSingle(orderno);
        if (job.isOfflineSaved == 1) {
            CustomDialogClass cdd = new CustomDialogClass(JobListActivity.this, null, orderno, true);
            cdd.show();
        } else if (job.Status.equals("DELIVERED")) {
            CustomDialogClass cdd = new CustomDialogClass(JobListActivity.this, null, orderno, true);
            cdd.show();
        } else {
            Intent intent = new Intent(JobListActivity.this, ProcessJobActivity.class);
            intent.putExtra("orderno", orderno);
            startActivity(intent);
        }
    }

    @Override
    public void onDataScanned(String result) {
        if (result != null) {
            try {
                int morderno = Integer.parseInt(result);
                if (Job.isJobExist(morderno))
                    gotoprocessjob(morderno);
                else
                    Toast.makeText(JobListActivity.this, "INVALID BARCODE", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(JobListActivity.this, "INVALID BARCODE", Toast.LENGTH_SHORT).show();
            }

        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.scanll:
                try {
                    scansoft();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_submit:
                if (NetworkUtil.getConnectivityStatusString(JobListActivity.this)) {
                    showProgressDialog("Updating...");
                    ATMOfflineListUpdateCaller.instance().UpdateATMList(this, getApplicationContext());
                } else {
                    raiseInternetSnakbar();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
        NetworkChangeReceiver.changekListener = this;
        onNetworkChanged();
        LocalBroadcastManager.getInstance(this).registerReceiver(offlineupdateReceiver,
                new IntentFilter("offlinereciverevent"));
        LocalBroadcastManager.getInstance(this).registerReceiver(syncReceiver,
                new IntentFilter("syncreciverevent"));
        registerScannerEvent(this);
    }

    private BroadcastReceiver offlineupdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            hideProgressDialog();
            Toast.makeText(JobListActivity.this, "Offline job updated", Toast.LENGTH_SHORT).show();
            refresh();
        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(offlineupdateReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(syncReceiver);
        unregisterScannerEvent();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };

    private void refresh() {
        if (mAdapter == null) {
            switch (status) {
                case "OFFLINE":
                    jobList = Job.getOfflinelist();
                    submitall.setVisibility(View.VISIBLE);
                    break;
                case "ALL":
                    jobList = Job.getbyOrderMode("SCHEDULED");
                    break;
                default:
                    jobList = Job.getByStatus("SCHEDULED", status);
                    break;
            }
            mAdapter = new JobListAdapter(jobList, this, this);
            recyclerView.setAdapter(mAdapter);
            if (jobList.size() == 0) {
                llmain.setVisibility(View.GONE);
                cardnodata.setVisibility(View.VISIBLE);
            }
        } else {
            List<Job> tempjoblist;
            switch (status) {
                case "OFFLINE":
                    tempjoblist = Job.getOfflinelist();
                    break;
                case "ALL":
                    tempjoblist = Job.getbyOrderMode("SCHEDULED");
                    break;
                default:
                    tempjoblist = Job.getByStatus("SCHEDULED", status);
                    break;
            }
            jobList.clear();
            jobList.addAll(tempjoblist);
            mAdapter.notifyDataSetChanged();
            if (jobList.size() > 0) {
                llmain.setVisibility(View.VISIBLE);
                cardnodata.setVisibility(View.GONE);
            } else {
                llmain.setVisibility(View.GONE);
                cardnodata.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.only_sync, menu);
        setRefreshItem(menu.findItem(R.id.action_sync));
        refreshItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(refreshItem);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sync) {
            if (NetworkUtil.getConnectivityStatusString(this)) {
                SyncCaller.instance().Sync(this, this);
                runRefresh();
            } else {
                raiseInternetSnakbar();
            }
        }
        return super.onOptionsItemSelected(item);
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
            Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
            rotation.setRepeatCount(Animation.INFINITE);
            refreshItem.getActionView().startAnimation(rotation);
        }
    }

    @Override
    public void onResult(int result, String messege) {
        stopRefresh();
        if (result == 409) {
            authalert(this);
        } else {
            raiseSnakbar(messege);
            refresh();
        }
    }

    @Override
    public void onNetworkChanged() {
        if (NetworkUtil.getConnectivityStatusString(JobListActivity.this))
            imgnetwork.setVisibility(View.VISIBLE);
        else
            imgnetwork.setVisibility(View.GONE);
    }

    @Override
    public void onOfflineUpdated(int result, String resultdata) {
        hideProgressDialog();
        Toast.makeText(JobListActivity.this, resultdata, Toast.LENGTH_SHORT).show();
        refresh();
    }

//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BUTTON_L1) {
//            try {
//                scanhard();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return super.onKeyUp(keyCode, event);
//    }
}
