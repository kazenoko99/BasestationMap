package com.wenruisong.basestationmap.basestation.Marker;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MarkerOptions;
import com.wenruisong.basestationmap.R;

/**
 * Created by wen on 2016/3/4.
 */
public class CellLteMarker extends CellMarker {
    private static BitmapDescriptor cell_lte = BitmapDescriptorFactory.fromResource(R.drawable.cell_green);
    private MarkerOptions marker_lte = new MarkerOptions().zIndex(4).icon(cell_lte);

    public void showInMap(AMap aMap) {
        cell.isShow = true;
        if (cellMarker == null) {
            cellMarker = aMap.addMarker(marker_lte.position(cell.aMapLatLng));
            cellMarker.setRotateAngle(cell.azimuth);
            cellMarker.setObject(cell.index);
        } else {
            cellMarker.setPosition(cell.aMapLatLng);
            cellMarker.setRotateAngle(cell.azimuth);
            cellMarker.setObject(cell.index);
        }
    }
}
