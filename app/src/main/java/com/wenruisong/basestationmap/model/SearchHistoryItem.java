package com.wenruisong.basestationmap.model;

import com.amap.api.maps.model.LatLng;

public class SearchHistoryItem {
    public String keyword;
    public String searchtype;
    public String time;
    public String id;
    public int cellindex;
    public int nettype;
    public LatLng latLng;
    public String address = "查询中。。。";
}
