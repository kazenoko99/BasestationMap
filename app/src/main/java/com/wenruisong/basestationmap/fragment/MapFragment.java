package com.wenruisong.basestationmap.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.model.LatLng;
import com.wenruisong.basestationmap.MainActivity;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.Marker.MarkerManager;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.map.MapController;
import com.wenruisong.basestationmap.map.MapManager;
import com.wenruisong.basestationmap.utils.MapViewUtils;
import com.wenruisong.basestationmap.view.BottomCellView;

/**
 * Created by wen on 2016/1/15.
 */
public class MapFragment extends BackPressHandledFragment {
   public static MapFragment getInstance(Bundle bundle) {
        MapFragment backPressHandledFragment = new MapFragment();
        backPressHandledFragment.mBundle = bundle;
        return backPressHandledFragment;
    }

    public static MapView mMapView;
    public static PanoramaView panoramaView2;



    @Override
    public View inflateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         View v = inflater.inflate(R.layout.fragment_map,null);
        mMapView = (MapView)v.findViewById(R.id.bmapView);
        MapViewUtils.hideZoomView(mMapView);
        MapManager.getInstance().init(v,mMapView.getMap());
        panoramaView2 = (PanoramaView)v.findViewById(R.id.panorama);
        panoramaView2.setVisibility(View.GONE);
       // panoramaView2.setPanorama(GEO_PINGXIANG.longitude,GEO_PINGXIANG.latitude);
        return v;
    }

}
