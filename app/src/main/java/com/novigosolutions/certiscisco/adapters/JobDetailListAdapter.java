package com.novigosolutions.certiscisco.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.models.Cartridge;
import com.novigosolutions.certiscisco.models.Seal;

import java.util.List;

public class JobDetailListAdapter extends RecyclerView.Adapter<JobDetailListAdapter.MyViewHolder> {
    List<Cartridge> cartridgeList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_SerialNo, txt_CartNo, txt_DuffleSeal, txt_SealNo;
        LinearLayout llduffle;

        public MyViewHolder(View view) {
            super(view);
            txt_SerialNo = (TextView) view.findViewById(R.id.txt_SerialNo);
            txt_CartNo = (TextView) view.findViewById(R.id.txt_CartNo);
            txt_DuffleSeal = (TextView) view.findViewById(R.id.txt_DuffleSeal);
            txt_SealNo = (TextView) view.findViewById(R.id.txt_SealNo);
            llduffle = (LinearLayout) view.findViewById(R.id.llduffle);
        }
    }

    //    public TakeJobAdapter(Context context, ArrayList<HashMap<String, String>> data) {
//        this.data = data;
//    }
    public JobDetailListAdapter(List<Cartridge> cartridgeList) {
        this.cartridgeList = cartridgeList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.job_deatail_list_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String cartwithd=cartridgeList.get(position).CartNo;
        if(cartridgeList.get(position).Deno!=null&&!cartridgeList.get(position).Deno.equals("")&&!cartridgeList.get(position).Deno.equals("null"))cartwithd=cartwithd+" ("+cartridgeList.get(position).Deno+")";
        holder.txt_SerialNo.setText(": "+cartridgeList.get(position).SerialNo);
        holder.txt_CartNo.setText(": "+cartwithd);
        String duffle=cartridgeList.get(position).DuffleSeal;
        Log.e("duffle",":"+duffle);
        if(duffle==null)
        {
            holder.llduffle.setVisibility(View.GONE);
        }
        else if (duffle.equals("")) {
            holder.llduffle.setVisibility(View.GONE);
        }
        else
        {
            holder.txt_DuffleSeal.setText(": "+duffle);
        }
        List<Seal> seals = Seal.get(cartridgeList.get(position).ATMOrderId, cartridgeList.get(position).CartId, cartridgeList.get(position).CartType);
        String strSeal = "";
        for (int i = 0; i < seals.size(); i++) {
            if (strSeal.equals("")) {
                strSeal = seals.get(i).SealNo;
            } else {
                strSeal = strSeal + ", " + seals.get(i).SealNo;
            }
        }
        holder.txt_SealNo.setText(": "+strSeal);

    }

    @Override
    public int getItemCount() {
        return cartridgeList.size();
    }
}