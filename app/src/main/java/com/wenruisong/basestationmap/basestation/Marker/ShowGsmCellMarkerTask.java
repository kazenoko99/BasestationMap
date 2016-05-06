package com.wenruisong.basestationmap.basestation.Marker;

import android.database.Cursor;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.wenruisong.basestationmap.basestation.Basestation;
import com.wenruisong.basestationmap.basestation.BasestationManager;
import com.wenruisong.basestationmap.basestation.Cell;

/**
 * Created by wen on 2016/3/6.
 */
public class ShowGsmCellMarkerTask implements Runnable {
    private static BaiduMap mBaiduMap;
    private final static int maxShowCells = 200;
    private final static int maxShowBsName = 150;
    private final static int maxShowBsMaker = 550;
    private static Boolean stop = false;
    public static int cellCount;
    private static CellLruCache gsmCellsToShow = new CellLruCache(220);
    private static BasesationLruCache bsNameToShow = new BasesationLruCache(160);
    private static BasesationLruCache bsMakerToShow = new BasesationLruCache(600);
    private static float lastZoomLevel = 19;


    public static void setMap(BaiduMap map) {
        mBaiduMap = map;
    }

    public static void start() {
        stop = false;
    }

    public static synchronized void clear() {
        stop = true;
        bsMakerToShow.trimToSize(0);
        gsmCellsToShow.trimToSize(0);
        bsNameToShow.trimToSize(0);
       // mBaiduMap.clear();
        stop = false;
    }

    public static synchronized void clearExceptName() {
        stop = true;
        bsMakerToShow.trimToSize(0);
        gsmCellsToShow.trimToSize(0);
        // mBaiduMap.clear();
        stop = false;
    }

    public static synchronized void clearName() {
        stop = true;
        bsNameToShow.trimToSize(0);
        // mBaiduMap.clear();
        stop = false;
    }


    @Override
    public void run() {
        showGsmCellMarkers();
    }

    public synchronized void showGsmCellMarkers() {

        MapStatus ms = mBaiduMap.getMapStatus();
        String minlat = Double.toString(ms.bound.southwest.latitude);
        String maxlat = Double.toString(ms.bound.northeast.latitude);
        String minlng = Double.toString(ms.bound.southwest.longitude);
        String maxlng = Double.toString(ms.bound.northeast.longitude);
        if (ms.zoom >= 17) {
            if (lastZoomLevel <17) {
                clearExceptName();
                SelectedBasestationMarker.deSeleted();
            }
            lastZoomLevel = ms.zoom;
            Cursor cursor = BasestationManager.basestationDB.rawQuery("select CELLINDEX from gsm_cells where baidulat>? and baidulat<? and BAIDULON>? and BAIDULON<?  group by BS,LAT,LON,azimuth", new String[]{minlat, maxlat, minlng, maxlng});
            cursor.moveToFirst();
            cellCount = cursor.getCount();
            int count = Math.min(cellCount, maxShowCells);
            for (int i = 0; i < count; i++) {
                Cell cell = BasestationManager.gsmCells.get(cursor.getInt(0));
                CellMarker cellMarker = gsmCellsToShow.get(cell);
                if (cellMarker == null) {
                    if (cell.type == 1) {
                        cellMarker = new LocelleGsmMarker();
                    } else {
                        cellMarker = new CellGsmMarker();
                    }
                    cellMarker.setCell(cell);
                    gsmCellsToShow.put(cell, cellMarker);
                    synchronized (this) {
                        if (stop == true)
                            return;
                        cellMarker.showInMap(mBaiduMap);
                    }
                }
                cursor.moveToNext();
            }
            cursor.close();
            cursor = BasestationManager.basestationDB.rawQuery("select CELLINDEX from gsm_cells where baidulat>? and baidulat<? and BAIDULON>? and BAIDULON<? group by BS,LAT,LON", new String[]{minlat, maxlat, minlng, maxlng});
            cursor.moveToFirst();
            count = Math.min(cursor.getCount(), maxShowBsName);
            for (int i = 0; i < count; i++) {
                Basestation basestation = BasestationManager.gsmBSs.get(cursor.getInt(0));
                BasestationMarker bsMarker = bsNameToShow.get(basestation);
                if (bsMarker == null) {
                    bsMarker = new BasestationNameMaker();
                    bsMarker.setBasestation(basestation);
                    bsNameToShow.put(basestation, bsMarker);
                    if (stop == true)
                        return;
                    bsMarker.showInMap(mBaiduMap);
                }
                cursor.moveToNext();
            }
            cursor.close();
        } else if(ms.zoom >= 16) {
            if (lastZoomLevel >=17) {
                clearExceptName();
                SelectedCellMarker.deSeleted();
            }
            lastZoomLevel = ms.zoom;
            Cursor cursor = BasestationManager.basestationDB.rawQuery("select CELLINDEX from gsm_cells where baidulat>? and baidulat<? and BAIDULON>? and BAIDULON<? and TYPE=0 group by BS,LAT,LON", new String[]{minlat, maxlat, minlng, maxlng});
            cursor.moveToFirst();
            cellCount = cursor.getCount();
            int count = Math.min(cellCount, maxShowBsMaker);
            int jump = cellCount/count;
            for (int i = 0; i < count; i++) {
                Basestation basestation = BasestationManager.gsmBSs.get(cursor.getInt(0));
                if(cursor.getInt(0)%jump!=0)
                    continue;
                BasestationMarker bsMarker = bsMakerToShow.get(basestation);
                if (bsMarker == null) {
                    bsMarker = new BasestationGsmMarker();
                    bsMarker.setBasestation(basestation);
                    bsMakerToShow.put(basestation, bsMarker);
                    if (stop == true)
                        return;
                    bsMarker.showInMap(mBaiduMap);
                }
                BasestationMarker bsNameMarker = bsNameToShow.get(basestation);
                if (bsNameMarker == null) {
                    bsNameMarker = new BasestationNameMaker();
                    bsNameMarker.setBasestation(basestation);
                    bsNameToShow.put(basestation, bsNameMarker);
                    if (stop == true)
                        return;
                    bsNameMarker.showInMap(mBaiduMap);
                }
                cursor.moveToNext();
            }
            cursor.close();

        } else {
            if (lastZoomLevel >=17)
                clear();
            else if(lastZoomLevel >=16)
                clearName();
            lastZoomLevel = ms.zoom;
            Cursor cursor = BasestationManager.basestationDB.rawQuery("select CELLINDEX from gsm_cells where baidulat>? and baidulat<? and BAIDULON>? and BAIDULON<? and TYPE=0 group by BS,LAT,LON", new String[]{minlat, maxlat, minlng, maxlng});
            cursor.moveToFirst();
            cellCount = cursor.getCount();
            int count = Math.min(cellCount, maxShowBsMaker);
            int jump = cellCount/count;
            for (int i = 0; i < count; i=i+jump) {
                Basestation basestation = BasestationManager.gsmBSs.get(cursor.getInt(0));
                BasestationMarker bsMarker = bsMakerToShow.get(basestation);
                if (bsMarker == null) {
                    bsMarker = new BasestationGsmMarker();
                    bsMarker.setBasestation(basestation);
                    bsMakerToShow.put(basestation, bsMarker);
                        if (stop == true)
                            return;
                        bsMarker.showInMap(mBaiduMap);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
    }
}