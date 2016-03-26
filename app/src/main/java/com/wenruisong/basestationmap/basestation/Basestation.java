package com.wenruisong.basestationmap.basestation;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;

/**
 * Created by wen on 2016/1/16.
 */
public class Basestation {
    public String bsName;
    public LatLng latLng;
    public LatLng baiduLatLng;
    public String address;
    public int type;

    public int cellIndex;
    public boolean isNameShow =false;
    public boolean isMakerShow =false;

}
