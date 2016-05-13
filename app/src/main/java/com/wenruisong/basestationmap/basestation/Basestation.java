package com.wenruisong.basestationmap.basestation;

import com.baidu.mapapi.model.LatLng;
import com.wenruisong.basestationmap.utils.Constants;

/**
 * Created by wen on 2016/1/16.
 */
public class Basestation {
    public String bsName;
    public LatLng latLng;
    public LatLng baiduLatLng;
    public String address;
    public int type;

    public int basestationIndex;
    public boolean isNameShow =false;
    public boolean isMakerShow =false;

    public int getInstanceType(){
        if(this instanceof GSMBasestation)
             return Constants.GSM;
        else if (this instanceof LTEBasestation)
             return Constants.LTE;
        else return Constants.TYPE_UNKNOWN;
    }

}
