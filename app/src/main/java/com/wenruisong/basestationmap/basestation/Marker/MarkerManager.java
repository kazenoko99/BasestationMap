package com.wenruisong.basestationmap.basestation.Marker;

import android.database.sqlite.SQLiteDatabase;

import com.baidu.mapapi.map.BaiduMap;
import com.wenruisong.basestationmap.basestation.BasestationManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wen on 2016/2/28.
 */
public class MarkerManager {

    private static MarkerManager.MarkerType mMarkerType = MarkerManager.MarkerType.GSM;
    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

    private static MarkerManager instance;

    public static MarkerManager getInstance() {
        if (instance == null)
            instance = new MarkerManager();
        return instance;
    }

    public MarkerManager() {

    }



    public void setMap(BaiduMap map) {
        ShowGsmCellMarkerTask.setMap(map);
        ShowLteCellMarkerTask.setMap(map);
    }


    public void setMarkerType(MarkerType type) {
        mMarkerType = type;
        ShowGsmCellMarkerTask.clear();
        ShowLteCellMarkerTask.clear();
        showMarkers();
    }

    public enum MarkerType {GSM, LTE, GSM_LTE, NONE}

    public void showMarkers() {
        switch (mMarkerType) {
            case GSM:
                fixedThreadPool.execute(new ShowGsmCellMarkerTask());
                break;
            case LTE:
                fixedThreadPool.execute(new ShowLteCellMarkerTask());
                break;
            case GSM_LTE:
                fixedThreadPool.execute(new ShowGsmCellMarkerTask());
                fixedThreadPool.execute(new ShowLteCellMarkerTask());
                break;
            case NONE:
                break;
            default:
                break;
        }
    }
}
