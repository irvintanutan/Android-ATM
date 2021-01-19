package com.novigosolutions.certiscisco.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.recivers.NetworkChangeReceiver;
import com.novigosolutions.certiscisco.adapters.JobDetailListAdapter;
import com.novigosolutions.certiscisco.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco.models.Cartridge;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.utils.NetworkUtil;
import com.novigosolutions.certiscisco.webservices.ApiCallback;
import com.novigosolutions.certiscisco.webservices.SyncCaller;

import java.util.List;

public class BufferDetailActivity extends BaseActivity implements ApiCallback,NetworkChangekListener {
    ImageView imgnetwork;
    int ATMOrderId=0;
    protected MenuItem refreshItem = null;
    CoordinatorLayout cl;
    CardView cardviewdata, cardviewnodata;
    TextView txt_atm_type,txt_bank;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buffer_detail);
        setuptoolbar();
        initializeviews();
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
        mTitle.setText("BUFFER DETAIL");
        imgnetwork = (ImageView) toolbar.findViewById(R.id.imgnetwork);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_sync, menu);
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
        if (item.getItemId() == R.id.action_home) {
            alert("Confirm", "Are you sure want to go home?");
        }
        else if (item.getItemId() == R.id.action_sync) {
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
           // ImageView iv = (ImageView) inflater.inflate(R.layout.refresh_action_view, null);
            Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
            rotation.setRepeatCount(Animation.INFINITE);
            refreshItem.getActionView().startAnimation(rotation);
            //refreshItem.setActionView(iv);
        }
    }
    private void initializeviews()
    {
        cl = (CoordinatorLayout) findViewById(R.id.cl);
        txt_atm_type = (TextView)findViewById(R.id.txt_atm_type);
        txt_bank = (TextView)findViewById(R.id.txt_bank);
        RecyclerView recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        cardviewdata = (CardView)findViewById(R.id.cardviewdata);
        cardviewnodata = (CardView)findViewById(R.id.cardviewnodata);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(BufferDetailActivity.this);
        recyclerview.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(BufferDetailActivity.this, DividerItemDecoration.VERTICAL);
        recyclerview.addItemDecoration(dividerItemDecoration);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ATMOrderId = extras.getInt("orderno");
            List<Cartridge> loadingcartridgeList = Cartridge.get(ATMOrderId);
            if (loadingcartridgeList.size() > 0) {
                JobDetailListAdapter mAdapter = new JobDetailListAdapter(loadingcartridgeList);
                recyclerview.setAdapter(mAdapter);
            }
            else
            {
                cardviewdata.setVisibility(View.GONE);
                cardviewnodata.setVisibility(View.VISIBLE);
            }
            Job job = Job.getSingle(extras.getInt("orderno"));
            txt_atm_type.setText(job.ATMTypeCode);
            txt_bank.setText(job.Bank+" "+job.ATMType);
        }
    }
    @Override
    public void onNetworkChanged() {
        if (NetworkUtil.getConnectivityStatusString(BufferDetailActivity.this))
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
        }
    }
    private void alert(String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BufferDetailActivity.this);
        //alertDialog.setCancelable(true);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(BufferDetailActivity.this, HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
