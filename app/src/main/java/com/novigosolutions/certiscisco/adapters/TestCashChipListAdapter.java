package com.novigosolutions.certiscisco.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.interfaces.RecyclerViewClickListenerLong;
import com.novigosolutions.certiscisco.models.TestCash;

import java.util.List;

public class TestCashChipListAdapter extends RecyclerView.Adapter<TestCashChipListAdapter.MyViewHolder> {
    List<TestCash> list;
    private static RecyclerViewClickListenerLong itemListener;
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtbarcode,txtscantype;
        ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            txtbarcode = (TextView) view.findViewById(R.id.txtbarcode);
            txtscantype = (TextView) view.findViewById(R.id.txtscantype);
            imageView = (ImageView) view.findViewById(R.id.imgclear);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(list.get(this.getLayoutPosition()).getId());
        }
    }

    public TestCashChipListAdapter(List<TestCash> list, RecyclerViewClickListenerLong itemListener) {
        this.list = list;
        TestCashChipListAdapter.itemListener = itemListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_cash_chip_list_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.txtbarcode.setText(list.get(position).ScanValue);
        holder.txtscantype.setText(list.get(position).ScanTypeName);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}