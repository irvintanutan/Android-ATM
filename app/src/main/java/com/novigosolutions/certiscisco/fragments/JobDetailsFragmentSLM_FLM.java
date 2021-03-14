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
import android.widget.Toast;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.activities.ProcessJobActivity;
import com.novigosolutions.certiscisco.interfaces.FragmentInterface;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class JobDetailsFragmentSLM_FLM extends Fragment implements FragmentInterface {

    public JobDetailsFragmentSLM_FLM() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_job_details_s_l_m__f_l_m, container, false);

        ButterKnife.bind(this, rootView);
        return rootView;
    }


    @OnClick(R.id.btn_next)
    void next() {
        ((ProcessJobActivity) getActivity()).setpage(1);
    }


    @Override
    public void fragmentBecameVisible() {

    }
}