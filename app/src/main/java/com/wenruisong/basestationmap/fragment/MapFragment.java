package com.wenruisong.basestationmap.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.model.LatLng;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.Basestation;
import com.wenruisong.basestationmap.basestation.BasestationManager;
import com.wenruisong.basestationmap.basestation.CellMarkerManager;
import com.wenruisong.basestationmap.basestation.GSMCell;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.map.MapController;
import com.wenruisong.basestationmap.utils.MapViewUtils;

/**
 * Created by wen on 2016/1/15.
 */
public class MapFragment extends BackPressHandledFragment {
    private static final LatLng GEO_PINGXIANG = new LatLng(27.64096, 113.8677);
    private static MapView mMapView;
    private static BaiduMap mBaiduMap;
    private static float mapZoomLevel = 17.0f;
    private static MapController mapController;
    BasestationManager btsManager = BasestationManager.getInstance();
    CellMarkerManager markerManager = CellMarkerManager.getInstance();
    BitmapDescriptor mCurrentMarker;
    private LocationHelper locationHelper;

    public static MapFragment getInstance(Bundle bundle) {
        MapFragment backPressHandledFragment = new MapFragment();
        backPressHandledFragment.mBundle = bundle;
        return backPressHandledFragment;
    }

    public BaiduMap getBaiduMap() {
        return this.mBaiduMap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) v.findViewById(R.id.bmapView);
        MapViewUtils.hideZoomView(mMapView);
        mapController = (MapController) v.findViewById(R.id.map_controller);
        mCurrentMarker = null;
        mBaiduMap = mMapView.getMap();
        mapController.setController(this, mBaiduMap);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker));
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(GEO_PINGXIANG));
        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(mapZoomLevel);
        mBaiduMap.animateMapStatus(u);
        locationHelper = LocationHelper.getInstance();
        locationHelper.setBaiduMap(mBaiduMap);

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            public void onMapStatusChangeStart(MapStatus status) {
                markerManager.showMarkers(mBaiduMap);
            }

            public void onMapStatusChangeFinish(MapStatus status) {

            }

            public void onMapStatusChange(MapStatus status) {
            }
        });
        return v;
    }

    @Override
    public View inflateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null;
    }

    public void changeMode(int mode) {
        switch (mode) {
            case 0:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case 1:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case 2:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            default:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
        }
    }

    public void zoomIn() {
        mapZoomLevel = mBaiduMap.getMapStatus().zoom;
        mapZoomLevel++;
        if (mapZoomLevel >= 20)
            mapZoomLevel = 20;
        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(mapZoomLevel);
        mBaiduMap.animateMapStatus(u);
    }

    public void zoomOut() {
        mapZoomLevel = mBaiduMap.getMapStatus().zoom;
        mapZoomLevel--;
        if (mapZoomLevel <= 3)
            mapZoomLevel = 3;
        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(mapZoomLevel);
        mBaiduMap.animateMapStatus(u);
    }

    public void toggleLocatingMode(MyLocationConfiguration.LocationMode mCurrentMode) {
        switch (mCurrentMode) {
            case NORMAL:
                mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                        mCurrentMode, true, mCurrentMarker));
                break;
            case COMPASS:
                mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                        mCurrentMode, true, mCurrentMarker));
                break;
            default:
                break;
        }
    }
}
