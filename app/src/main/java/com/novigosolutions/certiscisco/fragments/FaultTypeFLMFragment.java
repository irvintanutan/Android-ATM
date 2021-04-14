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
import android.widget.EditText;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.activities.ProcessJobActivity;
import com.novigosolutions.certiscisco.interfaces.FragmentInterface;
import com.novigosolutions.certiscisco.utils.Constants;
import com.novigosolutions.certiscisco.utils.MultiSelectionSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.novigosolutions.certiscisco.utils.Constants.FlmSlmDetails;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class FaultTypeFLMFragment extends Fragment implements FragmentInterface {

    @BindView(R.id.faultTypeSpinner)
    MultiSelectionSpinner faultType;

    @BindView(R.id.faultFound)
    EditText faultFound;

    @BindView(R.id.remarks)
    EditText remarks;

    int orderNo;

    public FaultTypeFLMFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fault_type_flm, container, false);
        ButterKnife.bind(this, rootView);
        loadSpinnerIdTypes();
        initialize();
        return rootView;
    }


    @OnClick(R.id.cancel_action)
    void cancel() {
        ((ProcessJobActivity) getActivity()).alert(1, "Confirm", "Confirm Exit Job?");
    }


    @OnClick(R.id.btn_next)
    void next() {
        if (faultFound.getText().toString().isEmpty() || remarks.getText().toString().isEmpty()) {
            ((ProcessJobActivity) getActivity()).alert("All Fields Are Required");
        } else {
            add();
            ((ProcessJobActivity) getActivity()).setpage(1);
        }
    }

    private void initialize() {
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            orderNo = extras.getInt("orderno");
            Log.e("orderno", ":" + orderNo);
        }

        try {
            List<String> faultTypeList;
            faultTypeList = Arrays.asList(FlmSlmDetails.FaultType.split(","));
            faultType.setSelection(faultTypeList);

            faultFound.setText(FlmSlmDetails.FaultFound);
            remarks.setText(FlmSlmDetails.AdditionalRemarks);

        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
    }

    private void add() {
        FlmSlmDetails.FaultType = faultType.getSelectedItemsAsString() == null ? "" : faultType.getSelectedItemsAsString();
        FlmSlmDetails.FaultFound = faultFound.getText().toString() == null ? "" : faultFound.getText().toString();
        FlmSlmDetails.AdditionalRemarks = remarks.getText().toString() == null ? "" : remarks.getText().toString();
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

    @Override
    public void fragmentBecameVisible() {

    }
}