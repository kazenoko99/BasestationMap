package com.wenruisong.basestationmap.map;

import android.app.Activity;
import android.view.View;

import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.model.LatLng;
import com.wenruisong.basestationmap.MainActivity;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.Marker.MarkerManager;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.view.BottomCellView;

/**
 * Created by wen on 2016/3/27.
 */
public class MapManager {
    private static MapManager instance;
    private static BaiduMap mBaiduMap;
    private static final LatLng GEO_PINGXIANG = new LatLng(27.64096, 113.8677);

    private static MapController mapController;
    private BottomCellView mapbottom;

    MarkerManager markerManager = MarkerManager.getInstance();
    BitmapDescriptor mCurrentMarker;
    private LocationHelper locationHelper;

    public static MapManager getInstance() {
        if (instance == null)
            instance = new MapManager();
        return instance;
    }


    public void init(View view,BaiduMap baiduMap) {
        mCurrentMarker = null;
        mBaiduMap = baiduMap;
        markerManager.setMap(mBaiduMap);
        mapController = (MapController) view.findViewById(R.id.map_controller);
        mapController.setController(mBaiduMap);
        mapbottom = mapController.mapBottomController.bottomCellView;
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

            }

            public void onMapStatusChangeFinish(MapStatus status) {
                markerManager.showMarkers();
                mapbottom.updateView(mBaiduMap);
            }

            public void onMapStatusChange(MapStatus status) {
            }
        });
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

    private static float mapZoomLevel = 17.0f;

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
