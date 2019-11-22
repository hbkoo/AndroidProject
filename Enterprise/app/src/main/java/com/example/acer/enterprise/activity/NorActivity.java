package com.example.acer.enterprise.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.support.v7.widget.Toolbar;

import com.example.acer.enterprise.R;
import com.example.acer.enterprise.unit.DataProcessing;
import com.example.acer.enterprise.unit.InfAdapter;
import com.example.acer.enterprise.unit.Information;

import java.util.ArrayList;
import java.util.List;

public class NorActivity extends AppCompatActivity {

    private List<Information> informationList = new ArrayList<>();
    private SQLiteDatabase database = null;

    private LinearLayout empty_layout;
    private RecyclerView recyclerView;
    private InfAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.nor_toolbar);
        setSupportActionBar(toolbar);

        empty_layout = (LinearLayout) findViewById(R.id.nor_empty_layout);
        recyclerView = (RecyclerView) findViewById(R.id.nor_recycle_view);

        LinearLayoutManager manager = new LinearLayoutManager(NorActivity.this);
        recyclerView.setLayoutManager(manager);
        adapter = new InfAdapter(NorActivity.this,informationList,false);
        recyclerView.setAdapter(adapter);

        LoadInformation();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nor_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nor_exit) {
            finish();
            startActivity(new Intent(NorActivity.this,MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    //加载全部信息
    private void LoadInformation(){
        if (database == null) {
            database = DataProcessing.getINFDatabase(NorActivity.this,0);
        }

        informationList.clear();
        informationList.addAll(DataProcessing.LoadAllInformation(database));

        if (informationList.size() == 0) {
            empty_layout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            empty_layout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

}
