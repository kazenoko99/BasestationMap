package com.wenruisong.basestationmap.basestation;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.wenruisong.basestationmap.R;

/**
 * Created by wen on 2016/3/1.
 */
public class SelectedCellMarker {
    private static BitmapDescriptor cell_select = BitmapDescriptorFactory.fromResource(R.drawable.cell_red);
    private static MarkerOptions marker_select = new MarkerOptions().zIndex(5);
    private static BitmapDescriptor cell_select_shifen = BitmapDescriptorFactory.fromResource(R.drawable.cell_shifen_red);
    public static Marker selectedMarker;

    public static void setSelected(BaiduMap baiduMap, Cell cell) {
        if(selectedMarker!=null)
            deSeleted();
        if(cell.type == 1) {
            selectedMarker = (Marker) baiduMap.addOverlay(marker_select.icon(cell_select_shifen).position(cell.baiduLatLng).rotate(0));
        }
        else {
            selectedMarker = (Marker) baiduMap.addOverlay(marker_select.icon(cell_select).position(cell.baiduLatLng).rotate(cell.azimuth));
        }
        selectedMarker.setTitle(Integer.toString(cell.index));
        selectedMarker.setToTop();
    }

    public static void deSeleted() {
        selectedMarker.remove();
    }
}
