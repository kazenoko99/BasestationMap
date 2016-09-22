package com.wenruisong.basestationmap.basestation.Marker;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.Cell;

/**
 * Created by wen on 2016/3/1.
 */
public class SelectedCellMarker {
    private static BitmapDescriptor cell_select = BitmapDescriptorFactory.fromResource(R.drawable.cell_red);
    private static MarkerOptions marker_select = new MarkerOptions().zIndex(5);
    private static BitmapDescriptor cell_select_shifen = BitmapDescriptorFactory.fromResource(R.drawable.cell_shifen_red);
    public static Marker selectedMarker;
    public static Marker selectedLocelleMarker;
    public static void setSelected(AMap aMap, Cell cell) {
        SelectedBasestationMarker.deSeleted();
            deSeleted();
        if(cell.type == 1) {
            selectedLocelleMarker = aMap.addMarker(marker_select.icon(cell_select_shifen).anchor(0.5f, 0.5f).position(cell.aMapLatLng));
            selectedLocelleMarker.setRotateAngle(0);
            selectedLocelleMarker.setTitle(Integer.toString(cell.index));
            selectedLocelleMarker.setToTop();
        }
        else {
            selectedMarker =  aMap.addMarker(marker_select.icon(cell_select).position(cell.aMapLatLng));
            selectedMarker.setRotateAngle(cell.azimuth);
            selectedMarker.setTitle(Integer.toString(cell.index));
            selectedMarker.setToTop();
        }

    }

    public static void deSeleted() {
        if(selectedMarker!=null)
        selectedMarker.remove();

        if(selectedLocelleMarker!=null)
            selectedLocelleMarker.remove();
    }
}
