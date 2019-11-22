package com.example.acer.enterprise.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.enterprise.R;
import com.example.acer.enterprise.unit.DataProcessing;
import com.example.acer.enterprise.unit.InfAdapter;
import com.example.acer.enterprise.unit.Information;

import java.util.ArrayList;
import java.util.List;

/**
 * 企业管理员活动
 */
public class AdmActivity extends AppCompatActivity {

    private List<Information> informationList = new ArrayList<>();
    private SQLiteDatabase database = null;

    private LinearLayout empty_layout;
    private RecyclerView recyclerView;
    private InfAdapter adapter;

    private LinearLayout delete_layout;
    private CheckBox all_check_box;

    private boolean isDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_adm);
        setSupportActionBar(toolbar);

        empty_layout = (LinearLayout) findViewById(R.id.adm_empty_layout);
        recyclerView = (RecyclerView) findViewById(R.id.adm_recycle_view);
        ImageView delete_iv = (ImageView) findViewById(R.id.delete_iv);
        all_check_box = (CheckBox) findViewById(R.id.all_check_box);
        delete_layout = (LinearLayout) findViewById(R.id.delete_layout);

        delete_iv.setOnClickListener(new mClick());
        all_check_box.setOnClickListener(new mClick());

        LinearLayoutManager manager = new LinearLayoutManager(AdmActivity.this);
        recyclerView.setLayoutManager(manager);
        adapter = new InfAdapter(AdmActivity.this, informationList, true);
        recyclerView.setAdapter(adapter);
        adapter.setAllCheckBox(all_check_box);

        //加载数据
        LoadInformation();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.adm_menu, menu);
        return true;
    }

    /**
     * 菜单选项按钮
     *
     * @param menu 菜单
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isDelete) {
            isDelete = false;
            delete_layout.setVisibility(View.GONE);
            adapter.setCheckable(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.release:
                startActivityForResult(new Intent(AdmActivity.this, ReleaseActivity.class),
                        0);
                break;
            case R.id.delete:
                if (informationList.size() == 0) {
                    Toast.makeText(AdmActivity.this, "当前暂无记录信息！", Toast.LENGTH_SHORT).show();
                } else {
                    delete_layout.setVisibility(View.VISIBLE);
                    isDelete = true;
                    adapter.setCheckable(true);
                }
                break;
            case R.id.exit:
                startActivity(new Intent(AdmActivity.this, MainActivity.class));
                finish();
                break;
        }

        return true;
    }

    private class mClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.delete_iv:
                    List<Information> removelist = new ArrayList<>();
                    removelist.addAll(adapter.getRemoveList());
                    if (removelist.size() == 0) {
                        Toast.makeText(AdmActivity.this, "请选择要删除的记录！", Toast.LENGTH_SHORT).show();
                    } else {
                        IsDelete(removelist);
                    }
                    break;
                case R.id.all_check_box:
                    adapter.setAllChecked(all_check_box.isChecked());
                    break;
            }
        }
    }

    //弹出对话框，判断是否确认删除
    private void IsDelete(final List<Information> removelist) {

        View view = LayoutInflater.from(AdmActivity.this)
                .inflate(R.layout.judge_layout, null, false);
        TextView inform_tv = (TextView) view.findViewById(R.id.inform_content);
        TextView continue_tv = (TextView) view.findViewById(R.id.judge_continue_tv);
        TextView quit_tv = (TextView) view.findViewById(R.id.judge_quit_tv);

        inform_tv.setText("确认删除吗？");
        continue_tv.setText("取消");
        quit_tv.setText("删除");

        AlertDialog.Builder builder = new AlertDialog.Builder(AdmActivity.this);
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
                Delete(removelist);
            }
        });


    }

    //删除选中的记录信息
    private void Delete(List<Information> removelist) {

        if (database == null) {
            database = DataProcessing.getINFDatabase(AdmActivity.this, 1);
        }
        if (DataProcessing.DeleteInformation(database, removelist)) {
            Toast.makeText(AdmActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
            //把数据从程序的集合中删除
            informationList.removeAll(removelist);
            adapter.clearRemoveList();

            if (informationList.size() == 0) {
                empty_layout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }

        } else {
            Toast.makeText(AdmActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
        }

        isDelete = false;
        adapter.setCheckable(false);
        delete_layout.setVisibility(View.GONE);
    }

    //获取数据
    private void LoadInformation() {
        if (database == null) {
            database = DataProcessing.getINFDatabase(AdmActivity.this, 1);
        }

        informationList.clear();
        informationList.addAll(DataProcessing.LoadAllInformation(database));

        if (informationList.size() == 0) {
            empty_layout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            empty_layout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 活动回调
     *
     * @param requestCode 访问码
     * @param resultCode  回调结果码
     * @param data        回调信息
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            switch (resultCode) {
                case 1:
                    boolean isAdd = data.getBooleanExtra("isAdd", false);
                    if (isAdd) {
                        LoadInformation();
                    }
                    break;
                case 2:
                    boolean isChange = data.getBooleanExtra("isChange", false);
                    if (isChange) {
                        LoadInformation();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isDelete) {
            isDelete = false;
            adapter.setCheckable(false);
            delete_layout.setVisibility(View.GONE);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
