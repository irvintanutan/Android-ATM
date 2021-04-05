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
import com.novigosolutions.certiscisco.utils.Constants;

import static com.novigosolutions.certiscisco.utils.Constants.FlmSlmDetails;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ResolutionFragment extends Fragment implements FragmentInterface {

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
        ((ProcessJobActivity) getActivity()).alert(1,"Confirm", "Confirm Exit Job?");
    }


    @OnClick(R.id.btn_next)
    void next() {

        setFLMResolutionDetails();

        Toast.makeText(getActivity(), "I AM HERE", Toast.LENGTH_SHORT).show();
        Log.e("DENOMINATION" , Constants.denomination.toString());
        Log.e("DETAILS" , FlmSlmDetails.toString());
        Log.e("SCANNED" , FLMSLMScan.get(orderNo).toString());
        // show complete screen custom dialog
    }

    void setFLMResolutionDetails(){
        FlmSlmDetails.Resolution = resolution.getText().toString();
        FlmSlmDetails.SLMRequired = slmRequired.getSelectedItem().toString();
    }


    @Override
    public void fragmentBecameVisible() {

    }
}