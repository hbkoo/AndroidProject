package com.example.acer.mynettest.Activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.mynettest.Class.DownloadProgressListener;
import com.example.acer.mynettest.Class.FileDownLoader;
import com.example.acer.mynettest.R;

import java.io.File;

public class ThreadLoadActivity extends AppCompatActivity {

    private static final int PROCESSING = 1;//正在下载时数据传输Message标志
    private static final int FAILURE = -1;//下载失败是Message标志

    private EditText path_et;
    private TextView result_tv;//现在进度显示百分比文本框
    private Button download_btn, stop_btn;
    private ProgressBar progressBar;
    private String path = "http://192.168.1.100:8080/ServerForMultipleThreadDownloader/" +
            "CNNRecordingFromWangjialin.mp3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_load);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        path_et = (EditText) findViewById(R.id.path);
        result_tv = (TextView) findViewById(R.id.result_tv);
        download_btn = (Button) findViewById(R.id.start_btn);
        stop_btn = (Button) findViewById(R.id.stop_btn);
        progressBar = (ProgressBar) findViewById(R.id.progress_Bar);

        path_et.setText(path);

        download_btn.setOnClickListener(new mClick());
        stop_btn.setOnClickListener(new mClick());

    }

    private class mClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.start_btn:
                    //获取SDcard是否存在
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        //获取SDcard根目录文件
                        File saveDir = Environment.getExternalStorageDirectory();
                        File saveDirl = Environment.getExternalStoragePublicDirectory(Environment
                                .DIRECTORY_MOVIES);
                        File saveDirl1 = getApplication().getExternalFilesDir(Environment
                                .DIRECTORY_MOVIES);
                        download(path, saveDirl1);//下载文件
                    } else {
                        //当SDcard不存在时
                        Toast.makeText(ThreadLoadActivity.this, "SDCard不存在，请检查！",
                                Toast.LENGTH_SHORT).show();
                    }
                    download_btn.setEnabled(false);
                    stop_btn.setEnabled(true);
                    break;
                case R.id.stop_btn:
                    exit();
                    download_btn.setEnabled(true);
                    stop_btn.setEnabled(false);
                    break;
            }
        }
    }

    private DownloadTask task;

    //退出下载
    public void exit() {
        if (task != null) {
            task.exit();
        }
    }

    private void download(String path, File saveDirl1) {
        task = new DownloadTask(path, saveDirl1);
        new Thread(task).start();//开始下载
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROCESSING:
                    int size = msg.getData().getInt("size");
                    progressBar.setProgress(size);
                    float num = (float) progressBar.getProgress() / (float) progressBar.getMax();
                    int result = (int) (num * 100);
                    result_tv.setText(result + "%");
                    if (progressBar.getProgress() == progressBar.getMax()) {
                        Toast.makeText(getApplicationContext(),"下载完成！",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case FAILURE:
                    Toast.makeText(getApplicationContext(),"下载失败！",Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private class DownloadTask implements Runnable {
        private String path;
        private File saveDIr;   //下载到保存的文件
        private FileDownLoader loader;


        private DownloadTask(String path, File saveDIr) {
            this.path = path;
            this.saveDIr = saveDIr;
        }

        //退出下载
        public void exit() {
            if (loader != null) {
                loader.exit();
            }
        }

        DownloadProgressListener downloadProgressListener = new DownloadProgressListener() {
            @Override
            public void onDownloadSize(int size) {
                Message message = new Message();
                message.what = PROCESSING;
                message.getData().putInt("size", size);
                handler.sendMessage(message);
            }
        };

        @Override
        public void run() {
            try {
                loader = new FileDownLoader(ThreadLoadActivity.this, path, saveDIr, 3);
                progressBar.setMax(loader.getFileSize());
                loader.download(downloadProgressListener);
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendMessage(handler.obtainMessage(FAILURE));
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
