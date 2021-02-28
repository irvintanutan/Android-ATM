package com.novigosolutions.certiscisco.adapters;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.novigosolutions.certiscisco.fragments.JobDetailsFragment;
import com.novigosolutions.certiscisco.fragments.ScanOtherFragment;
import com.novigosolutions.certiscisco.fragments.TestCashFragment;
import com.novigosolutions.certiscisco.fragments.LoadUnloadingFragment;


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
                fragment = new JobDetailsFragment();
                break;
            case 1:
                bundle = new Bundle();
                fragment = new LoadUnloadingFragment();
                if (OperationMode.equals("LOAD"))
                    bundle.putString("FragmentType", "LOAD");
                else
                    bundle.putString("FragmentType", "UNLOAD");
                fragment.setArguments(bundle);
                break;
            case 2:
                if (OperationMode.equals("LOAD"))
                    fragment = new TestCashFragment();
                else
                    fragment = new ScanOtherFragment();
                break;
            case 3:
                bundle = new Bundle();
                bundle.putString("FragmentType", "LOAD");
                fragment = new LoadUnloadingFragment();
                fragment.setArguments(bundle);
                break;
            case 4:
                fragment = new TestCashFragment();
                break;

        }
        return fragment;
    }

    @Override
    public int getCount() {
        switch (OperationMode) {
            case "UNLOAD&LOAD":
                return 5;
            case "LOAD":
                return 3;
            case "UNLOAD":
                return 3;
            default:
                return 1;
        }
    }
}
