package com.wenruisong.basestationmap.tools;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.wenruisong.basestationmap.utils.DistanceUtils;

import java.util.ArrayList;

/**
 * Created by wen on 2016/2/19.
 */
public class RulerTool {
    private static RulerTool instance;
    private static Overlay rulerLine;
    private static ArrayList<RulerPointMarker> rulerPoints = new ArrayList<RulerPointMarker>();
    private static ArrayList<Overlay> rulerLines = new ArrayList<Overlay>();
    private static ArrayList<LatLng> rulerLagLngs = new ArrayList<LatLng>();
    private static PolylineOptions polylineOptions = new PolylineOptions().width(10)
            .color(0xAAFF0000).zIndex(9);
    private static OverlayOptions rulerPolyline;
    public static RulerTool getInstance()
    {
        if (instance==null)
            instance=new RulerTool();
        return instance;
    }

    public static String addRulerPoint(BaiduMap mBaiduMap, LatLng point)
    {
        RulerPointMarker rulerPointMarker= new RulerPointMarker(point);
        rulerLagLngs.add(point);
        rulerPoints.add(rulerPointMarker);
        rulerPointMarker.addPointInMap(mBaiduMap);
        if(rulerLagLngs.size()>1)
        {
            rulerPolyline = polylineOptions.points(rulerLagLngs);
            rulerLine = mBaiduMap.addOverlay(rulerPolyline);
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
        for (Overlay line: rulerLines) {
            line.remove();
        }
        rulerPoints.clear();
        rulerLagLngs.clear();
        rulerLines.clear();
    }
}
