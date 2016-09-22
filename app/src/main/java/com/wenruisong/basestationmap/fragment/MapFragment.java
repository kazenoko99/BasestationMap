package com.wenruisong.basestationmap.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.wenruisong.basestationmap.BasestationMapApplication;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.map.MapController;
import com.wenruisong.basestationmap.map.MapManager;
import com.wenruisong.basestationmap.utils.MapViewUtils;

/**
 * Created by wen on 2016/1/15.
 */
public class MapFragment extends BackPressHandledFragment {
   public static MapFragment getInstance(Bundle bundle) {
        MapFragment backPressHandledFragment = new MapFragment();
        backPressHandledFragment.mBundle = bundle;
        return backPressHandledFragment;
    }


    private MapView mapView;
    private AMap aMap;
    private View mapLayout;
    private MapController mMapController;



    @Override
    public View inflateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMapController = new MapController(getContext());
        if (mapLayout == null) {
            Log.d("starttime","MapFragment initview start"+(System.currentTimeMillis()- BasestationMapApplication.startTime));
            mapLayout = inflater.inflate(R.layout.fragment_map, null);
            mMapController.initWidget(mapLayout);
            Log.d("starttime","MapFragment inflate done"+(System.currentTimeMillis()- BasestationMapApplication.startTime));
            mapView = (MapView) mapLayout.findViewById(R.id.amapView);
            mapView.onCreate(savedInstanceState);
            Log.d("starttime","MapFragment mapview done"+(System.currentTimeMillis()- BasestationMapApplication.startTime));
            if (aMap == null) {
                aMap = mapView.getMap();
             //   MapViewUtils.hideLogo(mapView);
                MapViewUtils.hideZoomView(mapView);
            }
        }else {
            if (mapLayout.getParent() != null) {
                ((ViewGroup) mapLayout.getParent()).removeView(mapLayout);
            }
        }
        MapManager.getInstance().init(mMapController,mapView.getMap());
        Log.d("starttime","MapFragment initview done"+(System.currentTimeMillis()- BasestationMapApplication.startTime));
        return mapLayout;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
       // ((MainActivity) activity).onSectionAttached(Constants.MAP_FRAGMENT);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        Log.i("sys", "mf onResume");
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onPause() {
        Log.i("sys", "mf onPause");
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i("sys", "mf onSaveInstanceState");
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onDestroy() {
        Log.i("sys", "mf onDestroy");
        super.onDestroy();
        mapView.onDestroy();
    }
}
