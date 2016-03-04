package com.wenruisong.basestationmap.utils;

import android.view.View;
import android.widget.ZoomControls;

import com.baidu.mapapi.map.MapView;

/**
 * Created by wen on 2016/1/16.
 */
public class MapViewUtils {
    public static void hideZoomView(MapView mapView) {
        // 隐藏缩放控件
        int childCount = mapView.getChildCount();
        View zoom = null;
        for (int i = 0; i < childCount; i++) {
            View child = mapView.getChildAt(i);
            if (child instanceof ZoomControls) {
                zoom = child;
                break;
            }
        }
        zoom.setVisibility(View.GONE);
    }
}
