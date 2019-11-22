package com.example.acer.bus1.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.acer.bus1.R;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    private int tag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        permission();

        tag = getIntent().getIntExtra("tag",0);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    //访问权限询问
    public void permission() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(StartActivity.this, android.Manifest
                .permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(StartActivity.this, android.Manifest.permission
                .READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(StartActivity.this, android.Manifest.permission
                .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(StartActivity.this, permissions, 1);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(StartActivity.this,LoginActivity.class);

//                    if (tag == 0) {
//                        intent = new Intent(StartActivity.this,LoginActivity.class);
//                    } else {
//                        intent = new Intent(StartActivity.this,MainActivity.class);
//                    }
                    startActivity(intent);
                    finish();
                }
            } , 3000);
        }

    }

    //权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须统一所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(StartActivity.this,LoginActivity.class);
//                            if (tag == 0) {
//                                intent = new Intent(StartActivity.this,LoginActivity.class);
//                            } else {
//                                intent = new Intent(StartActivity.this,MainActivity.class);
//                            }
                            startActivity(intent);
                            finish();
                        }
                    } , 3000);

                } else {
                    Toast.makeText(this, "发生未知错误，请重新启动", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }


}
