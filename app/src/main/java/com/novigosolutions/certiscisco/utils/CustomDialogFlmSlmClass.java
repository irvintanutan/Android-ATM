package com.novigosolutions.certiscisco.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
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
import com.novigosolutions.certiscisco.models.Denomination;
import com.novigosolutions.certiscisco.models.FLMSLMAdditionalDetails;
import com.novigosolutions.certiscisco.models.FLMSLMScan;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.models.OtherScan;
import com.novigosolutions.certiscisco.models.TestCash;
import com.novigosolutions.certiscisco.webservices.ATMListUpdateCaller;
import com.novigosolutions.certiscisco.webservices.ApiCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.novigosolutions.certiscisco.utils.Constants.FlmSlmDetails;
import static com.novigosolutions.certiscisco.utils.Constants.denomination;


public class CustomDialogFlmSlmClass extends Dialog implements View.OnClickListener {

    public Activity activity;
    public Button btncomplete, btncancel, btnok;
    TextView txttime, txtnodata;
    private ApiCallback callback;
    private int ATMOrderId;
    Chronometer chronometer;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DateFormat df = new SimpleDateFormat("HH:mm:ss");
    LinearLayout llbtns;
    Boolean isjobcompleted;
    long difference = 0;
    TextView txt_operation_mode, txt_atm_code, txt_atm_type, txt_status, txt_location, txt_zone, txt_bank, txt_remarks, txt_activation_time,
            engineerArrivalTime, staffName, teamArrivalTime, engineerArrivalTimeVal, staffNameVal, teamArrivalTimeVal, txt_assignment_date, txt_window_start_time, txt_window_end_time;

    TextView faultTypeVal, scannedEnvelopeVal, jammedCashDenominationVal, faultFoundVal, resolutionVal, remarksVal, slmRequiredVal;
    LinearLayout llduration;


