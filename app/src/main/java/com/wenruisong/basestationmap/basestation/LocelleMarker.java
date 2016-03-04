package com.wenruisong.basestationmap.basestation;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.wenruisong.basestationmap.R;

/**
 * Created by wen on 2016/2/28.
 */
public class LocelleMarker {
    private Cell cell;
    private Cell cellLastShow;
    public Marker cellMarker;

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public void setLastCell(Cell cell) {
        this.cellLastShow = cell;
    }

    private static BitmapDescriptor cell_gsm_shifen = BitmapDescriptorFactory.fromResource(R.drawable.cell_blue_shifen);

    private MarkerOptions marker_gsm_shifen = new MarkerOptions().zIndex(5).icon(cell_gsm_shifen);




    public void showInMap(BaiduMap baiduMap) {
        cell.isShow = true;
        if (cellMarker == null) {
            cellLastShow = cell;
            cellMarker = (Marker) baiduMap.addOverlay(marker_gsm_shifen.position(cell.baiduLatLng));
            cellMarker.setTitle(Integer.toString(cell.index));
        } else {
            cellLastShow.isShow = false;
            cellMarker.setPosition(cell.baiduLatLng);
            cellMarker.setTitle(Integer.toString(cell.index));
        }
    }
}
