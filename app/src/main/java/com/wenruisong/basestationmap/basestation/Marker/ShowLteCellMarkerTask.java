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
import com.wenruisong.basestationmap.common.Settings;

/**
 * Created by wen on 2016/3/6.
 */
public class  ShowLteCellMarkerTask implements Runnable {
    private static AMap aMap;
    private final static int maxShowCells = 200;
    private final static int maxShowBsName = 100;
    private final static int maxShowBsMaker = 550;
    private static Boolean stop = false;
    public static int cellCount;
    private static CellLruCache lteCellsToShow = new CellLruCache(220);
    private static BasesationLruCache bsNameToShow = new BasesationLruCache(120);
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
        lteCellsToShow.trimToSize(0);
        bsNameToShow.trimToSize(0);
        // aMap.clear();
        stop = false;
    }

    public static synchronized void clearExceptName() {
        stop = true;
        bsMakerToShow.trimToSize(0);
        lteCellsToShow.trimToSize(0);
        // aMap.clear();
        stop = false;
    }

    public static synchronized void clearName() {
        stop = true;
        bsNameToShow.trimToSize(0);
        // aMap.clear();
        stop = false;
    }


    @Override
    public void run() {
        showLteCellMarkers();
    }

    public synchronized void showLteCellMarkers() {
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
            Cursor cursor = BasestationManager.basestationDB.rawQuery("select * from lte_cells_"+BasestationManager.getCurrentShowCity()+" where baidulat>? and baidulat<? and BAIDULON>? and BAIDULON<?  group by BS,LAT,LON,azimuth", new String[]{minlat, maxlat, minlng, maxlng});
            cursor.moveToFirst();
            cellCount = cursor.getCount();
            int count = Math.min(cellCount, maxShowCells);
            for (int i = 0; i < count; i++) {
              //  cell = BasestationManager.getInstance().lteCells.get(cursor.getInt(0));
                cell  = BasestationManager.db2LteCell(cursor);
                cellMarker = lteCellsToShow.get(cell);
                if (cellMarker == null) {
                    if (cell.type == 1) {
                        cellMarker = new LocelleLteMarker();
                    } else {
                        cellMarker = new CellLteMarker();
                    }
                    cellMarker.setCell(cell);
                    lteCellsToShow.put(cell, cellMarker);
                    synchronized (this) {
                        if (stop == true)
                            return;
                        cellMarker.showInMap(aMap);
                    }
                }
                cursor.moveToNext();
            }
            cursor.close();
            if(Settings.isShowCellName) {
                cursor = BasestationManager.basestationDB.rawQuery("select * from lte_cells_" + BasestationManager.getCurrentShowCity() + " where baidulat>? and baidulat<? and BAIDULON>? and BAIDULON<? group by BS,LAT,LON", new String[]{minlat, maxlat, minlng, maxlng});
                cursor.moveToFirst();
                count = Math.min(cursor.getCount(), maxShowBsName);
                for (int i = 0; i < count; i++) {
                    //basestation = BasestationManager.getInstance().lteBSs.get(cursor.getInt(0));
                    basestation  = BasestationManager.db2LteBs(cursor);
                    bsMarker = bsMakerToShow.get(basestation);
                    if (bsMarker == null) {
                        bsMarker = new BasestationLteMarker();
                        bsMarker.setBasestation(basestation);
                        bsMakerToShow.put(basestation, bsMarker);
                        if (stop == true)
                            return;
                        bsMarker.showInMap(aMap);
                    }
                    if(Settings.isShowCellName) {
                        bsNameMarker = bsNameToShow.get(basestation);
                        if (bsNameMarker == null) {
//                        Message message = new Message();
//                        message.arg1 = cursor.getInt(cursor.getColumnIndex("CELLINDEX"));
//                        textHandler.sendMessage(message);
                            //  basestation = BasestationManager.getInstance().lteBSs.get(msg.arg1);
//                        basestation = BasestationManager.getLteBsbyIndex( cursor.getInt(cursor.getColumnIndex("CELLINDEX")));
                            bsMarker = bsNameToShow.get(basestation);
                            if (bsMarker == null) {

//                Log.d("hash wen","hash is :"+ basestation.hashCode());
                                bsMarker = new BasestationNameMaker();
                                bsMarker.setBasestation(basestation);
                                bsNameToShow.put(basestation, bsMarker);
                                if (stop == true)
                                    return;
                                Message.obtain(textHandler,0,bsMarker).sendToTarget();
                            }

                        }
                    }
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } else if(cameraPosition.zoom >= 15) {
            if (lastZoomLevel >=16) {
                clearExceptName();
                SelectedCellMarker.deSeleted();
            }
            lastZoomLevel = cameraPosition.zoom;
            Cursor cursor = BasestationManager.basestationDB.rawQuery("select * from lte_cells_"+BasestationManager.getCurrentShowCity()+" where baidulat>? and baidulat<? and BAIDULON>? and BAIDULON<? and TYPE=0 group by BS,LAT,LON", new String[]{minlat, maxlat, minlng, maxlng});
            cursor.moveToFirst();
            cellCount = cursor.getCount();
            int count = Math.min(cellCount, maxShowBsMaker);
            int jump = cellCount/count;

            for (int i = 0; i < count; i++) {
                //basestation = BasestationManager.getInstance().lteBSs.get(cursor.getInt(0));
                basestation  = BasestationManager.db2LteBs(cursor);
                if(cursor.getInt(0)%jump!=0)
                    continue;
                bsMarker = bsMakerToShow.get(basestation);
                if (bsMarker == null) {
                    bsMarker = new BasestationLteMarker();
                    bsMarker.setBasestation(basestation);
                    bsMakerToShow.put(basestation, bsMarker);
                    if (stop == true)
                        return;
                    bsMarker.showInMap(aMap);
                }
                if(Settings.isShowCellName) {
                    bsNameMarker = bsNameToShow.get(basestation);
                    if (bsNameMarker == null) {
//                        Message message = new Message();
//                        message.arg1 = cursor.getInt(cursor.getColumnIndex("CELLINDEX"));
//                        textHandler.sendMessage(message);
                        //  basestation = BasestationManager.getInstance().lteBSs.get(msg.arg1);
//                        basestation = BasestationManager.getLteBsbyIndex( cursor.getInt(cursor.getColumnIndex("CELLINDEX")));
                        bsMarker = bsNameToShow.get(basestation);
                        if (bsMarker == null) {

//                Log.d("hash wen","hash is :"+ basestation.hashCode());
                            bsMarker = new BasestationNameMaker();
                            bsMarker.setBasestation(basestation);
                            bsNameToShow.put(basestation, bsMarker);
                            if (stop == true)
                                return;
                            Message.obtain(textHandler,0,bsMarker).sendToTarget();
                        }

                    }
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
            Cursor cursor = BasestationManager.basestationDB.rawQuery("select * from lte_cells_"+BasestationManager.getCurrentShowCity()+" where baidulat>? and baidulat<? and BAIDULON>? and BAIDULON<? and TYPE=0 group by BS,LAT,LON", new String[]{minlat, maxlat, minlng, maxlng});
            cursor.moveToFirst();
            cellCount = cursor.getCount();
            int count = Math.min(cellCount, maxShowBsMaker);
            if(count ==0)
                return;
            int jump = cellCount/count;
            for (int i = 0; i < count; i=i+jump) {
                //basestation = BasestationManager.getInstance().lteBSs.get(cursor.getInt(0));
                basestation = BasestationManager.db2LteBs(cursor);
                bsMarker = bsMakerToShow.get(basestation);
                if (bsMarker == null) {
                    bsMarker = new BasestationLteMarker();
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


    private Handler textHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ((BasestationMarker)msg.obj).showInMap(aMap);
        }
    };
}