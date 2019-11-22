package com.example.acer.bus1.Class;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.acer.bus1.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * 显示路线站点的适配器
 */

public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.ViewHolder> {

    private List<String> trainList;
    private int nCount;
    public static int currentStation = 0;
    private StringBuilder builder = new StringBuilder();

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView station_iv;
        TextView station_number;
        TextView station_tv;
        TextView connection_tv;
        public ViewHolder (View view) {
            super(view);
            station_iv = (ImageView) view.findViewById(R.id.station_image);
            station_number = (TextView) view.findViewById(R.id.station_number);
            station_tv = (TextView) view.findViewById(R.id.station_name);
            connection_tv = (TextView) view.findViewById(R.id.connect_tv);
        }
    }

    public TrainAdapter(List<String> trainList) {
        this.trainList = trainList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.train_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0) {
            holder.connection_tv.setVisibility(View.INVISIBLE);
        } else {
            holder.connection_tv.setVisibility(View.VISIBLE);
        }
        if (position != currentStation) {
            holder.station_iv.setVisibility(View.INVISIBLE);
        } else {
            holder.station_iv.setVisibility(View.VISIBLE);
        }
        builder.delete(0,builder.length());
        holder.station_tv.setText(builder.append("  ").append(trainList.get(position)).toString());
        holder.station_number.setText(String.valueOf(position+1));
    }

    @Override
    public int getItemCount() {
        nCount = trainList.size();
        return nCount;
    }

}
