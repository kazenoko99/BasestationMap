package com.wenruisong.basestationmap.tools;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.wenruisong.basestationmap.utils.DistanceUtils;

import java.util.ArrayList;

/**
 * Created by wen on 2016/2/19.
 */
public class RulerTool {
    private static RulerTool instance;
    private static Polyline rulerLine;
    private static ArrayList<RulerPointMarker> rulerPoints = new ArrayList<RulerPointMarker>();
    private static ArrayList<Polyline> rulerLines = new ArrayList<Polyline>();
    private static ArrayList<LatLng> rulerLagLngs = new ArrayList<LatLng>();
    private static PolylineOptions polylineOptions = new PolylineOptions().width(10)
            .color(0xAAFF0000).zIndex(9);
    private static PolylineOptions rulerPolyline;
    public static RulerTool getInstance()
    {
        if (instance==null)
            instance=new RulerTool();
        return instance;
    }

    public static String addRulerPoint(AMap mAMap, LatLng point)
    {
        RulerPointMarker rulerPointMarker= new RulerPointMarker(point);
        rulerLagLngs.add(point);
        rulerPoints.add(rulerPointMarker);
        rulerPointMarker.addPointInMap(mAMap);
        if(rulerLagLngs.size()>1)
        {
            rulerPolyline = polylineOptions.addAll(rulerLagLngs);
            rulerLine = mAMap.addPolyline(rulerPolyline);
            rulerLines.add(rulerLine);
            return DistanceUtils.totalDistance(rulerLagLngs);
        }
        return "请触摸下一个测距点";
    }

    public static void clearAllPoints()
    {
        for (RulerPointMarker rulerMarker: rulerPoints) {
            rulerMarker.clearPoint();
        }
        for (Polyline line: rulerLines) {
            line.remove();
        }
        rulerPoints.clear();
        rulerLagLngs.clear();
        rulerLines.clear();
    }
}
