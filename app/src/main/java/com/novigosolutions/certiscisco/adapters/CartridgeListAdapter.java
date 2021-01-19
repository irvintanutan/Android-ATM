package com.novigosolutions.certiscisco.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.models.Cartridge;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.models.Seal;

import java.util.List;

public class CartridgeListAdapter extends RecyclerView.Adapter<CartridgeListAdapter.MyViewHolder> {
    List<Cartridge> cartridgeList;
    Context context;
    int orderno;
    String colorWhite = "#FFFFFF", colorGreen = "#43A047", colorOrange = "#EF6C00";
    String CartType;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_cartno, txt_serialno;//txt_DuffleSeal;
        LinearLayout ll_sealno, llbg;
        ImageView imgDone;

        public MyViewHolder(View view) {
            super(view);
            txt_cartno = (TextView) view.findViewById(R.id.txt_cartno);
            txt_serialno = (TextView) view.findViewById(R.id.txt_serialno);
            //txt_DuffleSeal = (TextView) view.findViewById(R.id.txt_DuffleSeal);
            ll_sealno = (LinearLayout) view.findViewById(R.id.ll_sealno);
            llbg = (LinearLayout) view.findViewById(R.id.llbg);
            imgDone = (ImageView) view.findViewById(R.id.imgdone);
        }
    }

    public CartridgeListAdapter(List<Cartridge> cartridgeList, Context context, int orderno, String CartType) {
        this.context = context;
        this.cartridgeList = cartridgeList;
        this.orderno = orderno;
        this.CartType = CartType;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cartridge_list_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String cartwithd=cartridgeList.get(position).CartNo;
        if(cartridgeList.get(position).Deno!=null&&!cartridgeList.get(position).Deno.equals(""))
            cartwithd=cartwithd+"\n("+cartridgeList.get(position).Deno+")";
        Boolean isHistoryCleared = Job.isHistoryCleared(orderno) && CartType.equals("UNLOAD");
        holder.txt_cartno.setText(cartwithd);
        holder.txt_serialno.setText(cartridgeList.get(position).SerialNo);
        if (isHistoryCleared)
            holder.txt_serialno.setBackgroundResource(R.drawable.squar_border);
        List<Seal> seals = Seal.get(cartridgeList.get(position).ATMOrderId, cartridgeList.get(position).CartId, cartridgeList.get(position).CartType);
        Boolean isCartScanned = cartridgeList.get(position).isScanCompleted == 1;
        holder.ll_sealno.removeAllViews();
        for (int i = 0; i < seals.size(); i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(2, 2, 2, 2);
            TextView tv = new TextView(context);
            tv.setText(seals.get(i).SealNo);
            tv.setLayoutParams(params);
            tv.setPadding(5, 2, 2, 2);
            if (isCartScanned) {
                tv.setTextColor(Color.parseColor(colorWhite));
            } else if (seals.get(i).isScanned == 1) {
                tv.setTextColor(Color.parseColor(colorGreen));
            } else {
                tv.setTextColor(Color.parseColor(colorOrange));
            }
            if (isHistoryCleared) tv.setBackgroundResource(R.drawable.squar_border);
            holder.ll_sealno.addView(tv);
        }
        if (isCartScanned) {
            holder.imgDone.setVisibility(View.VISIBLE);
            holder.llbg.setBackgroundColor(Color.parseColor(colorGreen));
            holder.txt_serialno.setTextColor(Color.parseColor(colorWhite));
            holder.txt_cartno.setTextColor(Color.parseColor(colorWhite));
        } else {
            holder.imgDone.setVisibility(View.INVISIBLE);
            holder.llbg.setBackgroundColor(Color.parseColor(colorWhite));
            if (cartridgeList.get(position).isScanned == 1) {
                holder.txt_serialno.setTextColor(Color.parseColor(colorGreen));
            } else {
                holder.txt_serialno.setTextColor(Color.parseColor(colorOrange));
            }
            holder.txt_cartno.setTextColor(Color.parseColor(colorOrange));
        }
    }

    @Override
    public int getItemCount() {
        return cartridgeList.size();
    }
}