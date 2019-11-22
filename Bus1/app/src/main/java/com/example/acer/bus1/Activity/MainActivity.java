package com.example.acer.bus1.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.example.acer.bus1.Class.MyLocation;
import com.example.acer.bus1.Class.MySearchLine;
import com.example.acer.bus1.Class.SaveUserPicture;
import com.example.acer.bus1.Class.Site;
import com.example.acer.bus1.R;
import com.example.acer.bus1.Service.HttpUtil;
import com.example.acer.bus1.Service.TaskLine;
import com.example.acer.bus1.Service.UrlAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static String UserID;   //用户账号
    public static Context CONTEXT;

    private boolean isFirst = true;  //第一次进入界面的话就读取当前头像和用户名信息

    private AlertDialog Dialog;              //车次选择对话框
    private TextView select_train;           //选择的车次号
    private Spinner train_spinner;           //车次下拉框
    private List<String> number_list = new ArrayList<>();//车次号

    private LinearLayout reform_linear;     //提示信息
    private View toolbar_shadow;

    private DrawerLayout drawerLayout;      //导航抽屉菜单
    private TextView nav_ID;          //导航抽屉账号
    private TextView nav_name;        //导航抽屉用户名字
    private CircleImageView nav_image;//导航抽屉头像
    private NavigationView navigationView;  //抽屉界面
    private RelativeLayout nav_header_layout;//抽屉头布局
    private FloatingActionButton location_fab;//返回当前位置的悬浮按钮

    private MyLocation myLocation;            //创建定位对象
    private MapView mapView;               //显示地图界面
    private BaiduMap baiduMap;             //百度地图界面


    //创建通知对象
    private NotificationManager notificationManager;
    private Notification notification;

    public static boolean isTask = false;                //是否有路线任务
    private ArrayList<String> stationNames = new ArrayList<>();//路线各站点的名字
    private List<Site> stationSites = new ArrayList<>();//路线的各站点信息
    private List<LatLng> stationLatlngs = new ArrayList<>();//路线的站点坐标
    private TaskLine taskLine;   //路线任务
    private MySearchLine mySearchLine;

    private TaskLineReceiver receiver;  //路线任务广播接收器

    //private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());    //地图显示前的初始化
        setContentView(R.layout.activity_main);

        //显示toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,
        //        R.string.openDrawerContent, R.string.closeDrawerContent);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);//显示导航按钮
            actionBar.setHomeAsUpIndicator(R.drawable.menu_image);
        }
        //actionBarDrawerToggle.syncState();

        UserID = "20170521";

        //初始化界面中的控件
        InitControls();
        //抽屉菜单按钮事件监听
        navigationView.setNavigationItemSelectedListener(new mNavigation_Select());
        drawerLayout.setDrawerListener(new mDrawer());
        //drawerLayout.addDrawerListener(actionBarDrawerToggle);
        nav_header_layout.setOnClickListener(new mClick());
        reform_linear.setOnClickListener(new mClick());
        location_fab.setOnClickListener(new mClick());

        //初始化车次号
        InitNumber();
    }

    //初始化界面中的控件
    private void InitControls() {
        isFirst = true;
        CONTEXT = this;
        select_train = (TextView) findViewById(R.id.select_train);
        reform_linear = (LinearLayout) findViewById(R.id.reform_linea);
        toolbar_shadow = findViewById(R.id.toolbar_shadow);

        location_fab = (FloatingActionButton) findViewById(R.id.location_fab);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        nav_ID = (TextView) navigationView.getHeaderView(0)
                .findViewById(R.id.nav_header_id);
        nav_name = (TextView) navigationView.getHeaderView(0)
                .findViewById(R.id.nav_header_Name);
        nav_image = (CircleImageView) navigationView.getHeaderView(0)
                .findViewById(R.id.nav_header_image);
        nav_header_layout = (RelativeLayout) navigationView.getHeaderView(0)
                .findViewById(R.id.nav_header_layout);

        mapView = (MapView) findViewById(R.id.mapView);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true); //设置我可以显示在地图上
        //将百度地图logo除去
        View child = mapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new NotificationCompat.Builder(this).build();
        myLocation = new MyLocation(MainActivity.this, baiduMap, location_fab);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.acer.bus1.Activity.TaskLine");
        receiver = new TaskLineReceiver();
        registerReceiver(receiver, intentFilter);
        InitNavHeader();
        isFirst = false;
        myLocation.startLocation();
    }

    /*初始化车次号码*/
    public void InitNumber() {
        LinearLayout linearLayout = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.train_number, null);
        train_spinner = (Spinner) linearLayout.findViewById(R.id.train_spinner);
        TextView ok_tv = (TextView) linearLayout.findViewById(R.id.OK_tv);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setTitle("车次选择");
        builder.setView(linearLayout);
        Dialog = builder.show();

        number_list.add("请选择车次号");
        number_list.add("211");
        number_list.add("985");
        number_list.add("811");
        number_list.add("540");
        ArrayAdapter<String> number_adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_dropdown_item, number_list);
        train_spinner.setFocusable(false);
        train_spinner.setAdapter(number_adapter);

        ok_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (train_spinner.getSelectedItem().toString().equals("请选择车次号")) {
                    Toast.makeText(MainActivity.this, "请选择一辆车次号！",
                            Toast.LENGTH_SHORT).show();
                } else {
                    //TODO 联网发送车次号
                    RequestBody body = new FormBody.Builder()
                            .add("train_name", train_spinner.getSelectedItem().toString())
                            .build();
                    HttpUtil.postOKHttpRequest(UrlAPI.SendTrainNumUrl, body, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            //不能进行Toast显示
//                            Toast.makeText(MainActivity.this, "请检查网络连接",
//                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Message message = new Message();
                            message.what = 0;
                            handler.sendMessage(message);
                        }
                    });
                    select_train.setText(train_spinner.getSelectedItem().toString());
                    Dialog.dismiss();
                }
            }
        });

    }

    //TODO
    //获取路线信息
    public void update() {

        stationNames.clear();
        //station_ll.clear();

        stationNames.add("友谊大道华城广场");
        stationNames.add("友谊大道才茂街");
        stationNames.add("友谊大道才华街");
        stationNames.add("徐东大街徐东一路");
   //     stationNames.add("友谊大道才华街");
        //stationNames.add("徐东大街红盛路");
   //     stationNames.add("和平大道四美塘");
        //stationNames.add("和平大道杨园");
   //     stationNames.add("和平大道余家头");
        //stationNames.add("和平大道杨家路");
   //     stationNames.add("和平大道奥山世纪城");
        //stationNames.add("和平大道建设一路");
        stationNames.add("团结大道沙湖边");
//        stationNames.add("建设一路121街坊");
//        //stationNames.add("友谊大道钢花新村");
//        stationNames.add("友谊大道园林路");

        stationNames.add("秦园东路沙湖湾");
//        stationNames.add("罗家港路井家墩");


        isTask = true;
        reform_linear.setVisibility(View.VISIBLE);
        toolbar_shadow.setVisibility(View.GONE);
        Intent intent = new Intent(MainActivity.this, TrainInformationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("train", select_train.getText().toString());
        bundle.putStringArrayList("stations", stationNames);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
        notification = new NotificationCompat.Builder(MainActivity.this)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle("新路线")
                .setContentText(stationNames.get(0) + "---" +
                        stationNames.get(stationNames.size() - 1))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.location_image)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.location_image))
                .setVibrate(new long[]{0, 1000, 1000, 1000})
                .setLights(Color.GREEN, 1000, 1000)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(1, notification);

        //SaveStationLine();//保存路线

        //设置路线站点名字从而得到站点对应的地理坐标位置以便之后的定位
        myLocation.setStationLL(stationNames);

    }

    //导航抽屉菜单按钮点击事件监听
    private class mNavigation_Select implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.nav_history:
                    drawerLayout.closeDrawer(GravityCompat.START);



                    Intent intent = new Intent(MainActivity.this, HistoryLineActivity.class);
                    startActivity(intent);
                    break;
                case R.id.nav_about:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Intent intent_about = new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(intent_about);
                    //update();
                    break;
                case R.id.nav_return:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Intent intent1 = new Intent(MainActivity.this, ReturnActivity.class);
                    startActivity(intent1);
                    break;
                default:
            }
            return true;
        }
    }

    //抽屉事件监听
    private class mDrawer implements DrawerLayout.DrawerListener {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            InitNavHeader();
        }

        @Override
        public void onDrawerClosed(View drawerView) {
        }

        @Override
        public void onDrawerStateChanged(int newState) {
        }
    }

    //按钮事件监听
    private class mClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.reform_linea://查看详细路线
                    Intent intent = new Intent(MainActivity.this, TrainInformationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("tag", 0);
                    bundle.putString("train", select_train.getText().toString());
                    bundle.putStringArrayList("stations", stationNames);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left, R.anim.push_right);
                    break;
                case R.id.location_fab://定位到当前位置
                    myLocation.returnCurrentLocation();
                    location_fab.setImageResource(R.drawable.location_fab_locked);
                    break;
                case R.id.nav_header_layout://抽屉中头像个人信息
                    Intent intent1 = new Intent(MainActivity.this, SelfInformationActivity.class);
                    startActivity(intent1);
                    break;
            }
        }
    }

    //加载toolbar里的menu菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    //对toolbar里的menu菜单按钮点击操作
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.draw_line:
                DrawLineInMap();
                break;
            default:
        }
        return true;
    }

    //在地图上画路线
    private void DrawLineInMap() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable() || !info.isConnected()) {
            Toast.makeText(MainActivity.this, "当前网络不可用\n请检查网络连接后重新绘制路线",
                    Toast.LENGTH_SHORT).show();
            return;
        }
 //       List<LatLng> stationsLL = getStationNames();
