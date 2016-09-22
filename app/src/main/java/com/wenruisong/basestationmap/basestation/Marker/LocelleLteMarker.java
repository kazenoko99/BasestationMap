package com.wenruisong.basestationmap.basestation.Marker;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MarkerOptions;
import com.wenruisong.basestationmap.R;

/**
 * Created by wen on 2016/2/28.
 */
public class LocelleLteMarker extends CellMarker{

    private static BitmapDescriptor cell_lte_shifen = BitmapDescriptorFactory.fromResource(R.drawable.cell_shifen_green);
    private MarkerOptions marker_gsm_shifen = new MarkerOptions().zIndex(4).icon(cell_lte_shifen).anchor(0.5f, 0.5f);

    public void showInMap(AMap aMap) {
        cell.isShow = true;
        if (cellMarker == null) {
            cellMarker = aMap.addMarker(marker_gsm_shifen.position(cell.aMapLatLng));
            cellMarker.setObject(cell.index);
        } else {
            cellMarker.setPosition(cell.aMapLatLng);
            cellMarker.setObject(cell.index);
        }
    }
}
