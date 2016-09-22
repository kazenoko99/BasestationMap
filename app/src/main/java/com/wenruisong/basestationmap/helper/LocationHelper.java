package com.wenruisong.basestationmap.helper;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.wenruisong.basestationmap.BasestationMapApplication;
import com.wenruisong.basestationmap.utils.Logs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wen on 2016/1/16.
 */
public class LocationHelper implements LocationSource {
    private AMap aMap;

    public static String getCity() {
        return city;
    }

    public static String city;
    public static AMapLocation location;
    private static AMapLocationClient mlocationClient;
    private  AMapLocationClientOption mLocationOption;
    private OnLocationChangedListener mOnLocationChangedListener;
    private static LocationListenner locationListenner;
    private List<RequestLocationCallBack> locationListenners = new ArrayList<>();
    private boolean isFirstLoc = true;
    public boolean isLocated = false;
    DirectionHelper directionHelper;
    private static LocationHelper instance;
    public static  LocationHelper getInstance()
    {
        if (instance==null)
            instance=new LocationHelper();
        return instance;
    }
    LocationHelper()
    {
        mlocationClient = new AMapLocationClient(BasestationMapApplication.getContext());
        directionHelper = DirectionHelper.getInstance();
        locationListenner = new LocationListenner();
        mlocationClient = new AMapLocationClient(BasestationMapApplication.getContext());
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(locationListenner);
        //设置为高精度定位模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        mlocationClient.startLocation();

    }
    public void setAmap(AMap map){
        aMap = map;
        aMap.setLocationSource(LocationHelper.getInstance());
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种'
        aMap.setMyLocationStyle(null);
    }

    public static void StartLocating()
    {
        mlocationClient.startLocation();
    }
    public static void StopLocating()
    {
        mlocationClient.stopLocation();
    }

    public void addLocationChangeListener(RequestLocationCallBack mCallback)
    {
        locationListenners.add(mCallback);
        mlocationClient.startLocation();
    }
    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mOnLocationChangedListener = listener;
        if (mlocationClient == null) {

        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
        mOnLocationChangedListener = null;
    }

    public class LocationListenner implements AMapLocationListener {
        @Override
        public void onLocationChanged(AMapLocation location) {
            Logs.d("hahaha","LocationCity"+location.getAddress());
            if (location == null)
                return;
            if(location.getCity()!=null && isLocated==false) {
                LocationHelper.location = location;
                city = location.getCity();
                Logs.d("hahaha","getCity"+city);
                for(RequestLocationCallBack requestLocationCallBack :locationListenners) {
                    requestLocationCallBack.onFirstLocated(location);
                }
                isLocated = true;
            }

            for(RequestLocationCallBack requestLocationCallBack :locationListenners) {
                requestLocationCallBack.onLocated(location);
            }

            if(mOnLocationChangedListener != null)
                mOnLocationChangedListener.onLocationChanged(location);// 显示系统小蓝点
//            MyLocationData locData = new MyLocationData.Builder()
//                    .accuracy(location.getRadius())
//                    // 此处设置开发者获取到的方向信息，顺时针0-360
//                    .direction(directionHelper.mCurrentDirection).latitude(location.getLatitude())
//                    .longitude(location.getLongitude()).build();
//            mAmap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
//                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(new LatLng(location.getLatitude(),
//                        location.getLongitude()));
//                mAmap.animateMapStatus(u);
            }
        }

    }

     public  interface RequestLocationCallBack{
         void onLocated(AMapLocation aMapLocation);
         void onFirstLocated(AMapLocation aMapLocation);
     }
}
