package com.wenruisong.basestationmap.basestation.Marker;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.Cell;

/**
 * Created by wen on 2016/3/1.
 */
public class SelectedBasestationMarker {

    private static MarkerOptions marker_select = new MarkerOptions().zIndex(7);
    private static BitmapDescriptor basestationSelected = BitmapDescriptorFactory.fromResource(R.drawable.basestation_red);
    public static Marker selectedMarker;

    public static void setSelected(BaiduMap baiduMap, Cell cell) {

            deSeleted();
        SelectedCellMarker.deSeleted();

       selectedMarker = (Marker) baiduMap.addOverlay(marker_select.icon(basestationSelected).position(cell.baiduLatLng));

        selectedMarker.setTitle(Integer.toString(cell.index));
        selectedMarker.setToTop();
    }

    public static void deSeleted() {
        if(selectedMarker!=null)selectedMarker.remove();
    }
}
