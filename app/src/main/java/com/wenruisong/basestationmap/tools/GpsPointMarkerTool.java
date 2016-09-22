package com.wenruisong.basestationmap.tools;

import android.widget.Toast;
import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.wenruisong.basestationmap.BasestationMapApplication;

import java.util.ArrayList;

/**
 * Created by wen on 2016/2/19.
 */
public class GpsPointMarkerTool {
    private static GpsPointMarkerTool instance;
    private static int maxCount = 10;
    private static int markerCounts = 0;
    private static ArrayList<GpsPointMarker> markers = new ArrayList<>();
    public static  GpsPointMarkerTool getInstance()
    {
        if (instance==null)
            instance=new GpsPointMarkerTool();
        return instance;
    }

    public static void addMarker(AMap AMap, String name, LatLng latLng)
    {
        if(markerCounts <= maxCount)
        {
            GpsPointMarker gpsPointMarker = new GpsPointMarker(name,latLng,markerCounts);
            gpsPointMarker.showInMap(AMap);
            markers.add(gpsPointMarker);
            markerCounts++;
        }
        else
        {
         Toast.makeText(BasestationMapApplication.getContext(), "最多添加10个标记，请清楚一些标记", Toast.LENGTH_SHORT).show();
        }
    }

    public static void clearAllMarkers()
    {
        if(markers!=null && markers.size()!=0)
        {
            for(int i=0;i<markers.size();i++)
            {
                markers.get(i).clearMarker();
            }
        }
        markerCounts = 0;
    }

}
