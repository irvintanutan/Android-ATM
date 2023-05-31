package com.novigosolutions.certiscisco.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.activities.ProcessJobActivity;
import com.novigosolutions.certiscisco.interfaces.FragmentInterface;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.service.UserLogService;
import com.novigosolutions.certiscisco.utils.Constants;
import com.novigosolutions.certiscisco.utils.CustomDialogFlmSlmClass;
import com.novigosolutions.certiscisco.utils.MultiSelectionSpinner;
import com.novigosolutions.certiscisco.utils.UserLog;
import com.novigosolutions.certiscisco.webservices.ApiCallback;
import com.novigosolutions.certiscisco.webservices.SendUpdateCaller;

import org.json.JSONObject;

import java.util.List;

import static com.novigosolutions.certiscisco.utils.Constants.FlmSlmDetails;
import static com.novigosolutions.certiscisco.utils.Constants.denomination;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class FaultTypeSLMFragment extends Fragment implements FragmentInterface, ApiCallback {

    @BindView(R.id.faultTypeSpinner)
    MultiSelectionSpinner faultType;

    @BindView(R.id.faultFound)
    EditText faultFound;
    @BindView(R.id.resolution)
    EditText resolution;
    @BindView(R.id.staffName)
    EditText staffName;
    @BindView(R.id.teamArrivalTime)
    Button teamArrivalTime;
    @BindView(R.id.engineerArrivalTime)
    Button engineerArrivalTime;
    @BindView(R.id.remarks)
    EditText remarks;
    @BindView(R.id.slmRequiredSpinner)
    Spinner slmRequired;

    int orderNo = 0;

    public FaultTypeSLMFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_fault_type_s_l_m, container, false);
        ButterKnife.bind(this, rootView);
        loadSpinnerIdTypes();
        loadSpinnerIdTypesSpinner();

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            orderNo = extras.getInt("orderno");
        }

        return rootView;
    }


    void timePicker(boolean isEngineer) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alertLayout = inflater.inflate(R.layout.time_picker, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final TimePicker datePicker = alertLayout.findViewById(R.id.timePicker);
        datePicker.setIs24HourView(true);

        builder.setView(alertLayout);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Set", null);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String appendMinute = "", appendHour = "";
                int hour = datePicker.getHour();
                int minute = datePicker.getMinute();

                if (hour < 10) {
                    appendHour = "0";
                }

                if (minute < 10) {
                    appendMinute = "0";
                }

                if (isEngineer) {
                    engineerArrivalTime.setText(appendHour + hour + ":" + appendMinute + minute);
                } else {
                    teamArrivalTime.setText(appendHour + hour + ":" + appendMinute + minute);
                }
            }
        });

        builder.show();
    }

    @OnClick(R.id.engineerArrivalTime)
    void setEngineerArrivalTime() {
        timePicker(true);
    }

    @OnClick(R.id.teamArrivalTime)
    void setTeamArrivalTime() {
        timePicker(false);
    }

    private void loadSpinnerIdTypesSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.yesno, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        slmRequired.setAdapter(adapter);
    }

    private void loadSpinnerIdTypes() {

        faultType.setItems(getResources().getStringArray(R.array.fault_type));

        faultType.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
            @Override
            public void selectedIndices(List<Integer> indices) {

            }

            @Override
            public void selectedStrings(List<String> strings) {

            }
        });
    }


    @OnClick(R.id.cancel_action)
    void cancel() {
        ((ProcessJobActivity) getActivity()).alert(UserLog.SLM.toString(), 1, "Confirm", "Confirm Exit Job?");
    }


    @OnClick(R.id.btn_next)
    void next() {
        if (resolution.getText().toString().isEmpty() || remarks.getText().toString().isEmpty() || faultFound.getText().toString().isEmpty() ||
                staffName.getText().toString().isEmpty()) {
            ((ProcessJobActivity) getActivity()).alert("All Fields Are Required");
        } else {
            setFLMResolutionDetails();
            CustomDialogFlmSlmClass cdd = new CustomDialogFlmSlmClass(getActivity(), this, orderNo, false);
            cdd.show();
        }
    }

    private void setFLMResolutionDetails() {
        FlmSlmDetails.Resolution = resolution.getText().toString();
        FlmSlmDetails.SLMRequired = slmRequired.getSelectedItem().toString();
        FlmSlmDetails.FaultType = faultType.getSelectedItemsAsString();
        FlmSlmDetails.AdditionalRemarks = remarks.getText().toString();
        FlmSlmDetails.FaultFound = faultFound.getText().toString();
        FlmSlmDetails.EngineerArrivalTime = engineerArrivalTime.getText().toString();
        FlmSlmDetails.StaffName = staffName.getText().toString();
        FlmSlmDetails.TeamArrivalTime = teamArrivalTime.getText().toString();

        FlmSlmDetails.ATMOrderId = orderNo;
        FlmSlmDetails.OperationMode = Job.getOperationMode(orderNo);
        denomination.ATMOrderId = orderNo;
        denomination.OperationMode = Job.getOperationMode(orderNo);
        FlmSlmDetails.save();
        denomination.save();

        UserLogService.save(UserLog.SLM.toString(), String.format("ATMOrderId : %s, Resolution : %s , SLMRequired : %s , FaultType : %s" +
                        " , AdditionalRemarks : %s , FaultFound : %s , EngineerArrivalTime : %s , StaffName : %s" +
                        " , TeamArrivalTime : %s", Job.getATMCode(orderNo), FlmSlmDetails.Resolution, FlmSlmDetails.SLMRequired, FlmSlmDetails.FaultType,
                FlmSlmDetails.AdditionalRemarks, FlmSlmDetails.FaultFound, FlmSlmDetails.EngineerArrivalTime,
                FlmSlmDetails.StaffName, FlmSlmDetails.TeamArrivalTime), "FAULT TYPE DATA",Job.getATMCode(orderNo),  getActivity());
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
                } else {
                    ((ProcessJobActivity) getActivity()).raiseSnakbar(messege);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (result == 409) {
            ((ProcessJobActivity) getActivity()).authalert(getActivity());
        }
    }

    @Override
    public void fragmentBecameVisible() {

    }
}