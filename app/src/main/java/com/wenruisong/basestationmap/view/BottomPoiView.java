package com.wenruisong.basestationmap.view;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.activity.NearbyBasestationActivity;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.utils.AMapUtil;
import com.wenruisong.basestationmap.utils.DistanceUtils;

/**
 * Created by wen on 2016/3/5.
 */
public class BottomPoiView implements GeocodeSearch.OnGeocodeSearchListener {
    private TextView poiName;
    private TextView poiAddress;
    private TextView poiBasestationNear;
    private TextView poiDistance;
    private ViewGroup rootLayout;
    private Context mContext;
    private Poi mSelectedPoi;
    private  GeocodeSearch geocoderSearch;
    private RegeocodeQuery mRegeocodeQuery;
   public void initView(View root,Context context) {
       mContext = context;
       geocoderSearch = new GeocodeSearch(context);
       geocoderSearch.setOnGeocodeSearchListener(this);
       rootLayout =(ViewGroup)root.findViewById(R.id.map_bottom_poi);
       poiName = (TextView) root.findViewById(R.id.map_bottom_name);
       poiAddress = (TextView) root.findViewById(R.id.map_bottom_address);
       poiBasestationNear = (TextView) root.findViewById(R.id.map_bottom_detail);
       poiDistance = (TextView) root.findViewById(R.id.map_bottom_distance);
   }

    public void setVisibility(int visibility){
        rootLayout.setVisibility(visibility);
    }

    public int getVisibility(){
        return rootLayout.getVisibility();
    }

    public void setDate(final Poi poi){
        mSelectedPoi = poi;
        poiName.setText(poi.getName());
        poiAddress.setText("");
        if(LocationHelper.location!=null){
            LatLng mLatlng =  new LatLng(LocationHelper.location.getLatitude(),LocationHelper.location.getLongitude());
            poiDistance.setText(DistanceUtils.getDistance(mLatlng, poi.getCoordinate()));
        }

        poiBasestationNear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NearbyBasestationActivity.class);
                intent.putExtra("POI",poi);
                mContext.startActivity(intent);
            }
        });
        LatLonPoint latLonPoint  = AMapUtil.convertToLatLonPoint(poi.getCoordinate());
        mRegeocodeQuery = new RegeocodeQuery(latLonPoint, 20, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        mRegeocodeQuery.setLatLonType(GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(mRegeocodeQuery);// 设置同步逆地理编码请求

    }





    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        StringBuilder sb = new StringBuilder();
        RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
        sb.append(regeocodeAddress.getDistrict()).append(regeocodeAddress.getNeighborhood())
                .append(regeocodeAddress.getStreetNumber().getStreet()).append(regeocodeAddress.getStreetNumber().getNumber());
        poiAddress.setText(sb.toString());
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}
