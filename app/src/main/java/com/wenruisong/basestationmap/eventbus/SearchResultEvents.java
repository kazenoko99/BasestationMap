package com.wenruisong.basestationmap.eventbus;

import com.baidu.mapapi.model.LatLng;
import com.squareup.otto.Bus;

/**
 * Created by wen on 2016/2/18.
 */
public class SearchResultEvents {
    private static final Bus BUS = new Bus();

    public static Bus getBus() {
        return BUS;
    }

    private SearchResultEvents() {
        // No instances.
    }

    public static class OnCellClick {
        public LatLng cellLatLng;
        public int cellIndex;
        public int netTypeFlag;
        public OnCellClick(LatLng cell,int index, int netTypeFlag) {
            this.cellLatLng = cell;
            cellIndex = index;
            this.netTypeFlag = netTypeFlag;
        }
    }

    public static class OnPoiClick {
        public LatLng position;
        public String key;
        public String address;
        public OnPoiClick(LatLng latLng,String key,String address) {
            position = latLng;
            this.address = address;
            this.key = key;
        }
    }
}