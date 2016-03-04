package com.wenruisong.basestationmap.map;

import com.baidu.mapapi.map.offline.MKOfflineMap;

/**
 * Created by wen on 2016/2/3.
 */
public class OfflineMapManager {
    private static OfflineMapManager instance;
    public static  OfflineMapManager getInstance()
    {
        if (instance==null)
            instance=new OfflineMapManager();
        return instance;
    }

    public OfflineMapManager()
    {
        mOffline = new MKOfflineMap();
    }

    public  MKOfflineMap mOffline = null;

}
