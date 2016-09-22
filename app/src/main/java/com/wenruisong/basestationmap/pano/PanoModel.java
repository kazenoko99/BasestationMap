package com.wenruisong.basestationmap.pano;

import android.util.Log;

import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.LatLng;
import com.baidu.pano.platform.comjni.JNITool;
import com.wenruisong.basestationmap.BasestationMapApplication;

/**
 * Created by wen on 2016/3/30.
 */
public class PanoModel {
    // onLoadPanoramaEnd : {"ID":"02003800001412161513443307B","Mode":"day","MoveDir":209.386,"Rname":"滨河东路","Type":"street","Z":78.143,"X":12675778,"Y":3184014}
   public String MoveDir;
    public String Rname;
    public String Type;
    public float Z;
    public double X;
    public double Y;
    public double lat;
    public double lng;
    public LatLng latLng ;
    CoordinateConverter mCoordinateConverter = new CoordinateConverter(BasestationMapApplication.getContext());
    public void transCoordinate(){
        Log.d("transCoordinate","X is"+X+"Y is"+ Y);
          lng = (double) JNITool.mc2ll(X, Y).x;
          lat = (double)JNITool.mc2ll(X, Y).y;

        latLng = new LatLng(lat,lng);
        latLng = mCoordinateConverter.from(CoordinateConverter.CoordType.BAIDU).coord(latLng).convert();
    }
}
