package com.example.acer.mynettest.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.acer.mynettest.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileActivity extends AppCompatActivity {

    static final int DOWNLOAD_SUCCESS = 0;
    static final int DOWNLOAD_FILE = 1;
    static final int UPLOAD_SUCCESS = 2;
    static final int UPLOAD_FILE = 3;

    EditText net_et,local_et;
    Button download_btn,upload_btn;
    ProgressDialog progressDialog;

    String net_address,local_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        net_et = (EditText) findViewById(R.id.net_et);
        local_et = (EditText) findViewById(R.id.local_et);
        download_btn = (Button) findViewById(R.id.download_btn);
        upload_btn = (Button) findViewById(R.id.upload_btn);
        download_btn.setOnClickListener(new mClick());
        upload_btn.setOnClickListener(new mClick());
        progressDialog = new ProgressDialog(this);
    }

    private class mClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.download_btn:
                    DownLoad();
                    break;
                case R.id.upload_btn:
                    UpLoad();
                    break;
            }
        }
    }

    private void DownLoad() {
        progressDialog.setTitle("提示");
        progressDialog.setMessage("正在下载...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        net_address = net_et.getText().toString();
        local_address = local_et.getText().toString() + "data.txt";

        if (net_address.equals("")) {
            Toast.makeText(FileActivity.this,"请输入下载网址",Toast.LENGTH_SHORT).show();
            return;
        }

        if (!net_address.startsWith("http://")) {
            net_address = "http://" + net_address;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                FileOutputStream outputStream = null;
                try {
                    outputStream = openFileOutput(local_address, Context.MODE_PRIVATE);
                    URL url = new URL(net_address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5 * 1000);
                    InputStream is = connection.getInputStream();
                    byte buffer [] = new byte[1024];
                    if ((is.read(buffer)) != -1) {
                        outputStream.write(buffer);
                    }
                    Message message = new Message();
                    message.what = DOWNLOAD_SUCCESS;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    progressDialog.setMessage("获取失败!");
                    Message message = new Message();
                    message.what = DOWNLOAD_FILE;
                    handler.sendMessage(message);
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                    }
                }
            }
        }).start();

    }

    private void UpLoad() {
        progressDialog.setTitle("提示");
        progressDialog.setMessage("正在上传...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        net_address = net_et.getText().toString();
        local_address = local_et.getText().toString();
        if (net_address.equals("")) {
            Toast.makeText(FileActivity.this,"请输入服务器网址",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!net_address.startsWith("http://")) {
            net_address = "http://" + net_address;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                FileOutputStream outputStream = null;
                try {
                    URL url = new URL(net_address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5 * 1000);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setRequestProperty("Connection","Keep-Alive");//维持长连接

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD_SUCCESS:
                    progressDialog.dismiss();
                    Toast.makeText(FileActivity.this,"下载成功！",Toast.LENGTH_SHORT).show();
                    break;
                case DOWNLOAD_FILE:
                    progressDialog.dismiss();
                    Toast.makeText(FileActivity.this,"下载失败！",Toast.LENGTH_SHORT).show();
                    break;
                case UPLOAD_SUCCESS:

                    break;
                case UPLOAD_FILE:

                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
