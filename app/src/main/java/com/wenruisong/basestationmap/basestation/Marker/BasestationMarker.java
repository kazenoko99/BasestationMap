package com.wenruisong.basestationmap.basestation.Marker;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
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

    abstract void setTextSize(int size);
    abstract void showInMap(AMap aMap);

    abstract void remove();

}
