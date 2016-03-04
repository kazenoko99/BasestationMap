package com.wenruisong.basestationmap.basestation;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.TextOptions;

/**
 * Created by wen on 2016/3/1.
 */
public class BsNameMaker {
    private Basestation bs;
    public void setBs(Basestation basestation) {
        this.bs = basestation;
    }


    public Overlay BSText;
    private TextOptions textOptions=new TextOptions().fontSize(28).fontColor(0XFF000000).zIndex(10);


    public void showBsNameInMap(BaiduMap mBaiduMap) {
        if(!bs.isNameShow)
            BSText=mBaiduMap.addOverlay(textOptions.text(bs.bsName)
                    .position(bs.baiduLatLng));
        bs.isNameShow=true;
    }

    public void remove() {
        if(BSText!=null) {
            BSText.remove();
            bs.isNameShow =false;
        }
    }

}
