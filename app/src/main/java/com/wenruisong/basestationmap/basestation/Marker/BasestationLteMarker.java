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
public class BasestationLteMarker extends BasestationMarker {

    private static BitmapDescriptor cell_lte_shifen = BitmapDescriptorFactory.fromResource(R.drawable.basestation_green);

    private MarkerOptions marker_lte_shifen = new MarkerOptions().zIndex(6).icon(cell_lte_shifen);

    public void showInMap(BaiduMap baiduMap) {
        mBasestation.isMakerShow = true;
        if (basestaionMarker == null) {
            basestaionMarker = (Marker) baiduMap.addOverlay(marker_lte_shifen.position(mBasestation.baiduLatLng));
        } else {
            basestaionMarker.setPosition(mBasestation.baiduLatLng);
        }
        basestaionMarker.setTitle(Integer.toString(mBasestation.basestationIndex));
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
