package com.wenruisong.basestationmap.utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;


/**
 * Created by wen on 2016/1/16.
 */
public class MapViewUtils {

    public static void hideZoomView(MapView mapView) {
        UiSettings mUiSettings = mapView.getMap().getUiSettings();
        // 隐藏缩放控件
        mUiSettings.setScaleControlsEnabled(false);
        mUiSettings.setZoomControlsEnabled(false);
    }


    public static void hideLogo(MapView mapView) {
        View child = mapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
    }
}
