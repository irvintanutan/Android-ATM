package com.novigosolutions.certiscisco.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.activities.ProcessJobActivity;
import com.novigosolutions.certiscisco.adapters.JobDetailListAdapter;
import com.novigosolutions.certiscisco.interfaces.FragmentInterface;
import com.novigosolutions.certiscisco.models.Cartridge;
import com.novigosolutions.certiscisco.models.CoinEnvelopes;
import com.novigosolutions.certiscisco.models.Job;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class JobDetailsFragment extends Fragment implements FragmentInterface {
    private RecyclerView loadingrecyclerview, unloadingrecyclerview;
    private JobDetailListAdapter mAdapter;
    Button next;
    CardView cardviewloading, cardviewunloading, cardviewnodata;
    TextView txt_operation_mode, txt_atm_code, txt_atm_type, txt_status, txt_location,
            txt_zone, txt_bank, txt_clealred_history, txt_assignment_date, txt_window_start_time, txt_window_end_time;
    int orderno;
    LinearLayout llcoinen, llcoinhead;

    public JobDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_job_details, container, false);
        loadingrecyclerview = rootView.findViewById(R.id.loadingrecyclerview);
        unloadingrecyclerview = rootView.findViewById(R.id.unloadingrecyclerview);
        cardviewloading = rootView.findViewById(R.id.cardviewloading);
        cardviewunloading = rootView.findViewById(R.id.cardviewunloading);
        cardviewnodata = rootView.findViewById(R.id.cardviewnodata);

        txt_operation_mode = rootView.findViewById(R.id.txt_operation_mode);
        txt_atm_code = rootView.findViewById(R.id.txt_atm_code);
        txt_atm_type = rootView.findViewById(R.id.txt_atm_type);
        txt_status = rootView.findViewById(R.id.txt_status);
        txt_location = rootView.findViewById(R.id.txt_location);
        txt_zone = rootView.findViewById(R.id.txt_zone);
        txt_bank = rootView.findViewById(R.id.txt_bank);
        txt_assignment_date = rootView.findViewById(R.id.assignment_date);
        txt_window_start_time = rootView.findViewById(R.id.window_start_time);
        txt_window_end_time = rootView.findViewById(R.id.window_end_time);
        llcoinen = rootView.findViewById(R.id.llcoinenvelopes);
        llcoinhead = rootView.findViewById(R.id.llcoinhead);
        txt_clealred_history = rootView.findViewById(R.id.txt_clealred_history);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity());
        loadingrecyclerview.setLayoutManager(mLayoutManager);
        loadingrecyclerview.setItemAnimator(new DefaultItemAnimator());
        unloadingrecyclerview.setLayoutManager(mLayoutManager2);
        unloadingrecyclerview.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        loadingrecyclerview.addItemDecoration(dividerItemDecoration);
        unloadingrecyclerview.addItemDecoration(dividerItemDecoration);
        Bundle extras = getActivity().getIntent().getExtras();
        Boolean isData = false;
        if (extras != null) {
            orderno = extras.getInt("orderno");
            List<CoinEnvelopes> coinEnvelopsList = CoinEnvelopes.get(orderno);
            Log.e("orderno", ":" + orderno);
            Log.e("mycoinsize", ":" + coinEnvelopsList.size());
            for (int i = 0; i < coinEnvelopsList.size(); i++) {
                TextView textView = new TextView(getActivity());
                if (i == 0) {
                    textView.setText(": " + coinEnvelopsList.get(i).CoinEnvelope);
                } else {
                    textView.setText("  " + coinEnvelopsList.get(i).CoinEnvelope);
                }
                llcoinen.addView(textView);
                isData = true;
            }
            if (!isData) {
                Log.e("no data", "dhvchhd");
                llcoinhead.setVisibility(View.GONE);
            }
            List<Cartridge> loadingcartridgeList = Cartridge.get(orderno, getString(R.string.load));
            if (loadingcartridgeList.size() > 0) {
                mAdapter = new JobDetailListAdapter(loadingcartridgeList);
                loadingrecyclerview.setAdapter(mAdapter);
                isData = true;
            } else {
                cardviewloading.setVisibility(View.GONE);
            }
            if (!Job.isHistoryCleared(orderno)) {
                List<Cartridge> unloadingcartridgeList = Cartridge.get(orderno, getString(R.string.unload));
                if (unloadingcartridgeList.size() > 0) {
                    mAdapter = new JobDetailListAdapter(unloadingcartridgeList);
                    unloadingrecyclerview.setAdapter(mAdapter);
                    isData = true;
                } else {
                    cardviewunloading.setVisibility(View.GONE);
                }
            } else {
                txt_clealred_history.setVisibility(View.VISIBLE);
                unloadingrecyclerview.setVisibility(View.GONE);
                isData = true;
            }
            Job job = Job.getSingle(orderno);
            txt_operation_mode.setText(job.OperationMode);
            txt_atm_code.setText(job.ATMCode);
            txt_atm_type.setText(job.ATMTypeCode);
            txt_status.setText(job.Status);
            txt_location.setText(job.Location);
            txt_zone.setText(job.Zone);
            txt_bank.setText(job.Bank + " " + job.ATMType);
            txt_assignment_date.setText(job.AssignmentDate);
            txt_window_start_time.setText(job.WindowStartTime);
            txt_window_end_time.setText(job.WindowEndTime);
        }
        if (!isData) {
            cardviewnodata.setVisibility(View.VISIBLE);
        }
        next = rootView.findViewById(R.id.btn_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProcessJobActivity) getActivity()).setpage(1);
            }
        });
        return rootView;
    }

    @Override
    public void fragmentBecameVisible() {
        if (Job.isHistoryCleared(orderno)) {
            txt_clealred_history.setVisibility(View.VISIBLE);
            unloadingrecyclerview.setVisibility(View.GONE);
        }
    }
}
