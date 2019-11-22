package com.example.acer.enterprise.unit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.acer.enterprise.R;
import com.example.acer.enterprise.activity.ChangeINFActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 信息适配器
 */

public class InfAdapter extends RecyclerView.Adapter<InfAdapter.ViewHolder> {

    private CheckBox allCheckBox = null;
    private Context context;
    private List<Information> informationList = new ArrayList<>(); //适配器的数据列表
    private List<Information> removeList = new ArrayList<>();      //删除的记录列表
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Boolean> isCheckeds = new HashMap<>();
    private boolean isCheckable = false;
    private boolean changeable;

    static class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout item_layout;
        private TextView date_tv, title_tv;
        private CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            item_layout = (LinearLayout) itemView.findViewById(R.id.item_layout);
            date_tv = (TextView) itemView.findViewById(R.id.date_item);
            title_tv = (TextView) itemView.findViewById(R.id.title_item);
            checkBox = (CheckBox) itemView.findViewById(R.id.information_checkbox);
        }
    }

    public InfAdapter(Context context, List<Information> informationList, boolean changeable) {
        this.context = context;
        this.informationList = informationList;
        this.changeable = changeable;
        for (int i = 0; i < informationList.size(); i++) {
            isCheckeds.put(i, false);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.infor_item, parent, false);

        final ViewHolder holder = new ViewHolder(view);

        holder.item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Information information = informationList.get(holder.getAdapterPosition());
                Intent intent = new Intent(context, ChangeINFActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("changeable", changeable);
                bundle.putSerializable("information", information);
                intent.putExtra("bundle", bundle);
                ((Activity) context).startActivityForResult(intent, 0);
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = holder.getAdapterPosition();
                Information information = informationList.get(position);
                if (isChecked) {
                    isCheckeds.put(position, true);
                    if (!removeList.contains(information)) {
                        removeList.add(information);
                    }
                } else {
                    isCheckeds.put(position, false);
                    removeList.remove(information);
                }
                //设置全选多选框的状态
                if (allCheckBox != null) {
                    if (removeList.size() == informationList.size()) {
                        allCheckBox.setChecked(true);
                        allCheckBox.setText("取消全选");
                    } else {
                        allCheckBox.setChecked(false);
                        allCheckBox.setText("全选");
                    }
                }

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Information information = informationList.get(position);
        holder.title_tv.setText(information.getTitle());
        holder.date_tv.setText(information.getDate());
        if (isCheckable) {
            holder.item_layout.setClickable(false);
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(isCheckeds.get(position));
        } else {
            holder.item_layout.setClickable(true);
            holder.checkBox.setVisibility(View.GONE);
            holder.checkBox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return informationList.size();
    }

    //设置是否显示多选框
    public void setCheckable(boolean isCheckable) {
        this.isCheckable = isCheckable;
        notifyDataSetChanged();
    }

    //获取要删除的列表
    public List<Information> getRemoveList() {
        return removeList;
    }

    //清空删除的列表
    public void clearRemoveList() {
        removeList.clear();
    }

    /**
     * 全选或全不选
     *
     * @param isChecked 状态
     */
    public void setAllChecked(boolean isChecked) {
        removeList.clear();
        for (int i = 0; i < isCheckeds.size(); i++) {
            isCheckeds.put(i, isChecked);
        }
        if (isChecked) {
            removeList.addAll(informationList);
            allCheckBox.setText("取消全选");
        } else {
            allCheckBox.setText("全选");
        }
        notifyDataSetChanged();
    }

    //传递进来主界面的全选复选框
    public void setAllCheckBox(CheckBox allCheckBox) {
        this.allCheckBox = allCheckBox;
    }

}
