package com.example.acer.bus1.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.acer.bus1.Class.TrainAdapter;
import com.example.acer.bus1.R;

import java.util.ArrayList;
import java.util.List;

public class TrainInformationActivity extends AppCompatActivity {

    private TextView train_tv,station_list;
    private ImageView station_image;
    private ArrayList<String> trainList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TrainAdapter adapter;

    private StationReceiver receiver;
    private IntentFilter intentFilter;

    //tag=0时代表正在进行的路线；tag=1时代表查看历史路线
    private int tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_information);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        train_tv = (TextView) findViewById(R.id.train_actionbar);
        station_list = (TextView) findViewById(R.id.station_list);

        //注册广播
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.acer.bus1.current_station");
        receiver = new StationReceiver();
        registerReceiver(receiver,intentFilter);

        Bundle bundle = getIntent().getExtras();
        tag = bundle.getInt("tag");
        String train = bundle.getString("train");
        trainList = bundle.getStringArrayList("stations");
        train_tv.setText(train);
        station_list.setText(trainList.get(0) + " ---> " + trainList.get(trainList.size()-1));

        if (tag == 1) {
            TrainAdapter.currentStation = -2;
        } else {
            TrainAdapter.currentStation = 0;
        }

        //加载recyclerView列表
        recyclerView = (RecyclerView) findViewById(R.id.station_RV);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new TrainAdapter(trainList);
        recyclerView.setAdapter(adapter);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:

        }
        return super.onContextItemSelected(item);
    }

    //广播接收器
    class StationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (tag != 1) {
                String station = intent.getStringExtra("CurrentStation");
                TrainAdapter.currentStation = trainList.indexOf(station);
                recyclerView.smoothScrollToPosition(trainList.indexOf(station));
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(R.anim.push_left,R.anim.push_right);
        if (receiver != null) {
            //取消注册的广播
            unregisterReceiver(receiver);
        }
    }
}
