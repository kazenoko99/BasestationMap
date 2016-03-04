package com.wenruisong.basestationmap.tools;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.wenruisong.basestationmap.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wen on 2016/2/19.
 */
public class GpsPointMarker {
    private String markerName;
    private LatLng latLng;
    private boolean isShow;
    private int index = 1;
    private Marker gpsPointMarker;
    private MarkerOptions marker;
    private Overlay markerText;
    private TextOptions textOptions=new TextOptions().fontSize(34).bgColor(0XFF000000).fontColor(0XFFFFFFFF).zIndex(12);
    GpsPointMarker(String markerName, LatLng latLng,int index)
    {
        this.markerName = markerName;
        this.latLng = latLng;
        this.index = index;
    }
    private static BitmapDescriptor marker1 = BitmapDescriptorFactory.fromResource(R.drawable.map_hotspot_1);
    private static BitmapDescriptor marker2 = BitmapDescriptorFactory.fromResource(R.drawable.map_hotspot_2);
    private static BitmapDescriptor marker3 = BitmapDescriptorFactory.fromResource(R.drawable.map_hotspot_3);
    private static BitmapDescriptor marker4 = BitmapDescriptorFactory.fromResource(R.drawable.map_hotspot_4);
    private static BitmapDescriptor marker5 = BitmapDescriptorFactory.fromResource(R.drawable.map_hotspot_5);
    private static BitmapDescriptor marker6 = BitmapDescriptorFactory.fromResource(R.drawable.map_hotspot_6);
    private static BitmapDescriptor marker7 = BitmapDescriptorFactory.fromResource(R.drawable.map_hotspot_7);
    private static BitmapDescriptor marker8 = BitmapDescriptorFactory.fromResource(R.drawable.map_hotspot_8);
    private static BitmapDescriptor marker9 = BitmapDescriptorFactory.fromResource(R.drawable.map_hotspot_9);
    private static BitmapDescriptor marker10 = BitmapDescriptorFactory.fromResource(R.drawable.map_hotspot_10);
    private static List<BitmapDescriptor> markers = Arrays.asList(marker1,marker2,marker3,marker4,marker5,marker6,marker7,marker8,marker9,marker10);

    public void showInMap(BaiduMap baiduMap) {
        if(baiduMap ==null)
            return;

        marker=new MarkerOptions().zIndex(11).icon(markers.get(this.index));
        if(this.isShow==false)
        {
            gpsPointMarker= (Marker) baiduMap.addOverlay(marker.position(this.latLng));
          if(!markerName.isEmpty()) {
              markerText = baiduMap.addOverlay(textOptions.text(markerName)
                      .position(this.latLng));
          }
          else
              markerText=baiduMap.addOverlay(textOptions.text("未命名")
                      .position(this.latLng));
            this.isShow=true;
        }
    }

    public void clearMarker() {
        if(this.isShow==true)
        {
            gpsPointMarker.remove();
            markerText.remove();
            this.isShow=false;
        }
    }
}
