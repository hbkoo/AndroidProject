package com.example.acer.enterprise.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

public class ReleaseActivity extends AppCompatActivity {

    private TextView date_tv;
    private Button release_btn;
    private ImageView back_iv;
    private EditText title_et, content_et;
    private SQLiteDatabase database = null;
    private boolean isAdd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);

        date_tv = (TextView) findViewById(R.id.add_date_tv);
        title_et = (EditText) findViewById(R.id.add_title_et);
        content_et = (EditText) findViewById(R.id.add_content_et);
        back_iv = (ImageView) findViewById(R.id.release_back_iv);
        release_btn = (Button) findViewById(R.id.release_btn);

        back_iv.setOnClickListener(new mClick());
        release_btn.setOnClickListener(new mClick());
        getCurrentTime();

    }

    private class mClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.release_back_iv:
                    Cancel();
                    break;
                case R.id.release_btn:
                    if ("".equals(title_et.getText().toString())) {
                        Toast.makeText(ReleaseActivity.this, "请输入消息的标题！",
                                Toast.LENGTH_SHORT).show();
                    } else if ("".equals(content_et.getText().toString())) {
                        Toast.makeText(ReleaseActivity.this, "请输入消息的内容！",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Save();
                    }
                    break;
            }
        }
    }

    //保存发布的信息到数据库中
    private void Save() {
        if (database == null) {
            //打开数据库连接
            database = DataProcessing.getINFDatabase(ReleaseActivity.this, 1);
        }
        Information information = new Information();
        information.setDate(getCurrentTime());
        information.setTitle(title_et.getText().toString());
        information.setContent(content_et.getText().toString());

        if (DataProcessing.InsertInformation(database, information)) {
            isAdd = true;
            title_et.setText("");
            content_et.setText("");
            Toast.makeText(ReleaseActivity.this, "发布成功！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ReleaseActivity.this, "数据库连接失败！", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 取消按钮点击处理
     */
    private void Cancel() {
        if (!"".equals(title_et.getText().toString()) || !"".equals(content_et.getText().toString())) {

            View view = LayoutInflater.from(ReleaseActivity.this)
                    .inflate(R.layout.judge_layout, null, false);
            TextView continue_tv = (TextView) view.findViewById(R.id.judge_continue_tv);
            TextView quit_tv = (TextView) view.findViewById(R.id.judge_quit_tv);

            AlertDialog.Builder builder = new AlertDialog.Builder(ReleaseActivity.this);
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
                    intent.putExtra("isAdd", isAdd);
                    setResult(1, intent);
                    finish();
                }
            });

        } else {
            Intent intent = new Intent();
            intent.putExtra("isAdd", isAdd);
            setResult(1, intent);
            finish();
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

    /**
     * 获取当前日期
     *
     * @return 返回当前日期字符串
     */
    private String getCurrentTime() {
        String time;
        Calendar calendar = Calendar.getInstance();
        time = calendar.get(Calendar.YEAR) + "年" +
                (calendar.get(Calendar.MONTH) + 1) + "月" +
                calendar.get(Calendar.DAY_OF_MONTH) + "日";
        date_tv.setText(time);
        return time;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Cancel();
        }
        return false;
    }
}
