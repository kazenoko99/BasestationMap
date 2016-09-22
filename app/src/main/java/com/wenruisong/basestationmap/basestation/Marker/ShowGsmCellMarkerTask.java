package com.wenruisong.basestationmap.basestation.Marker;

import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.VisibleRegion;
import com.wenruisong.basestationmap.basestation.Basestation;
import com.wenruisong.basestationmap.basestation.BasestationManager;
import com.wenruisong.basestationmap.basestation.Cell;

/**
 * Created by wen on 2016/3/6.
 */
public class ShowGsmCellMarkerTask implements Runnable {
        private static AMap aMap;
    private final static int maxShowCells = 200;
    private final static int maxShowBsName = 150;
    private final static int maxShowBsMaker = 550;
    private static Boolean stop = false;
    public static int cellCount;
    private static CellLruCache gsmCellsToShow = new CellLruCache(220);
    private static BasesationLruCache bsNameToShow = new BasesationLruCache(160);
    private static BasesationLruCache bsMakerToShow = new BasesationLruCache(600);
    private static float lastZoomLevel = 19;

    public static void setMap(AMap map) {
        aMap = map;
    }

    public static void start() {
        stop = false;
    }

    public static synchronized void clear() {
        stop = true;
        bsMakerToShow.trimToSize(0);
        gsmCellsToShow.trimToSize(0);
        bsNameToShow.trimToSize(0);
       // aMap.clear();
        stop = false;
    }

    public   synchronized void clearExceptName() {
        stop = true;
        bsMakerToShow.trimToSize(0);
        gsmCellsToShow.trimToSize(0);
        // aMap.clear();
        stop = false;
    }

    public static synchronized void clearName() {
        stop = true;
        bsNameToShow.trimToSize(0);
        stop = false;
    }


    @Override
    public void run() {
        showGsmCellMarkers();
    }

