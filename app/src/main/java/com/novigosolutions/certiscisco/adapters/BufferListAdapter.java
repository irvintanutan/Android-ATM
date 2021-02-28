package com.novigosolutions.certiscisco.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.interfaces.RecyclerViewClickListener;
import com.novigosolutions.certiscisco.models.Job;

import java.util.List;

public class BufferListAdapter extends RecyclerView.Adapter<BufferListAdapter.MyViewHolder> {
    List<Job> jobList;
    private static RecyclerViewClickListener itemListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_atm_type, txt_bank;

        public MyViewHolder(View view) {
            super(view);
            txt_atm_type = (TextView) view.findViewById(R.id.txt_atm_type);
            txt_bank = (TextView) view.findViewById(R.id.txt_bank);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(jobList.get(this.getLayoutPosition()).ATMOrderId);

        }
    }

    public BufferListAdapter(List<Job> jobList, RecyclerViewClickListener itemListener) {
        this.jobList = jobList;
        BufferListAdapter.itemListener = itemListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.buffer_list_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.txt_atm_type.setText(jobList.get(position).ATMTypeCode);
        holder.txt_bank.setText(jobList.get(position).Bank+" "+jobList.get(position).ATMType);
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }
}