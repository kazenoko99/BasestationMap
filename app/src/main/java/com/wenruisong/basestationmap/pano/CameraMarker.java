package com.wenruisong.basestationmap.pano;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.Marker.CellMarker;

/**
 * Created by wen on 2016/3/4.
 */
public class CameraMarker   {
    private  Marker cameraMaker;
    private static BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.pano_indicator);
    private MarkerOptions marker_gsm = new MarkerOptions().zIndex(8).icon(bitmapDescriptor);
    private float mRotate = 0;
    private LatLng mLatLng;

    public void update(float rotate, LatLng latLng){
        mRotate = rotate;
        mLatLng = latLng;
    }

    public void showInMap(BaiduMap baiduMap) {
        if (cameraMaker == null) {
            cameraMaker = (Marker) baiduMap.addOverlay(marker_gsm.position(mLatLng).rotate(mRotate));
        } else {
            cameraMaker.setPosition(mLatLng);
            cameraMaker.setRotate(mRotate);
        }
    }

}
