package com.wenruisong.basestationmap.basestation;

import com.baidu.mapapi.model.LatLng;
import com.wenruisong.basestationmap.utils.Constants;

/**
 * Created by wen on 2016/1/16.
 */
public class Cell {
    public int index;
    public String bsName;
    public  int cellid;
    public String cellName;
    public LatLng latLng;
    public LatLng baiduLatLng;
    public float azimuth;
    public float tatal_downtilt;
    public int highth;
    public float downtilt;
    public String address;
    public boolean isShow = false;
    public int type;//是否是室分 1/h为室分，0为宏站
    public enum CellType
    { GSM,TDS,LTE,CDMA,CDMA2000,WCDMA }

    public int getInstanceType(){
        if(this instanceof GSMCell)
            return Constants.GSM;
        else if (this instanceof LTECell)
            return Constants.LTE;
        else return Constants.TYPE_UNKNOWN;
    }

}
