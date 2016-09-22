package com.wenruisong.basestationmap.map;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
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
    private AMap aMap;
    private Context mContext;


    public CustomizedMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View root = LayoutInflater.from(context).inflate(R.layout.map_customized_view, this, true);
    }

    public AMap getMap(){
        return aMap;
    }



    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        super.onFinishInflate();
        mapZoomIn = (ImageView) findViewById(R.id.map_zoom_in);
        mapZoomOut = (ImageView) findViewById(R.id.map_zoom_out);
        location_btn = (ImageView) findViewById(R.id.map_locate);
        mMapView = (MapView)findViewById(R.id.mapview);
//        MapViewUtils.hideZoomView(mMapView);
//        MapViewUtils.hideLogo(mMapView);
        MapViewUtils.hideZoomView(mMapView);
        aMap =mMapView.getMap();
        location_btn.setOnClickListener(onLacatingClickListener);
        mapZoomIn.setOnClickListener(onZoomInClickListener);
        mapZoomOut.setOnClickListener(onZoomOutClickListener);
    }

    private float mapZoomLevel = 17.0f;
    private OnClickListener onZoomInClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            aMap.animateCamera(CameraUpdateFactory.zoomIn());
        }
    };
    private OnClickListener onZoomOutClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            aMap.animateCamera(CameraUpdateFactory.zoomOut());
        }
    };

    private OnClickListener onLacatingClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
                    location_btn.setImageResource(R.drawable.map_location_follow);
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);
    }

    public void onResume() {
        mMapView.onResume();
    }

    public void onPause() {
        mMapView.onPause();
    }

    public void onSaveInstanceState(Bundle outState) {
        mMapView.onSaveInstanceState(outState);
    }

    public void onDestroy() {
        mMapView.onDestroy();
    }
}
