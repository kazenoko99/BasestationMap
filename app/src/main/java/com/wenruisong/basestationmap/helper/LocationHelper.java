package com.wenruisong.basestationmap.helper;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.wenruisong.basestationmap.BasestationMapApplication;
import com.wenruisong.basestationmap.utils.Logs;

/**
 * Created by wen on 2016/1/16.
 */
public class LocationHelper {
    private BaiduMap mBaiduMap;
    public static String city;
    public static BDLocation location;
    private static LocationClient mLocClient;
    private static LocationListenner locationListenner;
    private boolean isFirstLoc = true;
    public boolean isLocated = false;
    private static RequestLocationCallBack requestLocationCallBack;
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
        directionHelper = DirectionHelper.getInstance();
        locationListenner = new LocationListenner();
        mLocClient = new LocationClient(BasestationMapApplication.getContext());
        mLocClient.registerLocationListener(locationListenner);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setAddrType("all");
        option.setPriority(LocationClientOption.NetWorkFirst);
        option.setPriority(LocationClientOption.GpsFirst);       //gps
        option.disableCache(true);
        mLocClient.setLocOption(option);
    }
    public void setBaiduMap(BaiduMap map)
    {
        mBaiduMap = map;
    }

    public static void StartLocating()
    {
        mLocClient.start();
    }
    public static void StopLocating()
    {
        mLocClient.stop();
    }

    public void RequestLocating(RequestLocationCallBack mCallback)
    {
        requestLocationCallBack= mCallback;
        mLocClient.start();
    }
    public class LocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            Logs.d("hahaha","LocationCity"+location.getAddrStr());
            if (location == null)
                return;
            if(location.getCity()!=null && isLocated==false) {
                LocationHelper.location = location;
                city = location.getCity();
                Logs.d("hahaha","getCity"+city);
                requestLocationCallBack.run(city+location.getDistrict());
                isLocated = true;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(directionHelper.mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                 isFirstLoc = false;
//                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(new LatLng(location.getLatitude(),
//                        location.getLongitude()));
//                mBaiduMap.animateMapStatus(u);
           }
        }
        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

     public  interface RequestLocationCallBack{
         void run(String city);
     }
}
