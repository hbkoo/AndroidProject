package com.example.acer.qrcode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ChoiceVideoActivity extends AppCompatActivity {

    private String path1 = "http://cdn.xfj1.com:36150/html7/vod/vod1/";    // 主路径一
    private String path2 = "http://cdn.xfj1.com:36150/html7/vod/vod2/vip"; // 主路径二


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_video);
    }



}
