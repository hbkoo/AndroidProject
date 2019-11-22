package com.example.acer.bus1.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.bus1.Class.HistoryLineAdapter;
import com.example.acer.bus1.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

public class HistoryLineActivity extends AppCompatActivity {

    private ArrayList<ArrayList<String>> stationList = new ArrayList<>();
    private ArrayList<String> isTask = new ArrayList<>();
    private ArrayList<String> trainNum = new ArrayList<>();
    private ArrayList<String> time = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView hintTV;
    private HistoryLineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_line);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = (RecyclerView) findViewById(R.id.station_history);
        hintTV = (TextView) findViewById(R.id.hint_tv);

        loadStationLine();

        if (stationList.size() == 0) {
            recyclerView.setVisibility(View.INVISIBLE);
            hintTV.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            hintTV.setVisibility(View.INVISIBLE);
        }

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new HistoryLineAdapter(stationList,trainNum,isTask,time,HistoryLineActivity.this);
        recyclerView.setAdapter(adapter);

    }

    //menu菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.delete_history:
                IsDeleteAll();
                break;
            default:
        }
        return super.onContextItemSelected(item);
    }

    //提示是否删除路线信息
    private void IsDeleteAll() {
        if (stationList.size() == 0) {
            Toast.makeText(HistoryLineActivity.this,"暂无历史路线信息",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        LinearLayout layout = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.delete_allhistory,null);
        TextView cancle_tv = (TextView) layout.findViewById(R.id.cancel_tv);
        TextView delete_tv = (TextView) layout.findViewById(R.id.delete_tv);
        AlertDialog.Builder dialog = new AlertDialog.Builder(HistoryLineActivity.this);
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
                deleteStationLine();
                stationList.clear();
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.INVISIBLE);
                hintTV.setVisibility(View.VISIBLE);
            }
        });

    }

    //加载menu布局文件
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu,menu);
        return true;
    }

    //读取文件获取全部历史路线信息
    public void loadStationLine() {
        stationList.clear();
        isTask.clear();
        trainNum.clear();
        FileInputStream inputStream ;
        BufferedReader reader = null;
        ArrayList<String> strings;
        try {
            inputStream = openFileInput("station");
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String str;
            while ((str = reader.readLine()) != null) {
                strings = new ArrayList<>();
                Collections.addAll(strings,str.split(","));
                isTask.add(strings.get(0));
                trainNum.add(strings.get(1));
                time.add(strings.get(2));
                strings.remove(2);
                strings.remove(1);
                strings.remove(0);
                stationList.add(strings);
            }
            Collections.reverse(isTask);
            Collections.reverse(trainNum);
            Collections.reverse(time);
            Collections.reverse(stationList);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //删除全部历史路线信息
    public void deleteStationLine() {
        FileOutputStream outputStream;
        BufferedWriter writer = null;
        try {
            outputStream = openFileOutput("station", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write("");
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
