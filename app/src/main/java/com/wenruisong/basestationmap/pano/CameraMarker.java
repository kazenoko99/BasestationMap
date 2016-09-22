package com.wenruisong.basestationmap.pano;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.wenruisong.basestationmap.R;

/**
 * Created by wen on 2016/3/4.
 */
public class CameraMarker   {
    private Marker cameraMaker;
    private static BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.map_location_marker_rotate);
    private MarkerOptions marker_gsm = new MarkerOptions().anchor(0.5f,0.5f).zIndex(8).icon(bitmapDescriptor);
    private float mRotate = 0;
    private LatLng mLatLng;

    public void update(float rotate, LatLng latLng){
        mRotate = rotate;
        mLatLng = latLng;
    }

    public void showInMap(AMap aMap) {
        if (cameraMaker == null) {
            cameraMaker =  aMap.addMarker(marker_gsm.position(mLatLng));
            cameraMaker.setRotateAngle(mRotate);
        } else {
            cameraMaker.setPosition(mLatLng);
            cameraMaker.setRotateAngle(mRotate);
        }
    }

}
