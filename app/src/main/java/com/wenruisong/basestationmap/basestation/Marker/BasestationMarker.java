package com.wenruisong.basestationmap.basestation.Marker;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Marker;
import com.wenruisong.basestationmap.basestation.Basestation;

/**
 * Created by wen on 2016/2/28.
 */
public abstract class BasestationMarker {

     Basestation mBasestation;
    public Marker basestaionMarker;

    public void setBasestation(Basestation basestation) {
        mBasestation = basestation;
    }


    abstract void showInMap(BaiduMap baiduMap);

    abstract void remove();

}