    public CustomDialogFlmSlmClass(Activity activity, ApiCallback callback, int ATMOrderId, Boolean isjobcompleted) {
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
        setContentView(R.layout.custom_dialog_flm_slm);

        FlmSlmDetails = FLMSLMAdditionalDetails.getSingle(ATMOrderId);
        denomination = Denomination.getSingle(ATMOrderId);

        txt_operation_mode = findViewById(R.id.txt_operation_mode);
        txt_atm_code = findViewById(R.id.txt_atm_code);
        txt_atm_type = findViewById(R.id.txt_atm_type);
        txt_status = findViewById(R.id.txt_status);
        txt_location = findViewById(R.id.txt_location);
        txt_zone = findViewById(R.id.txt_zone);
        txt_bank = findViewById(R.id.txt_bank);
        txt_remarks = findViewById(R.id.txt_remarks);
        txt_activation_time = findViewById(R.id.txt_activation_time);
        llduration = findViewById(R.id.llduration);
        engineerArrivalTime = findViewById(R.id.textEngineerArrivalTime);
        teamArrivalTime = findViewById(R.id.textTeamArrivalTime);
        staffName = findViewById(R.id.textStaffName);
        engineerArrivalTimeVal = findViewById(R.id.engineerArrivalTime);
        teamArrivalTimeVal = findViewById(R.id.teamArrivalTime);
        staffNameVal = findViewById(R.id.staffName);
        faultTypeVal = findViewById(R.id.faultType);
        scannedEnvelopeVal = findViewById(R.id.scannedEnvelop);
        jammedCashDenominationVal = findViewById(R.id.jammedCashDenomination);
        faultFoundVal = findViewById(R.id.faultFound);
        resolutionVal = findViewById(R.id.resolution);
        remarksVal = findViewById(R.id.remarks);
        slmRequiredVal = findViewById(R.id.slmRequired);
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
        txt_activation_time.setText(": " + job.ActivationTime);
        txt_remarks.setText(": " + job.Remarks);
        engineerArrivalTimeVal.setText(FlmSlmDetails.EngineerArrivalTime);
        teamArrivalTimeVal.setText(FlmSlmDetails.TeamArrivalTime);
        staffNameVal.setText(FlmSlmDetails.StaffName);
        faultTypeVal.setText(FlmSlmDetails.FaultType);
        List<FLMSLMScan> scanned = FLMSLMScan.get(job.ATMOrderId);
        StringBuilder sb = new StringBuilder();
        for (FLMSLMScan scan : scanned) {
            sb.append(scan.ScanType);
            sb.append(" ");
            sb.append(scan.ScanValue);
            sb.append("\n");
        }
        scannedEnvelopeVal.setText(sb.toString());
        jammedCashDenominationVal.setText(constructDenomination());
        faultFoundVal.setText(FlmSlmDetails.FaultFound);
        resolutionVal.setText(FlmSlmDetails.Resolution);
        remarksVal.setText(FlmSlmDetails.AdditionalRemarks);
        slmRequiredVal.setText(FlmSlmDetails.SLMRequired);

        if (job.OperationMode.equals("FLM")) {
            engineerArrivalTime.setVisibility(View.GONE);
            teamArrivalTime.setVisibility(View.GONE);
            staffName.setVisibility(View.GONE);
            engineerArrivalTimeVal.setVisibility(View.GONE);
            teamArrivalTimeVal.setVisibility(View.GONE);
            staffNameVal.setVisibility(View.GONE);
        }

        btncomplete = findViewById(R.id.btn_complete);
        btncancel = findViewById(R.id.btn_cancel);
        btnok = findViewById(R.id.btn_ok);
        txttime = findViewById(R.id.txttime);
        txtnodata = findViewById(R.id.txtnodata);
        chronometer = findViewById(R.id.chronometer);
        btncomplete.setOnClickListener(this);
        btncancel.setOnClickListener(this);
        btnok.setOnClickListener(this);
        try {
            if (isjobcompleted) {
                llbtns.setVisibility(View.GONE);
                btnok.setVisibility(View.VISIBLE);
                llduration.setVisibility(View.GONE);
            } else {
                difference = sdf.parse(CommonMethods.getCurrentDateTimeInFormat(activity)).getTime() - sdf.parse(job.StartDate).getTime();
                txttime.setText(": " + String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(difference),
                        TimeUnit.MILLISECONDS.toMinutes(difference) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(difference) % TimeUnit.MINUTES.toSeconds(1)));
                chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

                    @Override

                    public void onChronometerTick(Chronometer chronometer) {
                        difference = difference + 1000;
                        txttime.setText(": " + String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(difference),
                                TimeUnit.MILLISECONDS.toMinutes(difference) % TimeUnit.HOURS.toMinutes(1),
                                TimeUnit.MILLISECONDS.toSeconds(difference) % TimeUnit.MINUTES.toSeconds(1)));

                    }

                });
                chronometer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String constructDenomination() {
        String result = "";

        if (denomination.HighReject) {
            result = "High Reject";
        } else if (denomination.NoCashFound) {
            result = "No Cash Found";
        } else {
            result += Integer.parseInt(denomination.text1000) > 0 ? "$1000x" + denomination.text1000 + ", " : "";
            result += Integer.parseInt(denomination.text100) > 0 ? "$100x" + denomination.text100 + ", " : "";
            result += Integer.parseInt(denomination.text50) > 0 ? "$50x" + denomination.text50 + ", " : "";
            result += Integer.parseInt(denomination.text10) > 0 ? "$10x" + denomination.text10 + ", " : "";
            result += Integer.parseInt(denomination.text5) > 0 ? "$5x" + denomination.text5 + ", " : "";
            result += Integer.parseInt(denomination.text2) > 0 ? "$2x" + denomination.text2 + ", " : "";
            result += Integer.parseInt(denomination.text1) > 0 ? "$1x" + denomination.text1 + ", " : "";
            result += Integer.parseInt(denomination.text0_50) > 0 ? "$0.50x" + denomination.text0_50 + ", " : "";
            result += Integer.parseInt(denomination.text0_20) > 0 ? "$0.20x" + denomination.text0_20 + ", " : "";
            result += Integer.parseInt(denomination.text0_10) > 0 ? "$0.10x" + denomination.text0_10 + ", " : "";
            result += Integer.parseInt(denomination.text0_05) > 0 ? "$0.05x" + denomination.text0_05 + ", " : "";
        }

        return result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_complete:
                if (isjobcompleted) {
                    dismiss();
                } else {
                    Job.updateEndDate(ATMOrderId, CommonMethods.getCurrentDateTimeInFormat(activity), (int) difference / 1000);
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