package com.wenruisong.basestationmap.basestation.Marker;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.wenruisong.basestationmap.R;

/**
 * Created by wen on 2016/2/28.
 */
public class LocelleLteMarker extends CellMarker{

    private static BitmapDescriptor cell_lte_shifen = BitmapDescriptorFactory.fromResource(R.drawable.cell_shifen_green);
    private MarkerOptions marker_gsm_shifen = new MarkerOptions().zIndex(4).icon(cell_lte_shifen);

    public void showInMap(BaiduMap baiduMap) {
        cell.isShow = true;
        if (cellMarker == null) {
            cellMarker = (Marker) baiduMap.addOverlay(marker_gsm_shifen.position(cell.baiduLatLng));
            cellMarker.setTitle(Integer.toString(cell.index));
        } else {
            cellMarker.setPosition(cell.baiduLatLng);
            cellMarker.setTitle(Integer.toString(cell.index));
        }
    }
}
