package com.novigosolutions.certiscisco.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.models.Cartridge;
import com.novigosolutions.certiscisco.models.Seal;

import java.util.List;

public class JobSummaryListAdapter extends RecyclerView.Adapter<JobSummaryListAdapter.MyViewHolder> {
    List<Cartridge> cartridgeList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_SerialNo, txt_SealNo;

        public MyViewHolder(View view) {
            super(view);
            txt_SerialNo = (TextView) view.findViewById(R.id.txt_SerialNo);
            txt_SealNo = (TextView) view.findViewById(R.id.txt_SealNo);
        }
    }

    //    public TakeJobAdapter(Context context, ArrayList<HashMap<String, String>> data) {
//        this.data = data;
//    }
    public JobSummaryListAdapter(List<Cartridge> cartridgeList) {
        this.cartridgeList = cartridgeList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.job_summry_list_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.txt_SerialNo.setText(": "+cartridgeList.get(position).SerialNo);
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