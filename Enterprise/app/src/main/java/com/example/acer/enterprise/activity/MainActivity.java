package com.example.acer.enterprise.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.enterprise.R;
import com.example.acer.enterprise.unit.DataProcessing;
import com.example.acer.enterprise.unit.SQLUser;
import com.mob.MobSDK;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

public class MainActivity extends AppCompatActivity {

    //存储登录信息文件名
    private String LOGIN_DATA = "login_data";

    private EditText username_et, password_et;
    private CheckBox remember_cb;
    public static SQLiteDatabase liteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobSDK.init(this);


        Init();

    }

    //初始化控件
    private void Init() {
        Button login_btn = (Button) findViewById(R.id.login_btn);
        username_et = (EditText) findViewById(R.id.username_et);
        password_et = (EditText) findViewById(R.id.password_et);
        TextView register_tv = (TextView) findViewById(R.id.register);
        remember_cb = (CheckBox) findViewById(R.id.rememberCheckBox);
        login_btn.setOnClickListener(new mClick());
        register_tv.setOnClickListener(new mClick());
        remember_cb.setChecked(false);

        SharedPreferences preferences = getSharedPreferences(LOGIN_DATA, MODE_PRIVATE);
        Boolean isRemember = preferences.getBoolean("isRemember", false);
        if (isRemember) {
            username_et.setText(preferences.getString("username", ""));
            password_et.setText(preferences.getString("password", ""));
            String ROLE = preferences.getString("role", "");
            remember_cb.setChecked(true);
        }
        liteDatabase = new SQLUser(MainActivity.this, "User", null, 1)
                .getWritableDatabase();
    }

    private class mClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_btn:
                    //Login();
                    InitMob();
                    break;
                case R.id.register:
                    startActivity(new Intent(MainActivity.this,RegistActivity.class));
                    break;
            }
        }
    }

    //登录操作
    private void Login() {

        String username = username_et.getText().toString();
        String password = password_et.getText().toString();

        String role = DataProcessing.Login(liteDatabase, username, password);
        if (role != null) {
            StartActivity(role);
        } else {
            Toast.makeText(MainActivity.this, "用户名或密码错误！",
                    Toast.LENGTH_LONG).show();
        }

    }



    private void InitMob() {


        RegisterPage page = new RegisterPage();
        //如果使用我们的ui，没有申请模板编号的情况下需传null
        page.setTempCode(null);
        page.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 处理成功的结果
                    HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
                    String country = (String) phoneMap.get("country"); // 国家代码，如“86”
                    String phone = (String) phoneMap.get("phone"); // 手机号码，如“13800138000”
                    // TODO 利用国家代码和手机号码进行后续的操作
                } else{
                    // TODO 处理错误的结果
                }
            }
        });
        page.show(this);
    }

    //根据不同的角色启动不同的活动
    private void StartActivity(String role) {

        SharedPreferences.Editor editor = getSharedPreferences(LOGIN_DATA, MODE_PRIVATE).edit();

        if (remember_cb.isChecked()) {
            editor.putBoolean("isRemember", true);
            editor.putString("username", username_et.getText().toString());
            editor.putString("password", password_et.getText().toString());
            editor.putString("role", role);
            editor.apply();
        } else {
            editor.clear();
            editor.apply();
        }

        if ("administrator".equals(role)) {
            startActivity(new Intent(MainActivity.this, AdmActivity.class));
            this.finish();
        } else if ("normal".equals(role)) {
            startActivity(new Intent(MainActivity.this, NorActivity.class));
            this.finish();
        }

    }

}
