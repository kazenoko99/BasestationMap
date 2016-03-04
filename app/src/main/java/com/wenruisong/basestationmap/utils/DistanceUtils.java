package com.wenruisong.basestationmap.utils;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by wen on 2016/2/16.
 */
public class DistanceUtils {
    static DecimalFormat df = new DecimalFormat("#.00");
    public static String getDistance(LatLng latLng1, LatLng latLng2)
    {

        double dis= DistanceUtil.getDistance(latLng1,latLng2);
        if (dis == -1.0D)
            return "未知";
        if(dis<1000)
            return  (int)dis+"米";
        else
            return df.format(dis/1000)+"公里";
    }

    public static  String totalDistance(  ArrayList<LatLng> list) {
        double distence=0;
        String result;
        LatLng currentLatLng,pastLatLng;
        Iterator<LatLng> iterator= list.iterator();


        pastLatLng=iterator.next();
        while(iterator.hasNext()){
            currentLatLng=iterator.next();
            distence=distence+DistanceUtil.getDistance(pastLatLng, currentLatLng);
            pastLatLng=currentLatLng;
        }

        if (distence<1000)
        {
            distence= Math.floor(distence);
            result = Integer.toString((int)distence);
            result=String.format("总距离:"+result+"米");
        }
        else {
            DecimalFormat df=new DecimalFormat(".##");
            result=df.format(distence/1000);
            result=String.format("总距离:"+result+"公里");
        }
        return result;
    }
}
