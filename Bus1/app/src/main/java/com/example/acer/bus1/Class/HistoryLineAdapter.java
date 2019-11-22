package com.example.acer.bus1.Class;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.bus1.Activity.TrainInformationActivity;
import com.example.acer.bus1.R;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
/**
 * 历史路线信息适配器
 */

public class HistoryLineAdapter extends RecyclerView.Adapter<HistoryLineAdapter.ViewHolder>{

    private ArrayList<ArrayList<String>> stationList;
    private ArrayList<String> trainNum;
    private ArrayList<String> isTask;
    private ArrayList<String> time;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView start,end,train_num,time,drive;
        CardView item;

        public ViewHolder(View view) {
            super(view);
            item = (CardView) view.findViewById(R.id.history_item);
            start = (TextView) view.findViewById(R.id.start_tv);
            end = (TextView) view.findViewById(R.id.end_tv);
            train_num = (TextView) view.findViewById(R.id.train_num);
            time = (TextView) view.findViewById(R.id.time_tv);
            drive = (TextView) view.findViewById(R.id.drive);
        }
    }

    public HistoryLineAdapter(ArrayList<ArrayList<String>> stationList,ArrayList<String> trainNum,
                              ArrayList<String> isTask,ArrayList<String> time,Context context) {
        this.stationList = stationList;
        this.trainNum = trainNum;
        this.isTask = isTask;
        this.time = time;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item,parent,false);

        final ViewHolder holder = new ViewHolder(view);

        // TODO 点击事件要放到onCreate方法中去，不要放到OnBindVIewHolder中，onBindViewHOlder方法是每到滑动到
        // TODO 条目这里就会被执行
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(context, TrainInformationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("tag",1);
                bundle.putString("train",trainNum.get(position));
                bundle.putStringArrayList("stations", stationList.get(position));
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        holder.item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final int position = holder.getAdapterPosition();
                Activity activity = (Activity) context;
                LinearLayout layout = (LinearLayout) activity.getLayoutInflater()
                        .inflate(R.layout.delete_onehistory,null);
                TextView cancle_tv = (TextView) layout.findViewById(R.id.cancel_tv);
                TextView delete_tv = (TextView) layout.findViewById(R.id.delete_tv);
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setView(layout);
                final AlertDialog alertDialog = dialog.show();
                cancle_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                delete_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        stationList.remove(position);
                        isTask.remove(position);
                        trainNum.remove(position);
                        updateStationLine();
                        notifyDataSetChanged();
                        Toast.makeText(context,"删除成功",Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }
        });
        holder.drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)context).finish();
                int position = holder.getAdapterPosition();
                Intent intent = new Intent("com.example.acer.bus1.Activity.TaskLine");
                intent.putExtra("stations",stationList.get(position));
                context.sendBroadcast(intent);
                Toast.makeText(context,"请立即发车\n前往首页查看路线规划",Toast.LENGTH_LONG).show();
            }
        });

        return holder;
    }

    /**
     * 点击事件的监听不应该放在onBindViewHolder中，应该放在onCreateViewHolder中！！
     */

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (Boolean.parseBoolean(isTask.get(position))) {
            holder.item.setCardBackgroundColor(Color.parseColor("#66CC66"));
            holder.drive.setVisibility(View.VISIBLE);
        } else {
            holder.item.setCardBackgroundColor(Color.parseColor("#bebebe"));
            holder.drive.setVisibility(View.INVISIBLE);
        }
        holder.train_num.setText(trainNum.get(position));
        holder.time.setText(time.get(position));
        holder.start.setText(stationList.get(position).get(0));
        holder.end.setText(stationList.get(position).get(stationList.get(position).size()-1));

    }

    @Override
    public int getItemCount() {
        return stationList.size();
    }

    //更新历史路线文件
    private void updateStationLine() {
        StringBuilder builder = new StringBuilder();
        FileOutputStream outputStream;
        BufferedWriter writer = null;
        try {
            outputStream = context.openFileOutput("station",Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            for (int i = isTask.size()-1; i >= 0; i--) {
                builder.append(isTask.get(i)).append(",")
                        .append(trainNum.get(i)).append(",")
                        .append(time.get(i)).append(",");
                for (String station : stationList.get(i)) {
                    builder.append(station).append(",");
                }
                builder.append("\n");
            }
            writer.write(builder.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
