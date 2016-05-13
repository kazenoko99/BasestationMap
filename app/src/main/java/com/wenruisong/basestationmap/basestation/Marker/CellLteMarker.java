package com.wenruisong.basestationmap.basestation.Marker;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.wenruisong.basestationmap.R;

/**
 * Created by wen on 2016/3/4.
 */
public class CellLteMarker extends CellMarker {
    private static BitmapDescriptor cell_lte = BitmapDescriptorFactory.fromResource(R.drawable.cell_green);
    private MarkerOptions marker_lte = new MarkerOptions().zIndex(4).icon(cell_lte);

    public void showInMap(BaiduMap baiduMap) {
        cell.isShow = true;
        if (cellMarker == null) {
            cellMarker = (Marker) baiduMap.addOverlay(marker_lte.position(cell.baiduLatLng).rotate(cell.azimuth));
            cellMarker.setTitle(Integer.toString(cell.index));
        } else {
            cellMarker.setPosition(cell.baiduLatLng);
            cellMarker.setRotate(cell.azimuth);
            cellMarker.setTitle(Integer.toString(cell.index));
        }
    }
}
