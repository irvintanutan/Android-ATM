package com.novigosolutions.certiscisco.adapters;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.novigosolutions.certiscisco.R;

import java.util.List;

public class GridAdapter extends BaseAdapter {

    String[] menuList = {"Job List", "Pending", "Delivered","Buffer Sets","Offline","Settings"};
    //int[] count ;
    List<Integer> countList;
    Context context;
    int[] iconList = {
            R.drawable.joblist,
            R.drawable.pending,
            R.drawable.delivered,
            R.drawable.buffer,
            R.drawable.logout,
            R.drawable.settings

    };
    int gridItemHeight=0;
    private static LayoutInflater inflater = null;

    public GridAdapter(Activity activity, List<Integer> countList) {
        context = activity;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.countList=countList;

//        ActionBar actionBar = ((Activity) context).getActionBar();
//        gridItemHeight =(((Activity) context).getWindowManager()
//                .getDefaultDisplay().getHeight()-actionBar.getHeight())/3;
       // Log.e("gridItemHeight",":"+gridItemHeight);
    }

    @Override
    public int getCount() {
        return menuList.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView tv;
        ImageView img;
        TextView txtcount;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.gridview_items, null);
        holder.tv = (TextView) rowView.findViewById(R.id.gridview_text);
        holder.txtcount = (TextView) rowView.findViewById(R.id.txtcount);
        holder.img = (ImageView) rowView.findViewById(R.id.gridview_image);

       // rowView.setMinimumHeight(gridItemHeight);

        holder.tv.setText(menuList[position]);
        if(countList.get(position)>0) {
            holder.txtcount.setText(String.valueOf(countList.get(position)));
        }
        else
        {
            holder.txtcount.setVisibility(View.INVISIBLE);
        }
        holder.img.setImageResource(iconList[position]);
        return rowView;
    }

}