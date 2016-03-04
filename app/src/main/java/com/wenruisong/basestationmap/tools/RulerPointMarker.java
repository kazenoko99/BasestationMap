package com.wenruisong.basestationmap.tools;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.wenruisong.basestationmap.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wen on 2016/2/19.
 */
public class RulerPointMarker {
    private LatLng latLng;
    private Overlay rulerPoint;
    OverlayOptions rulerDot;
    RulerPointMarker(LatLng latLng)
    {
        this.latLng = latLng;
    }

    public void addPointInMap(BaiduMap baiduMap) {
        if(baiduMap ==null)
            return;
        rulerDot = new DotOptions().center(latLng).radius(20).color(0xAAFF0000).zIndex(12);
        rulerPoint= baiduMap.addOverlay(rulerDot);
    }

    public void clearPoint() {
        if(rulerPoint!=null)
            rulerPoint.remove();
    }
}
