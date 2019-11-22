package com.example.acer.bus1.Activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.bus1.R;

public class ReturnActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText opinion_et;
    private Button submit_btn;
    private String opinion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);
        toolbar = (Toolbar) findViewById(R.id.toolbar_return);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        opinion_et = (EditText) findViewById(R.id.opinion_et);
        submit_btn = (Button) findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(new mClick());
    }

    private class mClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.submit_btn:
                    opinion = opinion_et.getText().toString();
                    if ("".equals(opinion)) {
                        NULLOpinion();
                    } else {
                        SubmitOpinion();
                    }
                    break;
            }
        }
    }

    //输入的意见为空
    private void NULLOpinion() {
        LinearLayout layout = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.null_opinion,null);
        TextView OK_tv = (TextView) layout.findViewById(R.id.OK_tv);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(layout);
        final AlertDialog alertDialog = dialog.show();
        OK_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    //提交意见
    private void SubmitOpinion() {
        opinion_et.setText("");
        Toast.makeText(ReturnActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
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
