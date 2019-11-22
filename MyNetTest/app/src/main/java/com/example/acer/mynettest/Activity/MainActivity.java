package com.example.acer.mynettest.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.mynettest.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final int ERROR_IN_GET = 0;
    private static final int SUCCESS_IN_GET = 1;

    EditText netText_et;
    TextView result_tv, hont_tv;
    Button query_btn;

    HttpURLConnection connection;
    String result;
    String address;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        netText_et = (EditText) findViewById(R.id.netText_et);
        result_tv = (TextView) findViewById(R.id.result_tv);
        hont_tv = (TextView) findViewById(R.id.hont_tv);
        query_btn = (Button) findViewById(R.id.query_btn);
        query_btn.setOnClickListener(new mClick());
        progressDialog = new ProgressDialog(MainActivity.this);
        hont_tv.setText("你好啊，欢迎欢迎！Nice to meet you！哈哈哈哈哈！");
        hideKeyboard();
    }

    private class mClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.query_btn:
                    ConnectivityManager manager = (ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isAvailable()) {
//                        setHontTV();
//                        hideKeyboard();
//                        Connection();
                        getData();
                    } else {
                        Toast.makeText(MainActivity.this, "请检查网络连接", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
            }
        }
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = null;
                try {
                    URL url = new URL("http://58.48.161.44:8080/myweb/HelloWorld");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    InputStream inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    result = builder.toString();
                    Message message = new Message();
                    message.what = SUCCESS_IN_GET;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = ERROR_IN_GET;
                    handler.sendMessage(message);
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
//                    Message message = new Message();
//                    message.what = ERROR_IN_GET;
//                    handler.sendMessage(message);
                }


            }
        }).start();
    }

    //字符串的拆分显示形式
    private void setHontTV() {
        String net = netText_et.getText().toString();
        SpannableString spannableString = new SpannableString("输入的网址：" + net);
        spannableString.setSpan(new ForegroundColorSpan(Color.GREEN), 0, 3,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.YELLOW), 5, 6,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 6, 6+net.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
                                    @Override
                                    public void onClick(View widget) {
                                        Toast.makeText(MainActivity.this,"网址",Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                },
                3, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        hont_tv.setText(spannableString);
        hont_tv.setMovementMethod(LinkMovementMethod.getInstance());
        hont_tv.setFocusable(true);
    }

    //网络连接
    private void Connection() {
        address = netText_et.getText().toString();
        progressDialog.setMessage("获取中...");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("提示");
        progressDialog.show();
        if (!address.startsWith("http://") && !address.startsWith("https://")) {
            address = "https://" + address;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    InputStream inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    result = builder.toString();
                    Message message = new Message();
                    message.what = SUCCESS_IN_GET;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = new Message();
                    message.what = ERROR_IN_GET;
                    handler.sendMessage(message);
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
//                    Message message = new Message();
//                    message.what = ERROR_IN_GET;
//                    handler.sendMessage(message);
                }


            }
        }).start();


    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS_IN_GET:
                    Spanned spanned = Html.fromHtml(result);
                    result_tv.setText(spanned);
                    result_tv.setMovementMethod(LinkMovementMethod.getInstance());
                    progressDialog.dismiss();
                    break;
                case ERROR_IN_GET:
                    Toast.makeText(MainActivity.this, "输入的网址不符合要求", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    break;
                default:
            }
        }
    };

    //隐藏输入键盘
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.image_:
                Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                startActivity(intent);
                break;
            case R.id.file_:
                Intent intent1 = new Intent(MainActivity.this, FileActivity.class);
                startActivity(intent1);
                break;
            case R.id.thread_load:
                Intent intent2 = new Intent(MainActivity.this,ThreadLoadActivity.class);
                startActivity(intent2);
                break;
            case R.id.plugin:
                Intent intent3 = new Intent(MainActivity.this,PluginActivity.class);
                startActivity(intent3);
                break;
        }
        return true;
    }
}
