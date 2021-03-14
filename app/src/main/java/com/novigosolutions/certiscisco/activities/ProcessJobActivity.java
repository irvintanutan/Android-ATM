package com.novigosolutions.certiscisco.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.adapters.PagerIndicatorAdapter;
import com.novigosolutions.certiscisco.interfaces.FragmentInterface;
import com.novigosolutions.certiscisco.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco.models.Cartridge;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.recivers.NetworkChangeReceiver;
import com.novigosolutions.certiscisco.utils.CommonMethods;
import com.novigosolutions.certiscisco.utils.NetworkUtil;
import com.novigosolutions.certiscisco.utils.Preferences;
import com.novigosolutions.certiscisco.webservices.ApiCallback;
import com.novigosolutions.certiscisco.webservices.SyncCaller;
import com.viewpagerindicator.CirclePageIndicator;

import static com.novigosolutions.certiscisco.R.id.pager;

public class ProcessJobActivity extends BarCodeScanActivity implements ApiCallback, NetworkChangekListener {
    PagerIndicatorAdapter mAdapter;
    ViewPager mPager;
    //ImageView left, right;
    TextView mTitle;
    TextView txtorderno;
    int orderno = 0;
    int currentpage = 0;
    String OperationMode = "";
    ImageView imgnetwork;
    protected MenuItem refreshItem = null;
    CoordinatorLayout cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_job);
        setuptoolbar();
        initialize();
        setactions();
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
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("JOB DETAILS");
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
            alert(2, "Confirm", "Are you sure you want to go home?");
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

    private void initialize() {

        txtorderno = (TextView) findViewById(R.id.txt_orderno);
        cl = (CoordinatorLayout) findViewById(R.id.cl);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            orderno = extras.getInt("orderno");
            txtorderno.setText(Job.getATMCode(orderno));
            OperationMode = Job.getOperationMode(extras.getInt("orderno"));
            Cartridge.cancelAllScan(orderno);
            if (Job.isHistoryCleared(orderno)) {
                Job.clearHistory(orderno, getString(R.string.unload));
            }
        }
        mAdapter = new PagerIndicatorAdapter(getSupportFragmentManager(), OperationMode);
        mPager = (ViewPager) findViewById(pager);
        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(4);
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
        final float density = getResources().getDisplayMetrics().density;
        //indicator.setBackgroundColor(this.getResources().getColor(R.color.orange));
        indicator.setRadius(6 * density);
        indicator.setFillColor(this.getResources().getColor(R.color.colorPrimary));
        indicator.setStrokeColor(this.getResources().getColor(R.color.colorPrimary));
        indicator.setStrokeWidth(1 * density);
        indicator.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return true;
            }
        });

//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String date = df.format(Calendar.getInstance().getTime());
        //      Log.e("startdate:",":"+date);
        Job.updateStartDate(orderno, CommonMethods.getCurrentDateTimeInFormat(this
        ));
        Preferences.saveInt("PROGRESSJOBID", orderno, this);
        Log.e("PROGRESSJOBID", ":" + orderno);
    }

    private void setactions() {
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentpage = position;
                String title = "";
                switch (position) {
                    case 0:
                        title = "JOB DETAILS";
                        break;
                    case 1:
                        if (OperationMode.equals("LOAD"))
                            title = "LOADING";
                        else if (OperationMode.equals("SLM"))
                            title = "SLM";
                        else if (OperationMode.equals("FLM"))
                            title = "FLM";
                        else
                            title = "UNLOADING";
                        break;
                    case 2:
                        if (OperationMode.equals("LOAD"))
                            title = "LOADING ENVELOPE";
                        else if (OperationMode.equals("SLM"))
                            title = "SLM ENVELOPE";
                        else if (OperationMode.equals("FLM"))
                            title = "FLM";
                        else
                            title = "UNLOADING ENVELOPE";
                        break;
                    case 3:
                        if (OperationMode.equals("FLM"))
                            title = "FLM ENVELOPE";
                        else if (OperationMode.equals("SLM"))
                            title = "SLM";
                        else
                            title = "LOADING";
                        break;
                    case 4:
                        if (OperationMode.equals("FLM"))
                            title = "FLM";
                        else
                            title = "LOADING ENVELOPE";
                        break;
                }

                mTitle.setText(title);

                FragmentInterface fragment = (FragmentInterface) mAdapter.instantiateItem(mPager, position);
                if (fragment != null) {
                    fragment.fragmentBecameVisible();
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onBackPressed() {
        alert(1, "Confirm", "Are you sure you want to go back?");
    }

    public void alert(final int type, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProcessJobActivity.this);
        //alertDialog.setCancelable(true);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Cartridge.cancelAllScan(orderno);
                if (Job.isHistoryCleared(orderno)) {
                    Job.clearHistory(orderno, getString(R.string.unload));
                }
                if (type == 1) {
                    finish();
                } else {
                    Intent i = new Intent(ProcessJobActivity.this, HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    public void setpage(int count) {
        mPager.setCurrentItem(currentpage + count, true);
    }

    @Override
    public void onNetworkChanged() {
        if (NetworkUtil.getConnectivityStatusString(ProcessJobActivity.this))
            imgnetwork.setVisibility(View.VISIBLE);
        else
            imgnetwork.setVisibility(View.GONE);
    }

    @Override
    public void onResult(int result, String messege) {
        stopRefresh();
        if (result == 409) {
            authalert(this);
        } else {
            raiseSnakbar(messege);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Preferences.saveInt("PROGRESSJOBID", 0, this);
        unregisterScannerEvent();
    }

    @Override
    public void onPause() {
        super.onPause();
//        unregisterScannerEvent();
    }

}
