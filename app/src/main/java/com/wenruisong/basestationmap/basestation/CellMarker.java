package com.wenruisong.basestationmap.basestation;

import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.wenruisong.basestationmap.BasestationMapApplication;
import com.wenruisong.basestationmap.R;

/**
 * Created by wen on 2016/2/28.
 */
public class CellMarker {

    private Cell cell;
    private Cell cellLastShow;
    public Marker cellMarker;

    private static BitmapDescriptor cell_gsm = BitmapDescriptorFactory.fromResource(R.drawable.cell_blue);
    private MarkerOptions marker_gsm = new MarkerOptions().zIndex(5).icon(cell_gsm);

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public void setLastCell(Cell cell) {
        this.cellLastShow = cell;
    }

    public void showInMap(BaiduMap baiduMap) {
        cell.isShow = true;
        if (cellMarker == null) {
            cellLastShow = cell;
            cellMarker = (Marker) baiduMap.addOverlay(marker_gsm.position(cell.baiduLatLng).rotate(cell.azimuth));
            cellMarker.setTitle(Integer.toString(cell.index));
        } else {
            cellLastShow.isShow = false;
            cellMarker.setPosition(cell.baiduLatLng);
            cellMarker.setRotate(cell.azimuth);
            cellMarker.setTitle(Integer.toString(cell.index));
        }
    }

}
