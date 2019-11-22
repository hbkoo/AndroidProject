package com.example.acer.bus1.Class;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.PlanNode;
import com.example.acer.bus1.Activity.MainActivity;
import com.example.acer.bus1.R;
import com.example.acer.bus1.Service.HttpUtil;
import com.example.acer.bus1.Service.UrlAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 实现当前位置定位操作
 */

public class MyLocation {

    private static String CITY = "武汉";

    private Context context;
    private BaiduMap baiduMap;
    private LocationClient mLocationClient = null; //定位客户端
    private PoiSearch poiSearch = null;          //创建一个POI搜索对象
    private PoiCitySearchOption poiCitySearchOptions = null;

    //方向传感器监听
    public MyOrientationListener myOrientationListener = null;
    //创建自己的箭头定位
    private BitmapDescriptor bitmapDescriptor;

    private boolean isFocus = true;//是否聚焦
    public static int LocationTime;
    private int tag;

    private GeoCoder geoCoder;             //地理编码查询对象
    private GeoCodeOption geoCodeOption;      //查询对象要查询的条件

    private List<LatLng> stationlls = new ArrayList<>();
    private ArrayList<String> stationList;//站点名字
    private List<LatLng> stationLatlngs = null;
    private IntentFilter intentFilter;
    private SearchReceiver receiver;

    private float mCurrentX;  //方向值
    private BDLocation currentLocation = null;//当前位置
    private FloatingActionButton location_fab;//定位到当前位置按钮

    public MyLocation(Context context, BaiduMap baiduMap, FloatingActionButton location_fab) {
        //使用Application的上下文,Application的生命周期长，进程退出的时候才会销毁，
        // 和static的生命周期一样
        this.context = context.getApplicationContext();
        //this.context = context;//使用Activity的上下文
        this.baiduMap = baiduMap;
        this.location_fab = location_fab;
        mLocationClient = new LocationClient(context);
        mLocationClient.registerLocationListener(new MyLocationListener());
        InitPoiListener();//初始化poi搜索监听者和各种对象的实例化
    }

    public LocationClient getmLocationClient() {
        return mLocationClient;
    }

    //定位运行
    public void startLocation(){
        LocationTime = 0;
        initLocation();
        initMyLoc();//创建自己的定位图标，结合方向传感器，定位的时候显示自己的方向
        initBaiduMapListener();
        mLocationClient.start();
        myOrientationListener.start();
    }

