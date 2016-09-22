package com.wenruisong.basestationmap.tools;


import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;

/**
 * Created by wen on 2016/2/19.
 */
public class RulerPointMarker {
    private LatLng latLng;
    private Circle rulerPoint;
    CircleOptions rulerDot;
    RulerPointMarker(LatLng latLng)
    {
        this.latLng = latLng;
    }

    public void addPointInMap(AMap aMap) {
        if(aMap ==null)
            return;
        rulerDot = new CircleOptions().center(latLng).radius(20).zIndex(99).strokeWidth(0);
        rulerPoint= aMap.addCircle(rulerDot);
        rulerPoint.setFillColor(0xAAFF0000);
    }

    public void clearPoint() {
        if(rulerPoint!=null)
            rulerPoint.remove();
    }
}
