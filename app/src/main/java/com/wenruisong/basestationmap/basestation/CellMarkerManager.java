package com.wenruisong.basestationmap.basestation;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by wen on 2016/2/28.
 */
public class CellMarkerManager {
    private static String Tag = "CellMarkerManager";
    private final static int maxShowCells = 600;
    private final static int maxShowLocelles = 200;
    private final static int maxShowBs = 400;
    public static SQLiteDatabase basestationDB;
    private BasestationManager basestationManager;
    static LinkedHashMap<Cell, Boolean> cellsToShow = new LinkedHashMap<>();
    static LinkedHashMap<Basestation, Boolean> bsToShow = new LinkedHashMap<>();

    public static LinkedHashMap<Cell, CellMarker> lastShowCells = new LinkedHashMap();
    public static LinkedHashMap<Cell, LocelleMarker> lastShowlocelles = new LinkedHashMap();
    public static LinkedHashMap<Basestation, BsNameMaker> lastShowBsName = new LinkedHashMap();

    static LinkedList<CellMarker> markerPool = new LinkedList();
    static LinkedList<LocelleMarker> locellePool = new LinkedList<>();
    private static CellMarkerManager instance;


    public static CellMarkerManager getInstance() {
        if (instance == null)
            instance = new CellMarkerManager();
        return instance;
    }

    public CellMarkerManager() {
        basestationManager = BasestationManager.getInstance();
        basestationDB = basestationManager.basestationDB;
        for (int i = 0; i < maxShowCells; i++) {
            CellMarker cellMarker = new CellMarker();
            markerPool.offer(cellMarker);
        }

        for (int i = 0; i < maxShowLocelles; i++) {
            LocelleMarker locelleMarker = new LocelleMarker();
            locellePool.offer(locelleMarker);
        }

    }

    public void showMarkers(BaiduMap mBaiduMap)
    {
        ShowCellMarkerTask task = new ShowCellMarkerTask();
        task.execute(mBaiduMap);
        ShowBsNameTask showBsNameTask = new ShowBsNameTask();
        showBsNameTask.execute(mBaiduMap);
    }

    public class ShowCellMarkerTask extends AsyncTask<BaiduMap,Void,Void>{

        @Override
        protected Void doInBackground(BaiduMap... params) {
            showCellMarkers(params[0]);
            return null;
        }
    }

    public class ShowBsNameTask extends AsyncTask<BaiduMap,Void,Void>{

        @Override
        protected Void doInBackground(BaiduMap... params) {
            showBsName(params[0]);
            return null;
        }
    }


    public synchronized void showCellMarkers(BaiduMap mBaiduMap) {
        MapStatus ms = mBaiduMap.getMapStatus();
        Cursor cursor;
        String minlat = Double.toString(ms.target.latitude - 0.02);
        String maxlat = Double.toString(ms.target.latitude + 0.02);
        String minlng = Double.toString(ms.target.longitude - 0.02);
        String maxlng = Double.toString(ms.target.longitude + 0.02);
        cursor = basestationDB.rawQuery("select CELLINDEX from gsm_cells where baidulat>? and baidulat<? and BAIDULON>? and BAIDULON<?", new String[]{minlat, maxlat, minlng, maxlng});
        cursor.moveToFirst();
        int count = Math.min(cursor.getCount(), maxShowCells);
        cellsToShow.clear();
        for (int i = 0; i < count; i++) {
            Cell cell = basestationManager.gsmCells.get(cursor.getInt(0));
            cellsToShow.put(cell, true);
            cursor.moveToNext();
        }
        cursor.close();
        Iterator iter= lastShowCells.entrySet().iterator();

        for (int i = 0; i < maxShowCells; i++) {
            if (!iter.hasNext())
                break;
            Map.Entry<Cell, CellMarker> entry = (Map.Entry) iter.next();
            Cell key = entry.getKey();
            CellMarker value = entry.getValue();
            if (cellsToShow.get(key) == null) {
                value.setLastCell(key);
                markerPool.offer(value);
                iter.remove();
            }
        }

        Iterator iter2 = lastShowlocelles.entrySet().iterator();
        for (int i = 0; i < maxShowLocelles; i++) {
            if (!iter2.hasNext())
                break;
            Map.Entry<Cell, LocelleMarker> entry = (Map.Entry) iter2.next();
            Cell key = entry.getKey();
            LocelleMarker value = entry.getValue();
            if (cellsToShow.get(key) == null) {
                value.setLastCell(key);
                locellePool.offer(value);
                iter2.remove();
            }
        }

        Iterator iter3 = cellsToShow.entrySet().iterator();
        for (int i = 0; i < maxShowCells; i++) {
            if (!iter3.hasNext())
                break;
            Map.Entry<Cell, CellMarker> entry = (Map.Entry) iter3.next();
            Cell key = entry.getKey();
            if (key.type == 1  && key.isShow ==false && lastShowlocelles.get(key) == null) {
                LocelleMarker locelleMarker = locellePool.poll();
                locelleMarker.setCell(key);
                lastShowlocelles.put(key, locelleMarker);
                locelleMarker.showInMap(mBaiduMap);
            }

            if (key.type == 0 && key.isShow ==false && lastShowCells.get(key) == null) {
                CellMarker marker = markerPool.poll();
                marker.setCell(key);
                lastShowCells.put(key, marker);
                marker.showInMap(mBaiduMap);
            }
        }
    }



    public synchronized void showBsName(BaiduMap mBaiduMap) {
        MapStatus ms = mBaiduMap.getMapStatus();
        String minlat = Double.toString(ms.target.latitude - 0.02);
        String maxlat = Double.toString(ms.target.latitude + 0.02);
        String minlng = Double.toString(ms.target.longitude - 0.02);
        String maxlng = Double.toString(ms.target.longitude + 0.02);
        Cursor cursor = basestationDB.rawQuery("select CELLINDEX from gsm_cells where baidulat>? and baidulat<? and BAIDULON>? and BAIDULON<? group by BS,LAT,LON", new String[]{minlat, maxlat, minlng, maxlng});
        cursor.moveToFirst();

        int count = Math.min(cursor.getCount(), maxShowBs);
        bsToShow.clear();
        for ( int i= 0; i<count; i++ )
        {
            Basestation gsmBS =basestationManager.gsmBSs.get(cursor.getInt(0));
            bsToShow.put(gsmBS, true);
            cursor.moveToNext();
        }
        cursor.close();

        Iterator iter= lastShowBsName.entrySet().iterator();
        for (int i = 0; i < maxShowBs; i++) {
            if (!iter.hasNext())
                break;
            Map.Entry<Basestation, BsNameMaker> entry = (Map.Entry) iter.next();
            Basestation key = entry.getKey();
            BsNameMaker value = entry.getValue();
            if (bsToShow.get(key) == null) {
                value.remove();
                iter.remove();
            }
        }


        Iterator iter3 = bsToShow.entrySet().iterator();
        for (int i = 0; i < maxShowBs; i++) {
            if (!iter3.hasNext())
                break;
            Map.Entry<Basestation, Boolean> entry = (Map.Entry) iter3.next();
            Basestation key = entry.getKey();
            if (lastShowBsName.get(key) == null) {
                BsNameMaker bsNameMaker = new BsNameMaker();
                bsNameMaker.setBs(key);
                bsNameMaker.showBsNameInMap(mBaiduMap);
            }
        }
    }
}
