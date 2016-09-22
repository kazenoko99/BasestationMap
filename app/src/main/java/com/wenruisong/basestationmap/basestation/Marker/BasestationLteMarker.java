package com.wenruisong.basestationmap.basestation.Marker;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MarkerOptions;
import com.wenruisong.basestationmap.R;

/**
 * Created by wen on 2016/2/28.
 */
public class BasestationLteMarker extends BasestationMarker {

    private static BitmapDescriptor cell_lte_shifen = BitmapDescriptorFactory.fromResource(R.drawable.basestation_green);

    private MarkerOptions marker_lte_shifen = new MarkerOptions().zIndex(6).icon(cell_lte_shifen).anchor(0.5f, 0.5f);

    @Override
    void setTextSize(int size) {

    }

    public void showInMap(AMap aMap) {
        mBasestation.isMakerShow = true;
        if (basestaionMarker == null) {
            basestaionMarker = aMap.addMarker(marker_lte_shifen.position(mBasestation.amapLatLng));
        } else {
            basestaionMarker.setPosition(mBasestation.amapLatLng);
        }
        basestaionMarker.setObject(mBasestation.basestationIndex);
    }

    @Override
    void remove() {
            if(basestaionMarker!=null) {
                mBasestation.isMakerShow = false;
                basestaionMarker.remove();
                basestaionMarker = null;
            }
    }
}
