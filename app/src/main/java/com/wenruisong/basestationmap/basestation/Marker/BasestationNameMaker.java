package com.wenruisong.basestationmap.basestation.Marker;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.TextOptions;

/**
 * Created by wen on 2016/3/1.
 */
public class BasestationNameMaker extends BasestationMarker {

    public Overlay BSText;
    private TextOptions textOptions=new TextOptions().fontSize(28).fontColor(0XFF000000).zIndex(10);

    @Override
    void showInMap(BaiduMap baiduMap) {
        if( mBasestation.isNameShow)
            return;
        if (BSText == null)
            BSText=baiduMap.addOverlay(textOptions.text(mBasestation.bsName)
                    .position(mBasestation.baiduLatLng));
        mBasestation.isNameShow=true;
    }

    @Override
    public void remove() {
        if(BSText!=null) {
            BSText.remove();
            mBasestation.isNameShow =false;
        }
    }
}
