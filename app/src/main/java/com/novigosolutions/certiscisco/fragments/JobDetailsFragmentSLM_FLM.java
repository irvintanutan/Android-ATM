package com.novigosolutions.certiscisco.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.activities.ProcessJobActivity;
import com.novigosolutions.certiscisco.interfaces.FragmentInterface;
import com.novigosolutions.certiscisco.models.FLMSLMAdditionalDetails;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class JobDetailsFragmentSLM_FLM extends Fragment implements FragmentInterface {

    @BindView(R.id.txt_order_type)
    TextView orderType;
    @BindView(R.id.txt_operation_mode)
    TextView operationMode;
    @BindView(R.id.txt_status)
    TextView status;
    @BindView(R.id.txt_atm_code)
    TextView atmCode;
    @BindView(R.id.txt_atm_type)
    TextView atmType;
    @BindView(R.id.txt_location)
    TextView location;
    @BindView(R.id.txt_zone)
    TextView zone;
    @BindView(R.id.txt_bank)
    TextView bank;
    @BindView(R.id.txt_remarks)
    TextView remarks;
    @BindView(R.id.txt_activation_time)
    TextView activationTime;

    public JobDetailsFragmentSLM_FLM() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_job_details_s_l_m__f_l_m, container, false);
        ButterKnife.bind(this, rootView);

        initialize();

        return rootView;
    }

    private void initialize(){
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            int orderNo = extras.getInt("orderno");
            Job job = Job.getSingle(orderNo);
            operationMode.setText(job.OperationMode);
            atmCode.setText(job.ATMCode);
            atmType.setText(job.ATMTypeCode);
            status.setText(job.Status);
            location.setText(job.Location);
            zone.setText(job.Zone);
            bank.setText(job.Bank + " " + job.ATMType);
            // remarks.setText(job.Remarks);
            // activationTime.setText(job.ActivationTime);

            Constants.FlmSlmDetails = new FLMSLMAdditionalDetails();
        }
    }

    @OnClick(R.id.btn_next)
    void next() {
        ((ProcessJobActivity) getActivity()).setpage(1);
    }


    @Override
    public void fragmentBecameVisible() {

    }
}