package com.novigosolutions.certiscisco.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.activities.ProcessJobActivity;
import com.novigosolutions.certiscisco.interfaces.FragmentInterface;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class FaultTypeSLMFragment extends Fragment implements FragmentInterface {

    @BindView(R.id.faultTypeSpinner)
    Spinner faultType;


    public FaultTypeSLMFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_fault_type_s_l_m, container, false);
        ButterKnife.bind(this, rootView);

        loadSpinnerIdTypes();

        return rootView;
    }

    private void loadSpinnerIdTypes() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.fault_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        faultType.setAdapter(adapter);
    }


    @OnClick(R.id.cancel_action)
    void cancel() {
        ((ProcessJobActivity) getActivity()).alert(1,"Confirm", "Confirm Exit Job?");
    }


    @OnClick(R.id.btn_next)
    void next() {
        ((ProcessJobActivity) getActivity()).setpage(1);
    }

    @Override
    public void fragmentBecameVisible() {

    }
}