//        mySearchLine = new MySearchLine(MainActivity.this,baiduMap,
//                stationsLL,stationNames);
        //addLuoyangSites();
        addWuhanSites();
        mySearchLine = new MySearchLine(MainActivity.this,baiduMap,stationSites);
        mySearchLine.drive();
        myLocation.setStationLatlngs(stationLatlngs);
        isTask = true;
        reform_linear.setVisibility(View.VISIBLE);
        toolbar_shadow.setVisibility(View.GONE);

        taskLine = new TaskLine(select_train.getText().toString(),MainActivity.this);
        taskLine.SaveStationLine(stationNames);

//        if (isTask && stationNames.size() != 0) {
//            baiduMap.clear();
//            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
//            progressDialog.setMessage("正在努力规划路线...");
//            progressDialog.setCancelable(true);
//            progressDialog.show();
////            mySearchLine = new MySearchLine(MainActivity.this, baiduMap, stationNames);
////            mySearchLine.PlanBusLine(progressDialog);
//
//        } else {
//            //Toast.makeText(MainActivity.this, "目前暂无路线任务", Toast.LENGTH_SHORT).show();
//        }

    }

    private List<LatLng> getStationNames() {
        stationNames = new ArrayList<>();
        stationNames.clear();
        List<LatLng> stationLL = new ArrayList<>();
        LatLng ll = new LatLng(30.611887,114.366399);//30.612447,114.367369
        stationLL.add(ll);
        ll = new LatLng(30.603759,114.356603);
        stationLL.add(ll);
        ll = new LatLng(30.598568,114.35195);
        stationLL.add(ll);
        ll = new LatLng(30.594232,114.352349);
        stationLL.add(ll);
        ll = new LatLng(30.588924,114.354945);
        stationLL.add(ll);
        ll = new LatLng(30.584882,114.351765);
        stationLL.add(ll);
        stationNames.add("友谊大道华城广场");
        stationNames.add("友谊大道才茂街");
        stationNames.add("友谊大道才华街");
        stationNames.add("徐东大街徐东一路");
        stationNames.add("团结大道沙湖边");
        stationNames.add("秦园东路沙湖湾");
        return stationLL;
    }

    //获取站点信息
    private void addWuhanSites() {
        stationSites.clear();
        stationNames.clear();
        stationLatlngs.clear();
        Site site;
        site = new Site();
        site.setName("友谊大道华城广场");
        site.setIntroduce("友谊大道华城广场公交站点");
        site.setPosition_y(30.611887);
        site.setPosition_x(114.366399);
        site.setLatLng();
        stationSites.add(site);
        site = new Site();
        site.setName("友谊大道才茂街");
        site.setIntroduce("友谊大道才茂街公交站点");
        site.setPosition_y(30.603759);
        site.setPosition_x(114.356603);
        site.setLatLng();
        stationSites.add(site);
        site = new Site();
        site.setName("友谊大道才华街");
        site.setIntroduce("友谊大道才华街公交站点");
        site.setPosition_y(30.598568);
        site.setPosition_x(114.35195);
        site.setLatLng();
        stationSites.add(site);
        site = new Site();
        site.setName("徐东大街徐东一路");
        site.setIntroduce("徐东大街徐东一路公交站点");
        site.setPosition_y(30.594232);
        site.setPosition_x(114.352349);
        site.setLatLng();
        stationSites.add(site);
        site = new Site();
        site.setName("团结大道沙湖边");
        site.setIntroduce("团结大道沙湖边公交站点");
        site.setPosition_y(30.588924);
        site.setPosition_x(114.354945);
        site.setLatLng();
        stationSites.add(site);
        site = new Site();
        site.setName("秦园东路沙湖湾");
        site.setIntroduce("秦园东路沙湖湾公交站点");
        site.setPosition_y(30.584882);
        site.setPosition_x(114.351765);
        site.setLatLng();
        stationSites.add(site);
        stationNames.add("友谊大道华城广场");
        stationNames.add("友谊大道才茂街");
        stationNames.add("友谊大道才华街");
        stationNames.add("徐东大街徐东一路");
        stationNames.add("团结大道沙湖边");
        stationNames.add("秦园东路沙湖湾");
    }

    //获取站点信息
    private void addLuoyangSites() {
        stationSites.clear();
        stationNames.clear();
        stationLatlngs.clear();
        Site site;
        site = new Site();
        stationNames.add("启明西路夹马营路口");
        site.setName("启明西路夹马营路口");
        site.setIntroduce("启明西路夹马营路口公交站点");
        site.setPosition_y(34.701391);
        site.setPosition_x(112.502828);
        site.setLatLng();
        stationSites.add(site);
        stationLatlngs.add(site.getLatLng());
        site = new Site();
        stationNames.add("北窑");
        site.setName("北窑");
        site.setIntroduce("北窑公交站点");
        site.setPosition_y(34.701246);
        site.setPosition_x(112.499181);
        site.setLatLng();
        stationSites.add(site);
        stationLatlngs.add(site.getLatLng());
        site = new Site();
        stationNames.add("三井洞桥西");
        site.setName("三井洞桥西");
        site.setIntroduce("三井洞桥西公交站点");
        site.setPosition_y(34.702226);
        site.setPosition_x(112.496131);
        site.setLatLng();
        stationSites.add(site);
        stationLatlngs.add(site.getLatLng());
        site = new Site();
        stationNames.add("东车站");
        site.setName("东车站");
        site.setIntroduce("东车站公交站点");
        site.setPosition_y(34.700946);
        site.setPosition_x(112.491729);
        site.setLatLng();
        stationSites.add(site);
        stationLatlngs.add(site.getLatLng());
        site = new Site();
        stationNames.add("新街环城北路口");
        site.setName("新街环城北路口");
        site.setIntroduce("新街环城北路口公交站点");
        site.setPosition_y(34.697258);
        site.setPosition_x(112.493037);
        site.setLatLng();
        stationSites.add(site);
        stationLatlngs.add(site.getLatLng());
        site = new Site();
        stationNames.add("中州东路民主街口");
        site.setName("中州东路民主街口");
        site.setIntroduce("中州东路民主街口公交站点");
        site.setPosition_y(34.690495);
        site.setPosition_x(112.492632);
        site.setLatLng();
        stationSites.add(site);
        stationLatlngs.add(site.getLatLng());
        site = new Site();
        stationNames.add("青年宫");
        site.setName("青年宫");
        site.setIntroduce("青年宫公交站点");
        site.setPosition_y(34.689875);
        site.setPosition_x(112.487045);
        site.setLatLng();
        stationSites.add(site);
        stationLatlngs.add(site.getLatLng());
        site = new Site();
        stationNames.add("老集");
        site.setName("老集");
        site.setIntroduce("老集公交站点");
        site.setPosition_y(34.689375);
        site.setPosition_x(112.482886);
        site.setLatLng();
        stationSites.add(site);
        stationLatlngs.add(site.getLatLng());
        site = new Site();
        stationNames.add("西关");
        site.setName("西关");
        site.setIntroduce("西关公交站点");
        site.setPosition_y(34.688345);
        site.setPosition_x(112.475606);
        site.setLatLng();
        stationSites.add(site);
        stationLatlngs.add(site.getLatLng());
        site = new Site();
        stationNames.add("中州中路定鼎路口");
        site.setName("中州中路定鼎路口");
        site.setIntroduce("中州中路定鼎路口公交站点");
        site.setPosition_y(34.685247);
        site.setPosition_x(112.466958);
        site.setLatLng();
        stationSites.add(site);
        stationLatlngs.add(site.getLatLng());
    }

    //路线任务广播接收器
    private class TaskLineReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isFinish = intent.getBooleanExtra("isFinish",false);
            if (isFinish) {
                isTask = false;
                reform_linear.setVisibility(View.INVISIBLE);
                toolbar_shadow.setVisibility(View.GONE);
                stationNames.clear();
                taskLine.StartSearch();
                taskLine.UpdateHistory();
                return;
            }
            stationNames.clear();
            stationNames = intent.getStringArrayListExtra("stations");
            reform_linear.setVisibility(View.VISIBLE);
            toolbar_shadow.setVisibility(View.GONE);
            isTask = true;
            showNotify();
            //TODO 当有任务发送时或者在历史路线点击了发车按钮时
            //设置路线站点名字从而得到站点对应的地理坐标位置以便之后的定位
            List<LatLng> stationsLL = getStationNames();
           // mySearchLine = new MySearchLine(MainActivity.this,baiduMap, stationsLL,stationNames);

            myLocation.setStationLL(stationNames);
        }
    }

    //弹出一条通知
    private void showNotify() {
        Intent intent = new Intent(MainActivity.this, TrainInformationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("train", select_train.getText().toString());
        bundle.putStringArrayList("stations", stationNames);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
        notification = new NotificationCompat.Builder(MainActivity.this)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle("新路线")
                .setContentText(stationNames.get(0) + "---" +
                        stationNames.get(stationNames.size() - 1))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.location_image)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.location_image))
                .setVibrate(new long[]{0, 1000, 1000, 1000})
                .setLights(Color.GREEN, 1000, 1000)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(1, notification);
    }

    //访问权限询问
    public void permission() {
        List<String> permissionList = new ArrayList<>();
        //获取位置权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest
                .permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission
                .READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        //访问内存权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission
                .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
//            Intent intent = new Intent(MainActivity.this,StartActivity.class);
//            intent.putExtra("tag",1);
//            startActivity(intent);
//            finish();
//            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
//            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }
//        else {
//            myLocation.startLocation();
//        }

    }

