package com.example.acer.bus1.Class;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.acer.bus1.R;
import com.example.acer.bus1.Service.MyService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by acer on 2017/6/11.
 */

public class MySearchLine {


    private static String CITY = "武汉";
    private Context context;
    private BaiduMap baiduMap;
    private RoutePlanSearch routePlanSearch = null;

    static public int tag;//标记
    static public int allNum;
    private TransitRoutePlanOption transoption;
    private IntentFilter intentFilter;
    private DrawLineReceiver receiver;
    private ProgressDialog progressDialog;
    //路线的全部站点
    private List<Site> stationSites;
    private List<PlanNode> planNodeList;

    //public static List<LatLng> StartEndLL = new ArrayList<>();


//    public MySearchLine(Context context, BaiduMap baiduMap, List<String> list) {
//        this.context = context;
//        this.baiduMap = baiduMap;
//        this.list = list;
//        this.stationList = new ArrayList<>();
//
//        allNum = list.size();
//        //初始化路线搜索监听接口
//        InitListener();
//        //转换为车站节点
//        translateNode();
//    }

//    public MySearchLine(Context context, BaiduMap baiduMap, List<LatLng> stationsLL,
//                        List<String> stationsName) {
//        this.context = context;
//        this.baiduMap = baiduMap;
//        this.stationsLL = stationsLL;
//        allNum = stationsLL.size();
//        this.stationList = new ArrayList<>();
//        this.stationSites = new ArrayList<>();
//        this.stationsName = stationsName;
//        //初始化路线搜索监听接口
//        InitListener();
//        InitMapClick();
//    }

    public MySearchLine(Context context,BaiduMap baiduMap,List<Site> stationSites) {
        this.context = context;
        this.baiduMap = baiduMap;
        this.stationSites = stationSites;
        allNum = stationSites.size();
        planNodeList = new ArrayList<>();
        //初始化路线搜索监听接口
        InitListener();
        InitMapClick();
    }

    public List<Site> getStationList() {
        return stationSites;
    }

    public void setStationSites(List<Site> stationSites) {
        this.stationSites = stationSites;
        allNum = stationSites.size();
    }

    public String getCITY() {
        return CITY;
    }

    public void setCITY(String CITY) {
        this.CITY = CITY;
    }

    //驾车、步行路线规划监听者
    private void InitListener() {
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.acer.bus1.Class.drawLine");
        receiver = new DrawLineReceiver();
        transoption = new TransitRoutePlanOption();
        routePlanSearch = RoutePlanSearch.newInstance();
        routePlanSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            //步行路线结果回调
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
            }

            //换乘线结果回调
            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
                tag++;
                //currentSite = stationSites.get(tag);
                if (transitRouteResult == null
                        || transitRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //Toast.makeText(context, list.get(tag - 1) + "站到" + list.get(tag) +
                    //                "站的路线未找到\n司机可以在地图上查看自行规划路线",
                    //        Toast.LENGTH_LONG).show();
                    if (tag < stationSites.size() - 1) {
                        Intent intent = new Intent("com.example.acer.bus1.Class.drawLine");
                        context.sendBroadcast(intent);
                    } else {
                        progressDialog.dismiss();
                        context.unregisterReceiver(receiver);
                    }
                    return;
                }
                if (transitRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    TransitRouteOverlay transitRouteOverlay = new TransitRouteOverlay(baiduMap);
                    transitRouteOverlay.setData(transitRouteResult.getRouteLines().get(0));

                    baiduMap.setOnMarkerClickListener(transitRouteOverlay);
                    transitRouteOverlay.addToMap();
                    transitRouteOverlay.zoomToSpan();

                    if (tag < stationSites.size() - 1) {
                        Intent intent = new Intent("com.example.acer.bus1.Class.drawLine");
                        context.sendBroadcast(intent);
                    } else {
//                        Toast.makeText(context,String.valueOf(StartEndLL.size()),Toast.LENGTH_SHORT).show();
//                        List<OverlayOptions> overlayOptionses = new ArrayList<OverlayOptions>();
//                        List<LatLng> list = new ArrayList<>();
//                        for (LatLng ll : StartEndLL) {
//                            list.add(ll);
//                        }
//                        for (int i = 1; i < StartEndLL.size()-1; i++) {
//                            list.add(StartEndLL.get(i));
//                            list.add(StartEndLL.get(++i));
//                            overlayOptionses.add(new PolylineOptions()
//                                    .points(list).width(10).color(Color.argb(178, 0, 78, 255))
//                                    .zIndex(0));
//                        }
//
//                        for (OverlayOptions option : overlayOptionses) {
//                            baiduMap.addOverlay(option);
//                        }

                        progressDialog.dismiss();
                        context.unregisterReceiver(receiver);
                    }

                }
            }

            //跨城公共交通路线结果回调
            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
            }

            //驾车路线结果回调
            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
                if (drivingRouteResult == null
                        || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(context, "sorry，抱歉，未找到结果",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {

                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            baiduMap);
                    // 设置一条驾车路线方案
                    drivingRouteOverlay.setData(drivingRouteResult.getRouteLines().get(0));
                    //drivingRouteOverlay.setData(drivingRouteResult.getRouteLines().get(0),
                    //        stationSites.get(0),stationSites.get(stationSites.size()-1));

                    //baiduMap.setOnMarkerClickListener(drivingRouteOverlay);
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();

                }
            }

            //室内路线规划回调
            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
            }

            // 骑行路线结果回调
            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
            }
        });
        baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {}

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
                baiduMap.hideInfoWindow();
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {

            }
        });
    }

    //驾车路线规划
    public void drive() {

        Log.i("驾车站点数:",""+stationSites.size());
        baiduMap.clear();
        planNodeList.clear();
        final DrivingRoutePlanOption driveOption = new DrivingRoutePlanOption();
        PlanNode Node;
        for (Site site : stationSites) {
            Node = PlanNode.withLocation(site.getLatLng());
            planNodeList.add(Node);
        }

        PlanNode fromNode = planNodeList.get(0);
        PlanNode toNode = planNodeList.get(allNum - 1);
        driveOption.from(fromNode).to(toNode);

        planNodeList.remove(allNum - 1);
        planNodeList.remove(0);
        if (planNodeList.size() != 0) {
            driveOption.passBy(planNodeList);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                addMarker(stationSites);
                //设置驾车策略，避免拥堵.ECAR_AVOID_JAM
                driveOption.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_FEE_FIRST);
                //发起驾车检索
                routePlanSearch.drivingSearch(driveOption);
            }
        }).start();

    }

    //添加覆盖物
    private void addMarker(List<Site> stationSites) {
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.marker_station);
        //BitmapDescriptor bitmap = BitmapDescriptorFactory.fromAssetWithDpi("Icon_bus_station.png");
        LatLng latLng = null;
        Marker marker;
        OverlayOptions options;

        for (Site site: stationSites) {
            latLng = site.getLatLng();
            options = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromAssetWithDpi("Icon_bus_station.png"));
            baiduMap.addOverlay(options);
            options = new MarkerOptions()
                    .position(latLng)
                    .icon(bitmap)
                    .zIndex(9)
                    .draggable(true);//设置手势
            marker = (Marker) (baiduMap.addOverlay(options));
            Bundle bundle = new Bundle();
            bundle.putSerializable("station",site);
            marker.setExtraInfo(bundle);
        }
