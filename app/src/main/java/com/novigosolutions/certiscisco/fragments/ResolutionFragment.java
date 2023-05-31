package com.novigosolutions.certiscisco.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.activities.ProcessJobActivity;
import com.novigosolutions.certiscisco.interfaces.FragmentInterface;
import com.novigosolutions.certiscisco.models.FLMSLMScan;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.service.UserLogService;
import com.novigosolutions.certiscisco.utils.Constants;
import com.novigosolutions.certiscisco.utils.CustomDialogClass;
import com.novigosolutions.certiscisco.utils.CustomDialogFlmSlmClass;
import com.novigosolutions.certiscisco.utils.UserLog;
import com.novigosolutions.certiscisco.webservices.ApiCallback;
import com.novigosolutions.certiscisco.webservices.SendUpdateCaller;

import org.json.JSONObject;

import static com.novigosolutions.certiscisco.utils.Constants.FlmSlmDetails;
import static com.novigosolutions.certiscisco.utils.Constants.denomination;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ResolutionFragment extends Fragment implements FragmentInterface, ApiCallback {

    @BindView(R.id.slmRequiredSpinner)
    Spinner slmRequired;

    @BindView(R.id.resolution)
    EditText resolution;

    int orderNo = 0;


    public ResolutionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_resolution, container, false);
        ButterKnife.bind(this, rootView);
        loadSpinnerIdTypes();

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            orderNo = extras.getInt("orderno");
        }

        return rootView;
    }

    private void loadSpinnerIdTypes() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.yesno, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        slmRequired.setAdapter(adapter);
    }

    @OnClick(R.id.cancel_action)
    void cancel() {
        ((ProcessJobActivity) getActivity()).alert(UserLog.RESOLUTION.toString(), 1, "Confirm", "Confirm Exit Job?");
    }


    @OnClick(R.id.btn_next)
    void next() {

        if (resolution.getText().toString().isEmpty()) {
            ((ProcessJobActivity) getActivity()).alert("All Fields Are Required");
        } else {
            setFLMResolutionDetails();
            CustomDialogFlmSlmClass cdd = new CustomDialogFlmSlmClass(getActivity(), this, orderNo, false);
            cdd.show();
        }
    }

    void setFLMResolutionDetails() {
        FlmSlmDetails.Resolution = resolution.getText().toString();
        FlmSlmDetails.SLMRequired = slmRequired.getSelectedItem().toString();
        UserLogService.save(UserLog.RESOLUTION.toString(), String.format("ATMOrderId : %s , Resolution : %s , " +
                        "SLMRequired : %s", Job.getATMCode(orderNo), FlmSlmDetails.Resolution, FlmSlmDetails.SLMRequired),
                "RESOLUTION DATA", null, getActivity());
        FlmSlmDetails.ATMOrderId = orderNo;
        FlmSlmDetails.OperationMode = Job.getOperationMode(orderNo);
        denomination.ATMOrderId = orderNo;
        denomination.OperationMode = Job.getOperationMode(orderNo);
        FlmSlmDetails.save();
        denomination.save();
    }


    @Override
    public void fragmentBecameVisible() {

    }

    @Override
    public void onResult(int result, String resultdata) {
        if (result == 200) {
            try {
                JSONObject obj = new JSONObject(Constants.requestBody);
                String strresult = obj.getString("Result");
                String messege = obj.getString("Message");
                if (strresult.equals("Success")) {
                    Job.updateStatus(orderNo);
                    getActivity().finish();
                    SendUpdateCaller.instance().sendUpdate(getActivity());
                    UserLogService.save(UserLog.SYNC.toString(), String.format("ATMOrderId : %s", Job.getATMCode(orderNo)),
                            "SUCCESS", null, getContext());
                } else {
                    ((ProcessJobActivity) getActivity()).raiseSnakbar(messege);
                    UserLogService.save(UserLog.SYNC.toString(), String.format("ATMOrderId : %s, Message : %s", Job.getATMCode(orderNo), messege),
                            "FAILED", null, getContext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (result == 409) {
            ((ProcessJobActivity) getActivity()).authalert(getActivity());
        }
    }
}