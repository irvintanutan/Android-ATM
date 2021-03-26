package com.novigosolutions.certiscisco.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.activities.ProcessJobActivity;
import com.novigosolutions.certiscisco.interfaces.FragmentInterface;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class DenominationFragment extends Fragment implements FragmentInterface {

    public DenominationFragment() {
        // Required empty public constructor
        super(R.layout.fragment_denomination);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_denomination, container, false);

        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick(R.id.btn_next)
    void next() {
        ((ProcessJobActivity) getActivity()).setpage(1);
    }

    @OnClick(R.id.cancel_action)
    void cancel() {
        ((ProcessJobActivity) getActivity()).alert(1,"Confirm", "Confirm Exit Job?");
    }

    @Override
    public void fragmentBecameVisible() {

    }
}