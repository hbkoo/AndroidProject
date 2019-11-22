package com.example.acer.enterprise.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.acer.enterprise.R;
import com.example.acer.enterprise.unit.DataProcessing;

public class RegistActivity extends AppCompatActivity {

    private RadioButton nor_rb,adm_rb;
    private Button reg_btn;
    private EditText username_et,password_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        ImageView back_iv = (ImageView) findViewById(R.id.back_reg_iv);
        reg_btn = (Button) findViewById(R.id.regist_btn);
        username_et = (EditText) findViewById(R.id.username_reg_et);
        password_et = (EditText) findViewById(R.id.password_reg_et);
        nor_rb = (RadioButton) findViewById(R.id.nor_rb);
        adm_rb = (RadioButton) findViewById(R.id.adm_rb);
        nor_rb.setOnCheckedChangeListener(new mCheck());
        adm_rb.setOnCheckedChangeListener(new mCheck());
        back_iv.setOnClickListener(new mClick());
        reg_btn.setOnClickListener(new mClick());
        nor_rb.setChecked(true);
    }

    private class mClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.regist_btn:
                    Register();
                    break;
                case R.id.back_reg_iv:
                    finish();
                    break;
            }
        }
    }

    //注册新用户
    private void Register() {
        String name = username_et.getText().toString();
        String psd = password_et.getText().toString();
        String role = "";
        if (nor_rb.isChecked()){
            role = "normal";
        } else if (adm_rb.isChecked()) {
            role = "administrator";
        }

        if ("".equals(name)){
            Toast.makeText(RegistActivity.this,"注册名不能为空！",Toast.LENGTH_SHORT).show();
        } else if("".equals(psd)) {
            Toast.makeText(RegistActivity.this,"密码不能为空！",Toast.LENGTH_SHORT).show();
        } else {
            if (DataProcessing.Register(MainActivity.liteDatabase,name,psd,role)){
                Toast.makeText(RegistActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RegistActivity.this,"该用户名已经存在！",Toast.LENGTH_SHORT).show();
            }
        }

    }

    private class mCheck implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                switch (buttonView.getId()){
                    case R.id.nor_rb:
                        nor_rb.setChecked(true);
                        adm_rb.setChecked(false);
                        break;
                    case R.id.adm_rb:
                        nor_rb.setChecked(false);
                        adm_rb.setChecked(true);
                        break;
                }
            }
        }
    }

}
