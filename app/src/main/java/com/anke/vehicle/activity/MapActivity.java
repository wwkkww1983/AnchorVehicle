package com.anke.vehicle.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anke.vehicle.R;
import com.anke.vehicle.utils.Utility;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

/**
 * 百度地图搜索地理位置信息
 */
public class MapActivity extends Activity {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private LocationClient locationClient;
    private GeoCoder mSearch = null;
    private TextView locationSearch;
    private Button btBack;

    private String address = "";
    private double latitude = 0.0;//定位经纬度
    private double longitude = 0.0;
    private boolean isFirstLoc = true;//用于判断是否是第一次定位
    private String city;
    private int type = -1;//用于判断跳转事件的类型
    private String searchAddress = "";//搜索的地理位置信息
    private boolean flag = true;//用于判断是否搜索地理位置信息是否已经回传

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.map);
        locationSearch = (TextView) findViewById(R.id.location_search);
        btBack = (Button) findViewById(R.id.btback);
        Intent intent = getIntent();
        type = intent.getExtras().getInt("type"); // 获取跳转类型
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mMapView.showScaleControl(true); // 设置是否显示比例尺控件
        mMapView.showZoomControls(false);// 设置是否显示缩放控件
        mMapView.removeViewAt(1);// 删除百度地图LoGo
        mBaiduMap.setMyLocationEnabled(true);
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(myListener); // 注册监听函数
        this.setLocationOption(); // 设置定位参数
        locationClient.start(); // 开始定位

        /**
         * 地理位置信息搜索监听函数
         */
        locationSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MapActivity.this, BaiduMapPoiSearch.class);
                intent.putExtra("city", city);
                startActivityForResult(intent, 0);
            }
        });

        /**
         * 地理信息位置解码
         */
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //Toast.makeText(MapActivity.this, "抱歉，未能找到结果", Toast.LENGTH_SHORT).show();
                    Utility.ToastShow(MapActivity.this, "抱歉，未能找到结果");
                    return;
                }
                //设置地图中心点坐标
                MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(geoCodeResult.getLocation());
                mBaiduMap.animateMapStatus(status);
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    //Toast.makeText(MapActivity.this, "抱歉，未能找到结果", Toast.LENGTH_SHORT).show();
                    Utility.ToastShow(MapActivity.this, "抱歉，未能找到结果");
                    return;
                }
                //地理位置信息获取并显示
                mBaiduMap.clear();
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(reverseGeoCodeResult
                        .getLocation()));//改变地图状态
                city = reverseGeoCodeResult.getAddressDetail().city;//当前所在的城市信息
                address = reverseGeoCodeResult.getAddressDetail().city + ""
                        + reverseGeoCodeResult.getAddressDetail().district + ""
                        + reverseGeoCodeResult.getAddressDetail().street +
                        reverseGeoCodeResult.getAddressDetail().streetNumber;//地理位置信息
                if (flag) {
                    locationSearch.setText(address);//未跳转搜索路径，移动找地理位置信息
                } else {
                    flag = true;//搜索事件跳转到本事件，修改flag
                }
            }
        });

        /**
         * 百度地图状态改变监听函数
         */
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                LatLng ptCenter = mBaiduMap.getMapStatus().target; //获取地图中心点坐标
                latitude = ptCenter.latitude;//更新返回给NewTaskActivity的经纬度信息
                longitude = ptCenter.longitude;
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
            }
        });

        /**
         * 地址确定返回
         */
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backUpActivity();
            }
        });
    }

    /**
     * 设置定位参数
     */
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开GPS
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true); // 返回的定位结果包含手机机头的方向
        locationClient.setLocOption(option);
    }

    /**
     * 定位监听函数
     */
    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (isFirstLoc) {
                isFirstLoc = false;
                // map view 销毁后不在处理新接收的位置
                if (location == null || mBaiduMap == null)
                    return;
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(100).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();
                mBaiduMap.setMyLocationData(locData); // 设置定位数据

                city = location.getCity();
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                LatLng ll = new LatLng(latitude, longitude);
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16); //设置地图中心点以及缩放级别
                mBaiduMap.animateMapStatus(u);
            }
        }
    };

    /**
     * 数据保存，跳转到上一个Activity
     */
    void backUpActivity() {
        Intent intent = new Intent();
        if (!locationSearch.getText().toString().equals(""))
            intent.putExtra("WaitAddr", locationSearch.getText().toString());
        else
            intent.putExtra("WaitAddr", address);
        intent.putExtra("city", city);
        intent.putExtra("WaitAddrlatitude", String.valueOf(latitude));
        intent.putExtra("WaitAddrlongitude", String.valueOf(longitude));
        setResult(1, intent);
        MapActivity.this.finish();
    }

    /**
     * 返回按键保存数据
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        backUpActivity();
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取BaiduMapPoiSearch的数据信息
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == 0 && resultCode == 1) {//如果返回的搜索消息不为空就设置该位置信息，否则不更新
            if (!data.getStringExtra("searchAddress").equals("")) {
                locationSearch.setText(data.getStringExtra("searchAddress"));
                searchAddress = data.getStringExtra("searchAddress");
            } else {
                locationSearch.setText(address);
                searchAddress = address;
            }
            flag = data.getBooleanExtra("flag", false);
            mSearch.geocode(new GeoCodeOption().city(city).address(searchAddress));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        if (locationClient.isStarted())
            locationClient.stop();
        locationClient.unRegisterLocationListener(myListener);
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

}
