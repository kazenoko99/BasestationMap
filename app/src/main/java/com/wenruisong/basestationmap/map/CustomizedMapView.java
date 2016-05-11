package com.wenruisong.basestationmap.map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.utils.MapViewUtils;

/**
 * Created by wen on 2016/5/11.
 */
public class CustomizedMapView extends FrameLayout {

    private ImageView location_btn;
    private ImageView mapZoomOut;
    private ImageView mapZoomIn;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Context mContext;


    public CustomizedMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View root = LayoutInflater.from(context).inflate(R.layout.map_customized_view, this, true);
    }

    public BaiduMap getMap(){
        return mBaiduMap;
    }



    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        super.onFinishInflate();
        mapZoomIn = (ImageView) findViewById(R.id.map_zoom_in);
        mapZoomOut = (ImageView) findViewById(R.id.map_zoom_out);
        location_btn = (ImageView) findViewById(R.id.map_locate);
        mMapView = (MapView)findViewById(R.id.mapview);
        MapViewUtils.hideZoomView(mMapView);
        MapViewUtils.hideLogo(mMapView);
        mBaiduMap=mMapView.getMap();
        location_btn.setOnClickListener(onLacatingClickListener);
        mapZoomIn.setOnClickListener(onZoomInClickListener);
        mapZoomOut.setOnClickListener(onZoomOutClickListener);
    }

    private float mapZoomLevel = 17.0f;
    private OnClickListener onZoomInClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mapZoomLevel = mBaiduMap.getMapStatus().zoom;
            mapZoomLevel++;
            if (mapZoomLevel >= 20)
                mapZoomLevel = 20;
            MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(mapZoomLevel);
            mBaiduMap.animateMapStatus(u);
        }
    };
    private OnClickListener onZoomOutClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mapZoomLevel = mBaiduMap.getMapStatus().zoom;
            mapZoomLevel--;
            if (mapZoomLevel <= 3)
                mapZoomLevel = 3;
            MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(mapZoomLevel);
            mBaiduMap.animateMapStatus(u);
        }
    };

    private OnClickListener onLacatingClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
                    location_btn.setImageResource(R.drawable.map_location_follow);
        }
    };
}
