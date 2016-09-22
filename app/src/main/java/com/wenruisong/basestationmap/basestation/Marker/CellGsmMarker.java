package com.wenruisong.basestationmap.basestation.Marker;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MarkerOptions;
import com.wenruisong.basestationmap.R;

/**
 * Created by wen on 2016/3/4.
 */
public class CellGsmMarker extends CellMarker  {

    private static BitmapDescriptor cell_gsm = BitmapDescriptorFactory.fromResource(R.drawable.cell_blue);
    private MarkerOptions marker_gsm = new MarkerOptions().zIndex(2).icon(cell_gsm);

    public void showInMap(AMap aMap) {
        cell.isShow = true;
        if (cellMarker == null) {
            cellMarker = aMap.addMarker(marker_gsm.position(cell.aMapLatLng));
            cellMarker.setRotateAngle(cell.azimuth);
            cellMarker.setObject(cell.index);
        } else {
            cellMarker.setPosition(cell.aMapLatLng);
            cellMarker.setRotateAngle(cell.azimuth);
            cellMarker.setObject(cell.index);
        }
    }

}
