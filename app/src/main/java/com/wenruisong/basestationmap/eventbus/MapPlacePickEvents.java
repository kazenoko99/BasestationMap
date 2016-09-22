package com.wenruisong.basestationmap.eventbus;

import com.amap.api.maps.model.Poi;
import com.squareup.otto.Bus;

/**
 * Created by wen on 2016/2/18.
 */
public class MapPlacePickEvents {
    private static final Bus BUS = new Bus();

    public static Bus getBus() {
        return BUS;
    }

    private MapPlacePickEvents() {
        // No instances.
    }

    public static class OnPlacePick{
        public Poi mPoi;
        public OnPlacePick(Poi poi) {
            this.mPoi = poi;
        }
    }
}