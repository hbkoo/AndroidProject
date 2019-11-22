package com.example.acer.bus1.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.acer.bus1.R;

public class AboutActivity extends AppCompatActivity {

    private LinearLayout use_help_layout,about_us_layout,version_up_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_about);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        use_help_layout = (LinearLayout) findViewById(R.id.use_help_layout);
        about_us_layout = (LinearLayout) findViewById(R.id.about_us_layout);
        version_up_layout = (LinearLayout) findViewById(R.id.version_up_layout);
        use_help_layout.setOnClickListener(new mClick());
        about_us_layout.setOnClickListener(new mClick());
        version_up_layout.setOnClickListener(new mClick());

    }

    private class mClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.use_help_layout:
                    Intent intent = new Intent(AboutActivity.this,UseHelpActivity.class);
                    startActivity(intent);
                    //Toast.makeText(AboutActivity.this,"使用帮助",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.about_us_layout:
                    Toast.makeText(AboutActivity.this,"关于我们",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.version_up_layout:
                    Toast.makeText(AboutActivity.this,"当前已是最新版本",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
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