    //初始化定位方式
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        option.setCoorType("bd09ll");
        mLocationClient.setLocOption(option);
    }

    //创建自己的定位图标，结合方向传感器，定位的时候显示自己的方向
    private void initMyLoc() {
        //初始化图标
        bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_locked);
        //方向传感器监听
        myOrientationListener = new MyOrientationListener(context);
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                if (currentLocation != null) {
                    mCurrentX = x;
                    // 构造定位数据
                    MyLocationData locData = new MyLocationData.Builder()
                            .accuracy(currentLocation.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                            .direction(x)
                            .latitude(currentLocation.getLatitude())
                            .longitude(currentLocation.getLongitude()).build();
                    // 设置定位数据
                    baiduMap.setMyLocationData(locData);
                    // 设置自定义图标
                    MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration
                            .LocationMode.NORMAL, true, bitmapDescriptor);
                    baiduMap.setMyLocationConfigeration(config);
                }

            }
        });
    }

    //地图触摸事件
    private void initBaiduMapListener() {

        baiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                location_fab.setImageResource(R.drawable.location_fab_off);
                isFocus = false;
            }
        });
    }

    //导航
    private void navigateTo(BDLocation location){
        currentLocation = location;
        LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
        if (LocationTime < 2) {
            isFocus = true;
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(ll,18f);
            baiduMap.animateMapStatus(update);
            LocationTime++;
        }
        if (isFocus) {
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            Log.i("isFocus聚焦状态：",String.valueOf(isFocus));
        }
        //将自己的位置显示到地图上
//        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
//        locationBuilder.latitude(location.getLatitude());
//        locationBuilder.longitude(location.getLongitude());
//
//        MyLocationData locationData = locationBuilder.build();

        //Toast.makeText(context,"定位",Toast.LENGTH_SHORT).show();
        MyLocationData locationData= new MyLocationData.Builder()
                .direction(mCurrentX)//设定图标方向
                .accuracy(location.getRadius())//getRadius 获取定位精度,默认值0.0f
                .latitude(location.getLatitude())//百度纬度坐标
                .longitude(location.getLongitude())//百度经度坐标
                .build();
        baiduMap.setMyLocationData(locationData);

        //配置定位图层显示方式，使用自己的定位图标
        MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration
                .LocationMode.NORMAL, true, bitmapDescriptor);
        baiduMap.setMyLocationConfigeration(configuration);

        CircleOptions circle = new CircleOptions().center(ll).fillColor(0x00B2BF).radius(100);
        baiduMap.addOverlay(circle);

        if (stationLatlngs != null && stationLatlngs.contains(ll)) {
            Intent intent = new Intent("com.example.acer.bus1.current_station");
            String station = stationList.get(stationLatlngs.indexOf(ll));
            sendCurrentStation(ll);
            if (stationLatlngs.lastIndexOf(ll) == stationLatlngs.size()-1) {
                sendFinishLine();
                Intent intent1 = new Intent("com.example.acer.bus1.Activity.TaskLine");
                intent1.putExtra("isFinish",true);
                context.sendBroadcast(intent1);
            }
            intent.putExtra("CurrentStation", station);
            context.sendBroadcast(intent);
            Toast.makeText(context,location.getAddress().toString(),Toast.LENGTH_LONG).show();
        }

    }

    //返回到自己的当前位置
    public void returnCurrentLocation() {
        isFocus = true;
        if (currentLocation == null) {
            return;
        }
        LatLng ll = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(ll, 18f);
        baiduMap.animateMapStatus(update);
    }

    //TODO 发送当前所在的站点
    private void sendCurrentStation(LatLng ll) {
        String station = stationList.get(stationlls.indexOf(ll));
        RequestBody requestBody = new FormBody.Builder()
                .add("name",station)
                .add("position_y",String.valueOf(ll.latitude))
                .add("position_x",String.valueOf(ll.longitude))
                .add("introduce",station + "公交站点")
                .build();
        HttpUtil.postOKHttpRequest(UrlAPI.CurrentLocationUrl, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}

            @Override
            public void onResponse(Call call, Response response) throws IOException {}
        });
    }

    //TODO 发送数据告知当前路线已经行驶完毕
    private void sendFinishLine() {
        RequestBody requestBody = new FormBody.Builder()
                .add("isFinish","1")
                .build();
        HttpUtil.postOKHttpRequest(UrlAPI.SearchLine, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    //定位监听
    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onConnectHotSpotMessage(String s, int i) {}

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            ConnectivityManager manager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info == null || !info.isAvailable() || !info.isConnected()) {
                Toast.makeText(context,"当前网络不可用\n请检查网络连接后重新绘制路线",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (bdLocation != null) {
                navigateTo(bdLocation);
            }

            //TODO 如果到达某一个公交站点，则发送一条广播

        }
    }

    //设置公交节点的准确地理位置
    public void setStationLL(ArrayList<String> stationList) {

        this.stationList = stationList;
        stationlls.clear();
        context.registerReceiver(receiver,intentFilter);
        tag = 0;

        //城市内的公交、地铁路线检索
        //首先发起POI检索，查找是否为公交路线，如果是则再发起公交路线检索
        poiCitySearchOptions.city(CITY);//城市检索的数据设置
        poiCitySearchOptions.keyword(stationList.get(tag));
        poiSearch.searchInCity(poiCitySearchOptions);

    }

    public void setStationLatlngs(List<LatLng> stationLatlngs) {
        this.stationLatlngs = stationLatlngs;
    }

    //Poi搜索监听者
    private void InitPoiListener() {
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.acer.bus1.Class.POISearch");
        receiver = new SearchReceiver();
        poiCitySearchOptions = new PoiCitySearchOption();
        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {

                // 如果没有错误
                if (poiResult != null && poiResult.error == PoiResult.ERRORNO.NO_ERROR) {
                    //遍历所有数据
                    for (int i = 0; i < poiResult.getAllPoi().size(); i++) {
                        //获取里面的数据对象
                        PoiInfo poiInfo = poiResult.getAllPoi().get(i);
                        if (poiInfo.type == PoiInfo.POITYPE.BUS_STATION){
                            LatLng latLng = poiInfo.location;
                            stationlls.add(latLng);
                            tag++;
                            if(tag < stationList.size()) {
                                Intent intent = new Intent("com.example.acer.bus1.Class.POISearch");
                                context.sendBroadcast(intent);
                            } else {
                                context.unregisterReceiver(receiver);
                            }

                            return;
                        }
                    }
                } else {
                    tag++;
                    Toast.makeText(context, "搜索不到你需要的信息！", Toast.LENGTH_SHORT).show();
                    if(tag < stationList.size()) {
                        Intent intent = new Intent("com.example.acer.bus1.Class.POISearch");
                        context.sendBroadcast(intent);
                    } else {
                        context.unregisterReceiver(receiver);
                    }
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(context, "抱歉，未找到结果",
                            Toast.LENGTH_SHORT).show();
                } else {// 正常返回结果的时候，此处可以获得很多相关信息
                    Toast.makeText(context, poiDetailResult.getName() + ": "
                                    + poiDetailResult.getAddress(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {}
        });
    }

    //poi依次检索公交站点的准确地理位置广播接收器
    class SearchReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            poiCitySearchOptions.keyword(stationList.get(tag));
            poiSearch.searchInCity(poiCitySearchOptions);
        }
    }

    //初始化地理编码监听者
    private void InitGeoCoderListener() {
        geoCoder = GeoCoder.newInstance();
        //设置查询结果监听者
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null
                        || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    return;
                }
                LatLng latLng = geoCodeResult.getLocation();
                PlanNode Node = PlanNode.withLocation(latLng);
                //station_ll.add(Node);
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {}
        });
    }

    //地理编码
    public void GetAllLocation() {

        //新建查询对象要查询的条件
//        for (String station : stationlist) {
//            geoCodeOption = new GeoCodeOption().city(CITY).address(station);
//            //发起地理编码请求
//            geoCoder.geocode(geoCodeOption);
//        }

    }
}