//        //添加起点
//        options = new MarkerOptions().position(stationsLL.get(0))
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_start))
//                .zIndex(9).draggable(true);
//        marker = (Marker) (baiduMap.addOverlay(options));
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("station",stationSites.get(0));
//        marker.setExtraInfo(bundle);
//        //添加终点
//        options = new MarkerOptions().position(stationsLL.get(stationsLL.size()-1))
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_end))
//                .zIndex(9).draggable(true);
//        marker = (Marker) (baiduMap.addOverlay(options));
//        Bundle bundle1 = new Bundle();
//        bundle1.putSerializable("station",stationSites.get(stationSites.size()-1));
//        marker.setExtraInfo(bundle1);
        MapStatusUpdate mapStatusUpdate =
                MapStatusUpdateFactory.newLatLngZoom(latLng, 19);
        baiduMap.setMapStatus(mapStatusUpdate);

    }

    //地图覆盖物点击事件
    private void InitMapClick() {
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle bundle = marker.getExtraInfo();
                if (bundle == null) {
                    return true;
                }
                Site site = (Site) bundle.getSerializable("station");
                if (site == null) {
                    return true;
                }
                //infowindow中的布局
                View view = ((Activity)context).getLayoutInflater().inflate(R.layout.infowindow,null);
                TextView tv = (TextView) view.findViewById(R.id.infowindow_tv);
                tv.setText(site.getName());
                //view.setElevation(8f);
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
                //infowindow位置
                LatLng latLng = site.getLatLng();
                //infowindow点击事件
                InfoWindow.OnInfoWindowClickListener listener = new InfoWindow
                        .OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick() {
                        //隐藏infowindow
                        baiduMap.hideInfoWindow();
                    }
                };
                //显示infowindow
                InfoWindow infoWindow = new InfoWindow(bitmapDescriptor, latLng, -47, listener);
                baiduMap.showInfoWindow(infoWindow);
                return true;
            }
        });
    }

    //公交路线规划
    public void PlanBusLine(ProgressDialog progressDialog) {

        this.progressDialog = progressDialog;
        context.registerReceiver(receiver, intentFilter);
        tag = 0;

        if (planNodeList.size() == 0) {
            return;
        }
        transoption.city(CITY);
        //依次遍历全部节点
        transoption.from(planNodeList.get(tag)).to(planNodeList.get(tag + 1));
        //设置路线规划策略
        transoption.policy(TransitRoutePlanOption.TransitPolicy.EBUS_TRANSFER_FIRST);
        routePlanSearch.transitSearch(transoption);

    }

    class DrawLineReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            transoption.from(planNodeList.get(tag)).to(planNodeList.get(tag + 1));
            //设置路线规划策略
            transoption.policy(TransitRoutePlanOption.TransitPolicy.EBUS_TRANSFER_FIRST);
            routePlanSearch.transitSearch(transoption);

        }
    }

}