    public void showGsmCellMarkers() {
   if(TextUtils.isEmpty(BasestationManager.getCurrentShowCity()))
       return;

        VisibleRegion visibleRegion = aMap.getProjection().getVisibleRegion(); // 获取可视区域、
        CameraPosition cameraPosition = aMap.getCameraPosition();
        if(cameraPosition == null)
            return;
        LatLngBounds latLngBounds = visibleRegion.latLngBounds;// 获取可视区域的Bounds
        String minlat = Double.toString(latLngBounds.southwest.latitude);
        String maxlat = Double.toString(latLngBounds.northeast.latitude);
        String minlng = Double.toString(latLngBounds.southwest.longitude);
        String maxlng = Double.toString(latLngBounds.northeast.longitude);
        Basestation basestation;
        BasestationMarker bsMarker;
        BasestationMarker bsNameMarker;
        Cell cell;
        CellMarker cellMarker;
        if (cameraPosition.zoom >= 16) {
            if (lastZoomLevel <16) {
                clearExceptName();
                SelectedBasestationMarker.deSeleted();
            }
            lastZoomLevel = cameraPosition.zoom;
            Cursor cursor = BasestationManager.basestationDB.rawQuery("select * from gsm_cells_"+BasestationManager.getCurrentShowCity()+" where baidulat>? and baidulat<? and BAIDULON>? and BAIDULON<?  group by BS,LAT,LON,azimuth", new String[]{minlat, maxlat, minlng, maxlng});
            cursor.moveToFirst();
            cellCount = cursor.getCount();
            int count = Math.min(cellCount, maxShowCells);
            for (int i = 0; i < count; i++) {
              //  cell = BasestationManager.getInstance().gsmCells.get(cursor.getInt(0));
                cell = BasestationManager.db2GsmCell(cursor);
                cellMarker = gsmCellsToShow.get(cell);
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
                        cellMarker.showInMap(aMap);
                    }
                }
                cursor.moveToNext();
            }
            cursor.close();
            cursor = BasestationManager.basestationDB.rawQuery("select CELLINDEX from gsm_cells_"+BasestationManager.getCurrentShowCity()+" where baidulat>? and baidulat<? and BAIDULON>? and BAIDULON<? group by BS,LAT,LON", new String[]{minlat, maxlat, minlng, maxlng});
            cursor.moveToFirst();
            count = Math.min(cursor.getCount(), maxShowBsName);
            for (int i = 0; i < count; i++) {
                Message message = new Message();
                message.arg1 = cursor.getInt(0);
                textHandler.sendMessage(message);
                cursor.moveToNext();
            }
            cursor.close();
        } else if(cameraPosition.zoom >= 15) {
            if (lastZoomLevel >=16) {
                clear();
                SelectedCellMarker.deSeleted();
            }
            lastZoomLevel = cameraPosition.zoom;
            Cursor cursor = BasestationManager.basestationDB.rawQuery("select * from gsm_cells_"+BasestationManager.getCurrentShowCity()+" where baidulat>? and baidulat<? and BAIDULON>? and BAIDULON<? and TYPE=0 group by BS,LAT,LON", new String[]{minlat, maxlat, minlng, maxlng});
            cursor.moveToFirst();
            cellCount = cursor.getCount();
            int count = Math.min(cellCount, maxShowBsMaker);
            if(count ==0)
                return;
            int jump = cellCount/count;

            for (int i = 0; i < count; i++) {
                 basestation = BasestationManager.db2GsmBs(cursor);
                if(basestation.basestationIndex%jump!=0)
                    continue;
                bsMarker = bsMakerToShow.get(basestation);
                if (bsMarker == null) {
                    bsMarker = new BasestationGsmMarker();
                    bsMarker.setBasestation(basestation);
                    bsMakerToShow.put(basestation, bsMarker);
                    if (stop == true)
                        return;
                    bsMarker.showInMap(aMap);
                }
                bsNameMarker = bsNameToShow.get(basestation);
                if (bsNameMarker == null) {
                   Message message = new Message();
                    message.arg1 = basestation.basestationIndex;
                    textHandler.sendMessage(message);
                }
                cursor.moveToNext();
            }
            cursor.close();

        } else {
            if (lastZoomLevel >=15)
                clear();
            else if(lastZoomLevel >=15)
                clearName();
            lastZoomLevel = cameraPosition.zoom;
            Cursor cursor = BasestationManager.basestationDB.rawQuery("select * from gsm_cells_"+BasestationManager.getCurrentShowCity()+ " where baidulat>? and baidulat<? and BAIDULON>? and BAIDULON<? and TYPE=0 group by BS,LAT,LON", new String[]{minlat, maxlat, minlng, maxlng});
            cursor.moveToFirst();
            cellCount = cursor.getCount();
            int count = Math.min(cellCount, maxShowBsMaker);
            if ( count ==0)
                return;
            int jump = cellCount/count;
            for (int i = 0; i < count; i=i+jump) {
                // basestation = BasestationManager.getInstance().gsmBSs.get(cursor.getInt(0));
                basestation = BasestationManager.db2GsmBs(cursor);
                bsMarker = bsMakerToShow.get(basestation);
                if (bsMarker == null) {
                    bsMarker = new BasestationGsmMarker();
                    bsMarker.setBasestation(basestation);
                    bsMakerToShow.put(basestation, bsMarker);
                        if (stop == true)
                            return;
                        bsMarker.showInMap(aMap);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    private Basestation basestation;
    private BasestationMarker bsMarker;
    private Handler textHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //basestation = BasestationManager.getInstance().gsmBSs.get(msg.arg1);
            basestation = BasestationManager.getGsmBsbyIndex(msg.arg1);
             bsMarker = bsNameToShow.get(basestation);
            if (bsMarker == null) {
                bsMarker = new BasestationNameMaker();
                if(basestation.type ==0){
                    bsMarker.setTextSize(35);
                } else {
                    bsMarker.setTextSize(30);
                }
                bsMarker.setBasestation(basestation);
                bsNameToShow.put(basestation, bsMarker);
                if (stop == true)
                    return;
                   bsMarker.showInMap(aMap);
            }
        }
    };
}