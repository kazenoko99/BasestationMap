package com.wenruisong.basestationmap.eventbus;

import com.squareup.otto.Bus;

import java.io.File;

/**
 * Created by wen on 2016/2/18.
 */
public class MapToolsEvents  {
    private static final Bus BUS = new Bus();

    public static Bus getBus() {
        return BUS;
    }

    private MapToolsEvents() {
        // No instances.
    }

    public static class OnClickTools {
        public int mIndex;

        public OnClickTools(int index) {
            mIndex = index;
        }
    }
}