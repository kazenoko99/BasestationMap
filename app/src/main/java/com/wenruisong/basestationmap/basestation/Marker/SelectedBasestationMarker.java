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
public class SelectedBasestationMarker {

    private static MarkerOptions marker_select = new MarkerOptions().zIndex(7);
    private static BitmapDescriptor basestationSelected = BitmapDescriptorFactory.fromResource(R.drawable.basestation_red);
    public static Marker selectedMarker;

    public static void setSelected(AMap aMap, Cell cell) {

            deSeleted();
        SelectedCellMarker.deSeleted();

       selectedMarker =  aMap.addMarker(marker_select.icon(basestationSelected).position(cell.aMapLatLng));

        selectedMarker.setTitle(Integer.toString(cell.index));
        selectedMarker.setToTop();
    }

    public static void deSeleted() {
        if(selectedMarker!=null)selectedMarker.remove();
    }
}
