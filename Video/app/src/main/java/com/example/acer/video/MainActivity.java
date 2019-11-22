package com.example.acer.video;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private VideoView videoView;
    private EditText et_uri;

    private Button btn_last, btn_next;

    private TextView tv_current; // 显示当前播放的视频序号
    private RadioButton radioButton1, radioButton2, radioButton3;

    private String path1 = "http://cdn.xfj1.com:36150/html7/vod/vod1/";    // 主路径一
    private String path2 = "http://cdn.xfj1.com:36150/html7/vod/vod2/vip"; // 主路径二
    private String path3 = ""; //主路径三


    private String welcome_path1 = "https://vd.yinyuetai.com/he.yinyuetai." +
            "com/uploads/videos/common/D7ED015D121E12E2C78E01DB9017A15F.mp4"; // 欢迎视频一
    private String welcome_path2 = "https://vd.yinyuetai.com/he.yinyuetai." +
            "com/uploads/videos/common/9171015B8AB12B496684FE0F087EF65C.mp4"; // 欢迎视频二

    private int num1 = 0;
    private int num2 = 0;
    private int num3 = 0;

    private String current_path;
    private int current_num = 0;

    private String end = "/index.m3u8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        path1 = "http://jpsp1e1.moegay.club/rihan/QWoXdKJ1ZeV/index.m3u8";
        path2 = "http://jpsp1e1.moegay.club/fl/xiaoniaojiang/s9/index.m3u8";
        path3 = "http://jpsp1e1.moegay.club/fl/dikanong/03/index.m3u8";

        path1 = "http://jpsp1e1.moegay.club/fl/mingxing/";
        path2 = "http://jpsp1e1.moegay.club/fl/xiaoniaojiang/s";
        path3 = "http://jpsp1e1.moegay.club/fl/dikanong/";

        et_uri = findViewById(R.id.et_uri);  // 路径输入框
        findViewById(R.id.btn_start).setOnClickListener(this); // 播放按钮
        et_uri.setText("http://cdn.xfj1.com:36150/html7/vod/vod2/vip13.mp4");

        videoView = findViewById(R.id.video_view);
        MediaController controller = new MediaController(this);

        videoView.setMediaController(controller);

        tv_current = findViewById(R.id.tv_current);
        radioButton1 = findViewById(R.id.rb1);
        radioButton2 = findViewById(R.id.rb2);
        radioButton3 = findViewById(R.id.rb3);
        btn_last = findViewById(R.id.btn_last);
        btn_next = findViewById(R.id.btn_next);

        RadioGroup rg = findViewById(R.id.rg_rbs);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                btn_last.setEnabled(false);  // 第一个视频，所以上一个视频按钮不能点击
                switch (i) {
                    case R.id.rb1:
                        // 设置播放路径为主路径一
                        current_path = path1;
                        current_num = num1;
                        tv_current.setText("0");
                        videoView.setVideoURI(Uri.parse(welcome_path1));
                        break;
                    case R.id.rb2:
                        // 设置播放路径为主路径二
                        current_path = path2;
                        current_num = num2;
                        tv_current.setText("0");
                        videoView.setVideoURI(Uri.parse(welcome_path2));
                        break;
                    case R.id.rb3:
                        current_path = path3;
                        current_num = num3;
                        tv_current.setText("0");
                        videoView.setVideoURI(Uri.parse(welcome_path2));
                        break;

                }
                videoView.start();
            }
        });
        btn_last.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        tv_current.setOnClickListener(this);

        radioButton1.setChecked(true);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                videoView.setVideoURI(Uri.parse(et_uri.getText().toString().trim()));
                videoView.start();
                break;
            case R.id.btn_last:
                startLastVideo();
                break;
            case R.id.btn_next:
                startNextVideo();
                break;
            case R.id.tv_current:
//                Intent intent = new Intent(MainActivity.this,ChoiceVideoActivity.class);
//                startActivityForResult(intent,1);
                break;
        }
    }

    // 播放下一个视频
    private void startNextVideo() {

        //String uri = current_path + (++current_num) + ".mp4";

        current_num++;
        final String uri;
        if (radioButton2.isChecked()) {
            uri = current_path + (current_num) + end;
        } else {
            if (current_num < 10) {
                uri = current_path + "0" + current_num + end;
            } else {
                uri = current_path + current_num + end;
            }
        }

        videoView.setVideoPath(uri);
        videoView.requestFocus();
        videoView.start();

//        videoView.setVideoURI(Uri.parse(uri));
//        videoView.start();
        Toast.makeText(this,uri,Toast.LENGTH_SHORT).show();
        Init();

    }

    // 播放上一个视频
    private void startLastVideo() {

        //String uri = current_path + (--current_num) + ".mp4";

        current_num--;
        final String uri;
        if (radioButton2.isChecked()) {
            uri = current_path + (current_num) + end;
        } else {
            if (current_num < 10) {
                uri = current_path + "0" + current_num + end;
            } else {
                uri = current_path + current_num + end;
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                videoView.setVideoPath(uri);
                videoView.requestFocus();
                videoView.start();
            }
        }).start();

        videoView.setVideoURI(Uri.parse(uri));
        videoView.start();
        Toast.makeText(this,uri,Toast.LENGTH_SHORT).show();
        Init();

    }

    // 修改按钮状态
    private void Init() {
        if (current_num <= 1) {
            btn_last.setEnabled(false);
        } else {
            btn_last.setEnabled(true);
        }
        if (radioButton1.isChecked()) {
            tv_current.setText("" + current_num);
        } else {
            tv_current.setText("vip" + current_num);
        }
    }

    // 更新点击后的路径
    private void updateNum() {
        if (radioButton1.isChecked()) {
            num1 = current_num;
        } else {
            num2 = current_num;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 0) {

        }
    }

//    private void startVideo(final String uri) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                videoView.setVideoPath(uri);
//                videoView.requestFocus();
//                videoView.start();
//            }
//        }).start();
//    }

}
