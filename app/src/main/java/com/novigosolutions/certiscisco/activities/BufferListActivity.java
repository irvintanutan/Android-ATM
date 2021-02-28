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
import android.widget.ImageView;
import android.widget.TextView;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.adapters.BufferListAdapter;
import com.novigosolutions.certiscisco.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco.interfaces.RecyclerViewClickListener;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.recivers.NetworkChangeReceiver;
import com.novigosolutions.certiscisco.utils.NetworkUtil;
import com.novigosolutions.certiscisco.webservices.ApiCallback;
import com.novigosolutions.certiscisco.webservices.SyncCaller;

import java.util.List;

import static com.novigosolutions.certiscisco.R.id.recyclerview;
import static com.novigosolutions.certiscisco.models.Job.getbyOrderMode;

public class BufferListActivity extends BaseActivity implements ApiCallback,RecyclerViewClickListener,NetworkChangekListener {
    private RecyclerView recyclerView;
    CardView cardnodata;
    private BufferListAdapter mAdapter;
    List<Job> jobList;
    ImageView imgnetwork;
    protected MenuItem refreshItem = null;
    CoordinatorLayout cl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buffer_list);
        setuptoolbar();
        initializeviews();
    }
    @Override
    protected void onResume() {
        super.onResume();
        NetworkChangeReceiver.changekListener = this;
        onNetworkChanged();
        LocalBroadcastManager.getInstance(this).registerReceiver(syncReceiver,
                new IntentFilter("syncreciverevent"));
        refresh();
    }
    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(syncReceiver);
    }
    private BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };
    private void setuptoolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("BUFFER LIST");
        imgnetwork = (ImageView) toolbar.findViewById(R.id.imgnetwork);
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
            }
            else {
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
            //ImageView iv = (ImageView) inflater.inflate(R.layout.refresh_action_view, null);
            Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
            rotation.setRepeatCount(Animation.INFINITE);
            refreshItem.getActionView().startAnimation(rotation);
            //refreshItem.setActionView(iv);
        }
    }
    private void initializeviews() {

        recyclerView = (RecyclerView) findViewById(recyclerview);
        cardnodata = (CardView) findViewById(R.id.cardviewnodata);
        cl = (CoordinatorLayout) findViewById(R.id.cl);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
//        recyclerView.addItemDecoration(dividerItemDecoration);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void recyclerViewListClicked(int ATMOrderId) {
        Intent intent = new Intent(BufferListActivity.this, BufferDetailActivity.class);
        intent.putExtra("orderno", ATMOrderId);
        startActivity(intent);
    }

    private void refresh() {
        if (mAdapter == null) {
            jobList = getbyOrderMode("BUFFER");
            mAdapter = new BufferListAdapter(jobList, this);
            recyclerView.setAdapter(mAdapter);
            if (jobList.size() == 0) {
                recyclerView.setVisibility(View.GONE);
                cardnodata.setVisibility(View.VISIBLE);
            }

        } else {
            List<Job> tempjoblist=Job.getbyOrderMode("BUFFER");
            jobList.clear();
            jobList.addAll(tempjoblist);
            mAdapter.notifyDataSetChanged();
            if (jobList.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                cardnodata.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                cardnodata.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onNetworkChanged() {
        if (NetworkUtil.getConnectivityStatusString(BufferListActivity.this))
            imgnetwork.setVisibility(View.VISIBLE);
        else
            imgnetwork.setVisibility(View.GONE);
    }

    @Override
    public void onResult(int result, String messege) {
        stopRefresh();
        if(result==409)
        {
            authalert(this);
        }
        else
        {
            raiseSnakbar(messege);
            refresh();
        }
    }
}
