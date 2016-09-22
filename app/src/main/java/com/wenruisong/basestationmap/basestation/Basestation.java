package com.wenruisong.basestationmap.basestation;

import com.amap.api.maps.model.LatLng;
import com.wenruisong.basestationmap.utils.Constants;

/**
 * Created by wen on 2016/1/16.
 */
public class Basestation {
    public String bsName;
    public LatLng latLng;
    public LatLng amapLatLng;
    public String address;
    public int type;

    public int basestationIndex;
    public boolean isNameShow =false;
    public boolean isMakerShow =false;

    public int getInstanceType(){
        if(this instanceof GSMBasestation)
             return Constants.GSM;
        else if (this instanceof LTEBasestation)
             return Constants.LTE;
        else return Constants.TYPE_UNKNOWN;
    }

    @Override
    public boolean equals(Object o) {
        // return super.equals(o);
        if (o instanceof Basestation){
            if(((Basestation) o).bsName.equals(this.bsName) && ((Basestation) o).latLng.equals (this.latLng)){
                return true;
            } else {
                return false;
            }
        } else
            return false;

    }

      @Override
      public int hashCode() {
        return basestationIndex;
    }
}
