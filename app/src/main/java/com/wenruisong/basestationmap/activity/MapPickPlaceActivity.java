package com.wenruisong.basestationmap.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.eventbus.CommonAddressPickEvents;
import com.wenruisong.basestationmap.map.CustomizedMapView;
import com.wenruisong.basestationmap.model.CommonAddress;
import com.wenruisong.basestationmap.utils.AMapUtil;
import com.wenruisong.basestationmap.utils.Constants;

public class MapPickPlaceActivity extends AppCompatActivity  implements GeocodeSearch.OnGeocodeSearchListener,
        AMap.OnCameraChangeListener,View.OnClickListener{
    CustomizedMapView mCustomizedMapView;
    private AMap aMap;
    private TextView mPoiName;
    private TextView mPoiAddress;
    private LatLng mMapCenter;
    private static GeocodeSearch geocoderSearch;
    private RegeocodeQuery mRegeocodeQuery;
    private CommonAddress mCommonAddress;
    private TextView mPickComfirm;
    private int mType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getIntent().getIntExtra(Constants.TYPE, 0);
        if(mType == Constants.CommonPlacePic)
        CommonAddressPickEvents.getBus().register(this);

        setContentView(R.layout.activity_map_pick_place);
        mPickComfirm = (TextView)findViewById(R.id.comfirm);
        mPickComfirm.setEnabled(false);
        mPickComfirm.setOnClickListener(this);
        mPoiName = (TextView)findViewById(R.id.map_bottom_name);
        mPoiName.setText("地图位置");
        mPoiAddress = (TextView)findViewById(R.id.map_bottom_address);
        mCustomizedMapView = (CustomizedMapView)findViewById(R.id.customized_mapview);
        mCustomizedMapView.onCreate(savedInstanceState);
        aMap = mCustomizedMapView.getMap();
        aMap.setOnCameraChangeListener(this);
        mMapCenter = aMap.getCameraPosition().target;
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        mCommonAddress = new CommonAddress();
        mCommonAddress.addressType = Constants.COMMON_ADDRESS_TYPE_POI;
        LatLonPoint latLonPoint  = AMapUtil.convertToLatLonPoint(aMap.getCameraPosition().target);
        mRegeocodeQuery = new RegeocodeQuery(latLonPoint, 20, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        mRegeocodeQuery.setLatLonType(GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(mRegeocodeQuery);// 设置同步逆地理编码请求
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mType == Constants.CommonPlacePic)
        CommonAddressPickEvents.getBus().unregister(this);
        geocoderSearch = null;
        mCustomizedMapView.onDestroy();
    }


    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        if(regeocodeResult ==null)
            return;
        mPickComfirm.setEnabled(true);
        mPoiAddress.setText(regeocodeResult.getRegeocodeAddress().getFormatAddress());
        mCommonAddress.name = "地图选点";
        mCommonAddress.latLng = mMapCenter;
        mCommonAddress.address =  regeocodeResult.getRegeocodeAddress().getFormatAddress();
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        mPickComfirm.setEnabled(false);
        mPoiAddress.setText("查找中");
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        mRegeocodeQuery.setPoint(AMapUtil.convertToLatLonPoint(aMap.getCameraPosition().target));
        mRegeocodeQuery.setRadius(20);
        geocoderSearch.getFromLocationAsyn(mRegeocodeQuery);// 设置同步逆地理编码请求
        }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.comfirm:
                if(mType == Constants.CommonPlacePic) {
                    setResult(RESULT_OK, null);
                    CommonAddressPickEvents.getBus().post(new CommonAddressPickEvents.OnAddressPick(mCommonAddress));
                    finish();
                }

                if(mType == Constants.RoutePicPlace) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("NAME", mCommonAddress.address);
                    bundle.putDouble("LAT", mCommonAddress.latLng.latitude);
                    bundle.putDouble("LNG", mCommonAddress.latLng.longitude);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        Log.i("sys", "mf onResume");
        super.onResume();
        mCustomizedMapView.onResume();
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onPause() {
        Log.i("sys", "mf onPause");
        super.onPause();
        mCustomizedMapView.onPause();
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i("sys", "mf onSaveInstanceState");
        super.onSaveInstanceState(outState);
        mCustomizedMapView.onSaveInstanceState(outState);
    }

}
