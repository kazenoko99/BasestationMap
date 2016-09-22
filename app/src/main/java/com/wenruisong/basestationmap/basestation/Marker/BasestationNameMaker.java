package com.wenruisong.basestationmap.basestation.Marker;


import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;

/**
 * Created by wen on 2016/3/1.
 */
public class BasestationNameMaker extends BasestationMarker {
    private int mTextSize = 34;
    public Text BSText;
    private String basestationName;
    private TextOptions textOptions=new TextOptions().fontSize(mTextSize).fontColor(0XFF000000).backgroundColor(0X00000000).zIndex(10);
    public void setTextSize(int size){
        textOptions.fontSize(size);
    }
    @Override
    public void showInMap(AMap aMap) {
        if( mBasestation.isNameShow)
            return;
        if (BSText == null) {
            if(mBasestation.bsName.length()>15){
                basestationName = mBasestation.bsName.substring(0,15)+"...";
            } else {
                basestationName = mBasestation.bsName;
            }
            BSText = aMap.addText(textOptions.text(basestationName)
                    .position(mBasestation.amapLatLng));
        }
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
