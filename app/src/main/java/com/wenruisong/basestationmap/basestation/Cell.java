package com.wenruisong.basestationmap.basestation;

import com.baidu.location.Address;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;

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

}
