package com.example.acer.bus1.Activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.acer.bus1.R;

public class UseHelpActivity extends AppCompatActivity {

    private TextView summarize_tv,instruction_tv;
    private String summarize,instruction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_help);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_use_help);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setInformation();

        summarize_tv = (TextView) findViewById(R.id.summarize_tv);
        instruction_tv = (TextView) findViewById(R.id.instruction_tv);
        summarize_tv.setText(summarize);
        instruction_tv.setText(instruction);

    }

    private void setInformation() {
        summarize = "欢迎使用带一脚司机版，此应用主要是为了给出司机驾车行驶路线来供司机按路线发车行驶，" +
                "行驶过程司机在路线显示的站点停车接送乘客。";
        instruction = "·   点击首页右上角规划路线按钮就可以在有路线任务的前提下在地图上画出来行驶路线以方便" +
                "司机驾车行驶。\n·   在首页打开抽屉后可以选择相应的操作。\n·   抽屉的头部点击头像后会进入到" +
                "司机自己的信息设置页面。";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
