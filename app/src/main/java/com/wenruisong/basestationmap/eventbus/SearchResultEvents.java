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
        public OnCellClick(LatLng cell,int index) {
            this.cellLatLng = cell;
            cellIndex = index;
        }
    }

    public static class OnPoiClick {
        public int type;
        public LatLng position;
        public OnPoiClick(int index) {
            type = index;
        }
    }
}