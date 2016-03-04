package com.wenruisong.basestationmap.basestation;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.wenruisong.basestationmap.R;

/**
 * Created by wen on 2016/1/25.
 */
//public  int cellid;
//public String cellName;
//public LatLng latLng;
//public float azimuth;
//public float tatal_downtilt;
//public float downtilt;
public class GSMCell extends Cell {
    public int lac;
    public int bcch;

    public boolean isSelected =false;


}
