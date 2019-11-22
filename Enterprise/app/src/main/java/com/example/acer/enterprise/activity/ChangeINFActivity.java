package com.example.acer.enterprise.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.enterprise.R;
import com.example.acer.enterprise.unit.DataProcessing;
import com.example.acer.enterprise.unit.Information;

import java.util.Calendar;

public class ChangeINFActivity extends AppCompatActivity {

    private TextView date_tv, activity_title;
    private EditText title_et, content_et;
    private Button change_btn;
    private SQLiteDatabase database = null;
    private boolean isChange = false;
    private Information information;
    private String title, content, date;

    private boolean changeable;//标记是用户模式还是管理员模式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_inf);

        activity_title = (TextView) findViewById(R.id.change_activity_title);
        date_tv = (TextView) findViewById(R.id.change_date_tv);
        ImageView back_iv = (ImageView) findViewById(R.id.change_back_iv);
        title_et = (EditText) findViewById(R.id.change_title_et);
        content_et = (EditText) findViewById(R.id.change_content_et);
        change_btn = (Button) findViewById(R.id.change_btn);
        back_iv.setOnClickListener(new mClick());
        change_btn.setOnClickListener(new mClick());

        Bundle bundle = getIntent().getBundleExtra("bundle");
        changeable = bundle.getBoolean("changeable", false);
        information = (Information) bundle.getSerializable("information");

        if (!changeable) {
            activity_title.setText("详细信息");
            title_et.setEnabled(false);
            content_et.setEnabled(false);
            change_btn.setVisibility(View.GONE);
        }

        if (information != null) {
            date = information.getDate();
            title = information.getTitle();
            content = information.getContent();
            date_tv.setText(date);
            title_et.setText(title);
            content_et.setText(content);
        }


    }

    //获取当前日期
    private String getCurrentTime() {
        String time;
        Calendar calendar = Calendar.getInstance();
        time = calendar.get(Calendar.YEAR) + "年" +
                (calendar.get(Calendar.MONTH) + 1) + "月" +
                calendar.get(Calendar.DAY_OF_MONTH) + "日";
        date_tv.setText(time);
        return time;
    }

    private class mClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.change_back_iv:
                    if (changeable) {
                        Cancel();
                    } else {
                        finish();
                    }
                    break;
                case R.id.change_btn:
                    Change();
                    break;
            }
        }
    }

    //修改信息记录
    private void Change() {
        if (!title.equals(title_et.getText().toString()) ||
                !content.equals(content_et.getText().toString())) {

            if (database == null) {
                database = DataProcessing.getINFDatabase(ChangeINFActivity.this, 1);
            }

            information.setTitle(title_et.getText().toString());
            information.setContent(content_et.getText().toString());
            information.setDate(getCurrentTime());

            if (DataProcessing.UpdateInformation(database, information)) {
                isChange = true;
                title = title_et.getText().toString();
                content = content_et.getText().toString();
                Toast.makeText(ChangeINFActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ChangeINFActivity.this, "修改失败！", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ChangeINFActivity.this, "还未修改任何信息！", Toast.LENGTH_SHORT).show();
        }
    }

    //取消对话框判断
    private void Cancel() {

        if (!title.equals(title_et.getText().toString()) ||
                !content.equals(content_et.getText().toString())) {
            View view = LayoutInflater.from(ChangeINFActivity.this)
                    .inflate(R.layout.judge_layout, null, false);
            TextView continue_tv = (TextView) view.findViewById(R.id.judge_continue_tv);
            TextView quit_tv = (TextView) view.findViewById(R.id.judge_quit_tv);
            TextView infor_content = (TextView) view.findViewById(R.id.inform_content);

            infor_content.setText("放弃修改吗？");
            continue_tv.setText("继续修改");
            quit_tv.setText("放弃修改");

            AlertDialog.Builder builder = new AlertDialog.Builder(ChangeINFActivity.this);
            builder.setView(view);
            builder.setCancelable(false);
            final AlertDialog dialog = builder.show();

            continue_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            quit_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent();
                    intent.putExtra("isChange", isChange);
                    setResult(2, intent);
                    finish();
                }
            });
        } else {
            Intent intent = new Intent();
            intent.putExtra("isChange", isChange);
            setResult(2, intent);
            finish();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Cancel();
        }
        return super.onKeyDown(keyCode, event);
    }
}
