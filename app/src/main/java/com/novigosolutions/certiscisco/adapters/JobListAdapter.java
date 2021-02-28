package com.novigosolutions.certiscisco.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.interfaces.RecyclerViewClickListener;
import com.novigosolutions.certiscisco.models.Job;

import java.util.List;

public class JobListAdapter extends RecyclerView.Adapter<JobListAdapter.MyViewHolder> {
    List<Job> jobList;
    Context context;
    private static RecyclerViewClickListener itemListener;
    String colorGreen = "#A5D6A7";
    String colorRed = "#EF9A9A";

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_order_type_head,txt_order_type,txt_operation_mode, txt_atm_code, txt_atm_type, txt_status, txt_location, txt_zone;
        LinearLayout llmain;

        public MyViewHolder(View view) {
            super(view);
            txt_order_type_head = (TextView) view.findViewById(R.id.txt_order_type_head);
            txt_order_type = (TextView) view.findViewById(R.id.txt_order_type);
            txt_operation_mode = (TextView) view.findViewById(R.id.txt_operation_mode);
            txt_atm_code = (TextView) view.findViewById(R.id.txt_atm_code);
            txt_atm_type = (TextView) view.findViewById(R.id.txt_atm_type);
            txt_status = (TextView) view.findViewById(R.id.txt_status);
            txt_location = (TextView) view.findViewById(R.id.txt_location);
            txt_zone = (TextView) view.findViewById(R.id.txt_zone);
            llmain = (LinearLayout) view.findViewById(R.id.llmain);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(jobList.get(this.getLayoutPosition()).ATMOrderId);

        }
    }

    public JobListAdapter(List<Job> jobList, Context context, RecyclerViewClickListener itemListener) {
        this.jobList = jobList;
        this.context = context;
        JobListAdapter.itemListener = itemListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.job_list_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if(jobList.get(position).OrderType!=null) {
            holder.txt_order_type.setText(jobList.get(position).OrderType);
            if (jobList.get(position).OrderType.equals("AD-HOC")) {
                holder.txt_order_type.setTextColor(context.getResources().getColor(R.color.red));
                holder.txt_order_type_head.setTextColor(context.getResources().getColor(R.color.red));
            } else {
                holder.txt_order_type.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                holder.txt_order_type_head.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            }
        }
        holder.txt_operation_mode.setText(jobList.get(position).OperationMode);
        holder.txt_atm_code.setText(jobList.get(position).ATMCode);
        holder.txt_atm_type.setText(jobList.get(position).ATMTypeCode);
        holder.txt_status.setText(jobList.get(position).Status);
        holder.txt_location.setText(jobList.get(position).Location);
        holder.txt_zone.setText(jobList.get(position).Zone);
        if (jobList.get(position).isOfflineSaved==1)
            holder.llmain.setBackgroundColor(Color.parseColor(colorRed));
        else if (jobList.get(position).Status.equals("DELIVERED"))
            holder.llmain.setBackgroundColor(Color.parseColor(colorGreen));
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }
}