//    //权限的回调
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        switch (requestCode) {
////            case 0:
////                if (grantResults.length > 0 && grantResults[0] == PackageManager
////                        .PERMISSION_GRANTED) {
////                    myLocation.startLocation();
////                } else {
////                    finish();
////                }
////                break;
////            case 1:
////                if (grantResults.length > 0 && grantResults[0] == PackageManager
////                        .PERMISSION_GRANTED) {
////
////                } else {
////                    Toast.makeText(MainActivity.this,"您拒绝了该权限可能导致程序之后无法正常运行"
////                          ,Toast.LENGTH_SHORT).show();
////                }
////                break;
//            case 1:
//                if (grantResults.length > 0) {
//                    for (int result : grantResults) {
//                        if (result != PackageManager.PERMISSION_GRANTED) {
//                            Toast.makeText(this, "必须统一所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
//                            finish();
//                            return;
//                        }//leaked
//                    }
//                    if (myLocation == null) {
//                        myLocation = new MyLocation(MainActivity.this,baiduMap);
//                    }
//                    myLocation.startLocation();
//
//                } else {
//                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//                break;
//            default:
//        }
//    }

    //初始化抽屉里的头数据
    private void InitNavHeader() {
        nav_ID.setText(UserID);
        if (isFirst || SelfInformationActivity.isNameChange) {
            SelfInformationActivity.isNameChange = false;
            SharedPreferences preferences = getSharedPreferences("information", MODE_PRIVATE);
            String name = preferences.getString("name", "司机  新用户");
            if (name.equals("未设置")) {
                name = "司机  新用户";
            }
            nav_name.setText(name);
        }
        if (isFirst || SelfInformationActivity.isImageChange) {
            SelfInformationActivity.isImageChange = false;
            Bitmap bitmap;
            SharedPreferences preferences = getSharedPreferences("information", MODE_PRIVATE);
            String ImageUri = preferences.getString("imageUri", "");
            if (ImageUri.equals("")) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_image);
            } else {
                bitmap = SaveUserPicture.getBitmap(ImageUri);
            }
            nav_image.setImageBitmap(bitmap);
        }
    }

    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    select_train.setText(train_spinner.getSelectedItem().toString());
                    Dialog.dismiss();
                    taskLine = new TaskLine(select_train.getText().toString(),MainActivity.this);
                    taskLine.StartSearch();//开始监听是否有新路线生成
                    break;
            }
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            moveTaskToBack(false);
//            LinearLayout layout = (LinearLayout) getLayoutInflater()
//                    .inflate(R.layout.user_exit, null);
//            TextView cancel_tv = (TextView) layout.findViewById(R.id.cancel_tv);
//            TextView exit_tv = (TextView) layout.findViewById(R.id.exit_tv);
//            TextView exit_information = (TextView) layout.findViewById(R.id.exit_information);
//            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//            dialog.setView(layout);
//            dialog.setCancelable(false);
//            final AlertDialog alertDialog = dialog.show();
//            if (!isTask) {
//                exit_information.setText("确认退出程序吗？");
//            }
//            cancel_tv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    alertDialog.dismiss();
//                }
//            });
//            exit_tv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    alertDialog.dismiss();
//                    finish();
//                }
//            });

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        myLocation.getmLocationClient().stop();
        if (myLocation.myOrientationListener != null) {
            myLocation.myOrientationListener.stop();
        }
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //permission();
        mapView.onResume();
        myLocation.getmLocationClient().start();
        MyLocation.LocationTime = 0;
        Log.i("开始定位:","LocationTime:0");
        Log.i("定位聚焦:","true");
        location_fab.setImageResource(R.drawable.location_fab_locked);
        if (myLocation.myOrientationListener != null) {
            myLocation.myOrientationListener.start();
        }
        //myLocation.returnCurrentLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        myLocation.getmLocationClient().stop();
        if (myLocation.myOrientationListener != null) {
            myLocation.myOrientationListener.stop();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        myLocation.getmLocationClient().stop();
        if (myLocation.myOrientationListener != null) {
            myLocation.myOrientationListener.stop();
        }
    }

    @Override
    protected void onRestart() {
        permission();
        super.onRestart();
    }
}
