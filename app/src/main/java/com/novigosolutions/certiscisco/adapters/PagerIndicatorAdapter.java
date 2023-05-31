package com.novigosolutions.certiscisco.adapters;

import android.os.Bundle;
import android.util.Log;

import com.novigosolutions.certiscisco.fragments.DenominationFragment;
import com.novigosolutions.certiscisco.fragments.EnvelopeFragment;
import com.novigosolutions.certiscisco.fragments.FaultTypeFLMFragment;
import com.novigosolutions.certiscisco.fragments.FaultTypeSLMFragment;
import com.novigosolutions.certiscisco.fragments.JobDetailsFragment;
import com.novigosolutions.certiscisco.fragments.JobDetailsFragmentSLM_FLM;
import com.novigosolutions.certiscisco.fragments.LoadUnloadingFragment;
import com.novigosolutions.certiscisco.fragments.ResolutionFragment;
import com.novigosolutions.certiscisco.fragments.ScanOtherFragment;
import com.novigosolutions.certiscisco.fragments.TestCashFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


public class PagerIndicatorAdapter extends FragmentStatePagerAdapter {
    private String OperationMode;

    public PagerIndicatorAdapter(FragmentManager activity, String OperationMode) {
        super(activity);
        this.OperationMode = OperationMode;
        Log.e("OperationMode", OperationMode);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        Log.e("pos", ":" + position);
        Bundle bundle;
        switch (position) {
            case 0:
                if (OperationMode.equals("FLM") || OperationMode.equals("SLM")) {
                    fragment = new JobDetailsFragmentSLM_FLM();
                } else
                    fragment = new JobDetailsFragment();
                break;
            case 1:
                if (OperationMode.equals("FLM")) {
                    fragment = new FaultTypeFLMFragment();
                } else if (OperationMode.equals("SLM")) {
                    fragment = new DenominationFragment();
                } else {
                    bundle = new Bundle();
                    fragment = new LoadUnloadingFragment();
                    if (OperationMode.equals("LOAD"))
                        bundle.putString("FragmentType", "LOAD");
                    else
                        bundle.putString("FragmentType", "UNLOAD");
                    fragment.setArguments(bundle);
                }
                break;
            case 2:
                if (OperationMode.equals("LOAD"))
                    fragment = new TestCashFragment();
                else if (OperationMode.equals("FLM"))
                    fragment = new DenominationFragment();
                else if (OperationMode.equals("SLM"))
                    fragment = new EnvelopeFragment();
                else
                    fragment = new ScanOtherFragment();
                break;
            case 3:
                if (OperationMode.equals("FLM")) {
                    fragment = new EnvelopeFragment();
                } else  if (OperationMode.equals("SLM")) {
                    fragment = new FaultTypeSLMFragment();
                } else {
                    bundle = new Bundle();
                    bundle.putString("FragmentType", "LOAD");
                    fragment = new LoadUnloadingFragment();
                    fragment.setArguments(bundle);
                }
                break;
            case 4:
                if (OperationMode.equals("FLM")) {
                    fragment = new ResolutionFragment();
                } else
                    fragment = new TestCashFragment();
                break;

        }
        return fragment;
    }

    @Override
    public int getCount() {
        switch (OperationMode) {
            case "UNLOAD&LOAD":
            case "FLM":
                return 5;
            case "LOAD":
            case "UNLOAD":
                return 3;
            case "SLM":
                return 4;
            default:
                return 1;
        }
    }
}
