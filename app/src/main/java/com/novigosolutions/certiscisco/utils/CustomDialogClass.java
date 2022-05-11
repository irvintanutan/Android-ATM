package com.novigosolutions.certiscisco.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.adapters.JobSummaryListAdapter;
import com.novigosolutions.certiscisco.models.Cartridge;
import com.novigosolutions.certiscisco.models.CoinEnvelopes;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.models.OtherScan;
import com.novigosolutions.certiscisco.models.TestCash;
import com.novigosolutions.certiscisco.webservices.ATMListUpdateCaller;
import com.novigosolutions.certiscisco.webservices.ApiCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class CustomDialogClass extends Dialog implements View.OnClickListener {

    public Activity activity;
    public Button btncomplete, btncancel, btnok;
    TextView txttime, txtnodata;
    private final ApiCallback callback;
    private final int ATMOrderId;
    Chronometer chronometer;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DateFormat df = new SimpleDateFormat("HH:mm:ss");
    LinearLayout llloadmsg, llunloadmsg, llbtns;
    Boolean isjobcompleted;
    long difference = 0;
    TextView txt_operation_mode, txt_atm_code, txt_atm_type, txt_status, txt_location, txt_zone, txt_bank,
            txt_assignment_date, txt_window_start_time, txt_window_end_time;
    String colorBlack = "#000000";
    LinearLayout llduration;
    private RecyclerView loadingrecyclerview, unloadingrecyclerview;
    private JobSummaryListAdapter mAdapter;
    TextView loadingno, unloadingno;
    public CustomDialogClass(Activity activity, ApiCallback callback, int ATMOrderId, Boolean isjobcompleted) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.callback = callback;
        this.ATMOrderId = ATMOrderId;
        this.isjobcompleted = isjobcompleted;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);

        txt_operation_mode = findViewById(R.id.txt_operation_mode);
        txt_atm_code = findViewById(R.id.txt_atm_code);
        txt_atm_type = findViewById(R.id.txt_atm_type);
        txt_status = findViewById(R.id.txt_status);
        txt_location = findViewById(R.id.txt_location);
        txt_zone = findViewById(R.id.txt_zone);
        txt_bank = findViewById(R.id.txt_bank);
        llduration = findViewById(R.id.llduration);
        txt_assignment_date = findViewById(R.id.assignment_date);
        txt_window_start_time = findViewById(R.id.window_start_time);
        txt_window_end_time = findViewById(R.id.window_end_time);
        Job job = Job.getSingle(ATMOrderId);
        txt_operation_mode.setText(": " + job.OperationMode);
        txt_atm_code.setText(": " + job.ATMCode);
        txt_atm_type.setText(": " + job.ATMTypeCode);
        txt_status.setText(": " + job.Status);
        txt_location.setText(": " + job.Location);
        txt_zone.setText(": " + job.Zone);
        txt_bank.setText(": " + job.Bank + " " + job.ATMType);
        txt_assignment_date.setText(": " + job.AssignmentDate);
        txt_window_start_time.setText(": " + job.WindowStartTime);
        txt_window_end_time.setText(": " + job.WindowEndTime);
        btncomplete = findViewById(R.id.btn_complete);
        btncancel = findViewById(R.id.btn_cancel);
        btnok = findViewById(R.id.btn_ok);
        txttime = findViewById(R.id.txttime);
        txtnodata = findViewById(R.id.txtnodata);
        chronometer = findViewById(R.id.chronometer);
        llloadmsg = findViewById(R.id.llloadmsg);
        llunloadmsg = findViewById(R.id.llunloadmsg);
        llbtns = findViewById(R.id.llbtns);
        btncomplete.setOnClickListener(this);
        btncancel.setOnClickListener(this);
        btnok.setOnClickListener(this);
        loadingno= findViewById(R.id.loadingno);
        unloadingno= findViewById(R.id.unloadingno);
        loadingrecyclerview = findViewById(R.id.loadingrecyclerview);
        unloadingrecyclerview = findViewById(R.id.unloadingrecyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(activity);
        loadingrecyclerview.setLayoutManager(mLayoutManager);
        loadingrecyclerview.setItemAnimator(new DefaultItemAnimator());
        unloadingrecyclerview.setLayoutManager(mLayoutManager2);
        unloadingrecyclerview.setItemAnimator(new DefaultItemAnimator());
       // DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//        loadingrecyclerview.addItemDecoration(dividerItemDecoration);
//        unloadingrecyclerview.addItemDecoration(dividerItemDecoration);

        try {

            if (isjobcompleted) {
                llbtns.setVisibility(View.GONE);
                btnok.setVisibility(View.VISIBLE);
                llduration.setVisibility(View.GONE);
            } else {
                difference =  sdf.parse(CommonMethods.getCurrentDateTimeInFormat(activity)).getTime() - sdf.parse(job.StartDate).getTime();
                txttime.setText(": " +String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(difference),
                        TimeUnit.MILLISECONDS.toMinutes(difference) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(difference) % TimeUnit.MINUTES.toSeconds(1)));
                chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

                    @Override

                    public void onChronometerTick(Chronometer chronometer) {
                            difference = difference + 1000;
                            txttime.setText(": " +String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(difference),
                                    TimeUnit.MILLISECONDS.toMinutes(difference) % TimeUnit.HOURS.toMinutes(1),
                                    TimeUnit.MILLISECONDS.toSeconds(difference) % TimeUnit.MINUTES.toSeconds(1)));

                    }

                });
                chronometer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int count = 0;
        Boolean isloadingdetailsexist = false, isunloadingdetailsexist = false;
        count = Cartridge.get(ATMOrderId, "UNLOAD").size();
        if (count > 0) {
            unloadingno.setText(count + " Cartridge(s) unloaded");
            isunloadingdetailsexist = true;

            List<Cartridge> unloadingcartridgeList = Cartridge.get(ATMOrderId, "UNLOAD");
            if (unloadingcartridgeList.size() > 0) {
                mAdapter = new JobSummaryListAdapter(unloadingcartridgeList);
                unloadingrecyclerview.setAdapter(mAdapter);
            } else {
                unloadingrecyclerview.setVisibility(View.GONE);
            }
        }
        count = OtherScan.getCount(ATMOrderId, "TESTCASH").size();
        Log.e("TESTCASH", ":" + count);
        if (count > 0) {
            TextView textView = new TextView(activity);
            textView.setTextColor(Color.parseColor(colorBlack));
            textView.setTextSize(16);
            textView.setText(count + " Test cash scanned");
            llunloadmsg.addView(textView);
            TextView dtextView = new TextView(activity);
            dtextView.setTextColor(Color.parseColor(colorBlack));
            dtextView.setTextSize(14);
            dtextView.setText(OtherScan.getByType(ATMOrderId, "TESTCASH"));
            llunloadmsg.addView(dtextView);
            View v = new View(activity);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
            );
            params.setMargins(0, 5, 0, 5);
            v.setLayoutParams(params);
            llunloadmsg.addView(v);
            isunloadingdetailsexist = true;
        }
        count = OtherScan.getCount(ATMOrderId, "PASSBOOK").size();
        Log.e("PASSBOOK", ":" + count);
        if (count > 0) {
            TextView textView = new TextView(activity);
            textView.setTextColor(Color.parseColor(colorBlack));
            textView.setTextSize(16);
            textView.setText(count + " Passbook scanned");
            llunloadmsg.addView(textView);
            TextView dtextView = new TextView(activity);
            dtextView.setTextColor(Color.parseColor(colorBlack));
            dtextView.setTextSize(14);
            dtextView.setText(OtherScan.getByType(ATMOrderId, "PASSBOOK"));
            llunloadmsg.addView(dtextView);
            View v = new View(activity);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
            );
            params.setMargins(0, 5, 0, 5);
            v.setLayoutParams(params);
            llunloadmsg.addView(v);
            isunloadingdetailsexist = true;
        }
        count = OtherScan.getCount(ATMOrderId, "RJR").size();
        Log.e("RJR", ":" + count);
        if (count > 0) {
            TextView textView = new TextView(activity);
            textView.setTextColor(Color.parseColor(colorBlack));
            textView.setTextSize(16);
            textView.setText(count + " RJR scanned");
            llunloadmsg.addView(textView);
            TextView dtextView = new TextView(activity);
            dtextView.setTextColor(Color.parseColor(colorBlack));
            dtextView.setTextSize(14);
            dtextView.setText(OtherScan.getByType(ATMOrderId, "RJR"));
            llunloadmsg.addView(dtextView);
            View v = new View(activity);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
            );
            params.setMargins(0, 5, 0, 5);
            v.setLayoutParams(params);
            llunloadmsg.addView(v);
            isunloadingdetailsexist = true;
        }
        count = OtherScan.getCount(ATMOrderId, "RETAIN").size();
        Log.e("RETAINCARD", ":" + count);
        if (count > 0) {
            TextView textView = new TextView(activity);
            textView.setTextColor(Color.parseColor(colorBlack));
            textView.setTextSize(16);
            textView.setText(count + " Retain card scanned");
            llunloadmsg.addView(textView);
            TextView dtextView = new TextView(activity);
            dtextView.setTextColor(Color.parseColor(colorBlack));
            dtextView.setTextSize(14);
            dtextView.setText(OtherScan.getByType(ATMOrderId, "RETAIN"));
            llunloadmsg.addView(dtextView);
            View v = new View(activity);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
            );
            params.setMargins(0, 5, 0, 5);
            v.setLayoutParams(params);
            llunloadmsg.addView(v);
            isunloadingdetailsexist = true;
        }
        count = OtherScan.getCount(ATMOrderId, "MISCSCAN").size();
        Log.e("MISCSCAN", ":" + count);
        if (count > 0) {
            TextView textView = new TextView(activity);
            textView.setTextColor(Color.parseColor(colorBlack));
            textView.setTextSize(16);
            textView.setText(count + " Misc scan scanned");
            llunloadmsg.addView(textView);
            TextView dtextView = new TextView(activity);
            dtextView.setTextColor(Color.parseColor(colorBlack));
            dtextView.setTextSize(14);
            dtextView.setText(OtherScan.getByType(ATMOrderId, "MISCSCAN"));
            llunloadmsg.addView(dtextView);
            View v = new View(activity);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
            );
            params.setMargins(0, 5, 0, 5);
            v.setLayoutParams(params);
            llunloadmsg.addView(v);
            isunloadingdetailsexist = true;
        }
        count = OtherScan.getCount(ATMOrderId, "MISCINPUT").size();
        Log.e("MISCINPUT", ":" + count);
        if (count > 0) {
            TextView textView = new TextView(activity);
            textView.setTextColor(Color.parseColor(colorBlack));
            textView.setTextSize(16);
            textView.setText(count + " Misc input");
            llunloadmsg.addView(textView);
            TextView dtextView = new TextView(activity);
            dtextView.setTextColor(Color.parseColor(colorBlack));
            dtextView.setTextSize(14);
            dtextView.setText(OtherScan.getByType(ATMOrderId, "MISCINPUT"));
            llunloadmsg.addView(dtextView);
            View v = new View(activity);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
            );
            params.setMargins(0, 5, 0, 5);
            v.setLayoutParams(params);
            llunloadmsg.addView(v);
            v.setBackgroundColor(Color.parseColor("#B3B3B3"));
            isunloadingdetailsexist = true;
        }

        count = Cartridge.get(ATMOrderId, "LOAD").size();
        if (count > 0) {
            loadingno.setText(count + " Cartridge(s) loaded");
            isloadingdetailsexist = true;
            List<Cartridge> loadingcartridgeList = Cartridge.get(ATMOrderId, "LOAD");
            if (loadingcartridgeList.size() > 0) {
                mAdapter = new JobSummaryListAdapter(loadingcartridgeList);
                loadingrecyclerview.setAdapter(mAdapter);
            } else {
                loadingrecyclerview.setVisibility(View.GONE);
            }
        }
        count = CoinEnvelopes.getCount(ATMOrderId).size();
        if (count > 0) {
            TextView textView = new TextView(activity);
            textView.setTextColor(Color.parseColor(colorBlack));
            textView.setTextSize(16);
            textView.setText(count + " Coin envelope(s) scanned");
            llloadmsg.addView(textView);
            TextView dtextView = new TextView(activity);
            dtextView.setTextColor(Color.parseColor(colorBlack));
            dtextView.setTextSize(14);
            dtextView.setText(CoinEnvelopes.getEnvList(ATMOrderId));
            llloadmsg.addView(dtextView);
            View v = new View(activity);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
            );
            params.setMargins(0, 5, 0, 5);
            v.setLayoutParams(params);
            llloadmsg.addView(v);
            isloadingdetailsexist = true;
        }
        count = TestCash.getCount(ATMOrderId, "TESTCASH").size();
        if (count > 0) {
            TextView textView = new TextView(activity);
            textView.setTextColor(Color.parseColor(colorBlack));
            textView.setTextSize(16);
            textView.setText(count + " Test cash scanned");
            llloadmsg.addView(textView);
            TextView dtextView = new TextView(activity);
            dtextView.setTextColor(Color.parseColor(colorBlack));
            dtextView.setTextSize(14);
            dtextView.setText(TestCash.getByType(ATMOrderId, "TESTCASH"));
            llloadmsg.addView(dtextView);
            View v = new View(activity);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
            );
            params.setMargins(0, 5, 0, 5);
            v.setLayoutParams(params);
            llloadmsg.addView(v);
            isloadingdetailsexist = true;
        }
        count = TestCash.getCount(ATMOrderId, "JUNKCASH").size();
        if (count > 0) {
            TextView textView = new TextView(activity);
            textView.setTextColor(Color.parseColor(colorBlack));
            textView.setTextSize(16);
            textView.setText(count + " Jammed cash scanned");
            llloadmsg.addView(textView);
            TextView dtextView = new TextView(activity);
            dtextView.setTextColor(Color.parseColor(colorBlack));
            dtextView.setTextSize(14);
            dtextView.setText(TestCash.getByType(ATMOrderId, "JUNKCASH"));
            llloadmsg.addView(dtextView);
            View v = new View(activity);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
            );
            params.setMargins(0, 5, 0, 5);
            v.setLayoutParams(params);
            llloadmsg.addView(v);
            isloadingdetailsexist = true;
        }
        if (!isloadingdetailsexist) {
            llloadmsg.setVisibility(View.GONE);
        }
        if (!isunloadingdetailsexist) {
            llunloadmsg.setVisibility(View.GONE);
        }
        if (!isloadingdetailsexist && !isunloadingdetailsexist) {
            txtnodata.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_complete:
                if (isjobcompleted) {
                    dismiss();
                } else {
                    Job.updateEndDate(ATMOrderId,CommonMethods.getCurrentDateTimeInFormat(activity), (int) difference / 1000);

                    chronometer.stop();
                    if (NetworkUtil.getConnectivityStatusString(activity)) {
                        ATMListUpdateCaller.instance().UpdateATMList(callback, activity, ATMOrderId);
                    } else {
                        Job.saveasOffline(ATMOrderId);
                        Job.updateStatus(ATMOrderId);
                        activity.finish();
                        Toast.makeText(activity, "Saved offline", Toast.LENGTH_SHORT).show();
                    }
                    dismiss();
                }
                break;
            case R.id.btn_cancel:
            case R.id.btn_ok:
                dismiss();
                break;
            default:
                break;
        }

    }
}