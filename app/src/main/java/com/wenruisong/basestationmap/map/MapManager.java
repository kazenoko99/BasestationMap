package com.wenruisong.basestationmap.map;

import android.graphics.Color;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.basestation.BasestationManager;
import com.wenruisong.basestationmap.basestation.Marker.ServiceCellMarker;
import com.wenruisong.basestationmap.basestation.Marker.MarkerManager;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.utils.Constants;
import com.wenruisong.basestationmap.utils.ResourcesUtil;
import com.wenruisong.basestationmap.view.BottomCellView;

/**
 * Created by wen on 2016/3/27.
 */
public class MapManager implements LocationHelper.RequestLocationCallBack
{
    private static MapManager instance;
    private static AMap aMap;
    public final static  int LocateTypePX =0;
    private static int mMapLocateType = LocateTypePX;

    private BottomCellView mapbottom;

    MarkerManager markerManager = MarkerManager.getInstance();
    BitmapDescriptor mCurrentMarker;
    private LocationHelper locationHelper;
    private LatLng locatedLatLng;
    protected Circle myLocCircle;//定位点的精度值外圆
    protected Marker myLocMarker;//当前定位点的marker

    public static MapManager getInstance() {
        if (instance == null)
            instance = new MapManager();
        return instance;
    }


    public void init(MapController mapController,AMap amap) {
        mCurrentMarker = null;
        aMap = amap;
        markerManager.setMap(aMap);
     //  mapController = (MapController) view.findViewById(R.id.map_controller);
        mapController.setController(aMap);
        mapbottom = mapController.mapBottomController.mBottomCellView;
//        aMap.setMyLocationConfigeration(new MyLocationConfiguration(
//                MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker));
//        aMap.setMyLocationEnabled(true);
        aMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        locationHelper = LocationHelper.getInstance();
        locationHelper.addLocationChangeListener(this);
        locationHelper.setAmap(aMap);
        initLocationMaker();

        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                markerManager.showMarkers();
            }
        });

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.transparent));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        myLocationStyle.strokeWidth(0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        ServiceCellMarker.getInstance().setMap(aMap);
    }

    public void changeMode(int mode) {
        switch (mode) {
            case 0:
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                aMap.animateCamera(CameraUpdateFactory.changeTilt(0));
                break;
            case 1:
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                break;
            case 2:
                aMap.animateCamera(CameraUpdateFactory.changeTilt(60));

                break;
            default:
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                aMap.animateCamera(CameraUpdateFactory.changeTilt(0));
                break;
        }
    }

    public void changeLocationMode(ImageView location_btn){
        switch (mMapLocateType) {
            case AMap.LOCATION_TYPE_LOCATE:
                mMapLocateType =LocateTypePX;
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        Constants.GEO_PINGXIANG, 15),1500,null);
                setMyLocMarkerIcon(R.drawable.map_location_marker);
                location_btn.setImageResource(R.drawable.map_location_locate_disabled);
                break;

            case LocateTypePX:
                aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
                mMapLocateType =  AMap.LOCATION_TYPE_MAP_ROTATE;
                setMyLocMarkerIcon(R.drawable.map_location_marker_rotate);
                location_btn.setImageResource(R.drawable.map_location_rotate);
                break;

            case AMap.LOCATION_TYPE_MAP_ROTATE:
                aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
                mMapLocateType = AMap.LOCATION_TYPE_LOCATE;
                setMyLocMarkerIcon(R.drawable.map_location_marker);
                location_btn.setImageResource(R.drawable.map_location_follow);
                break;
            default:
                break;
        }
    }
    private static float mapZoomLevel = 17.0f;

    public void zoomIn() {
        aMap.animateCamera(CameraUpdateFactory.zoomIn());
    }

    public void zoomOut() {
        aMap.animateCamera(CameraUpdateFactory.zoomOut());
    }


    public void initLocationMaker(){
            myLocMarker = aMap.addMarker(new MarkerOptions()
                    .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_location_marker)));

                    myLocCircle = aMap.addCircle(new CircleOptions()
                            .fillColor(Color.argb(38, 43, 125, 225))
                            .strokeColor(ResourcesUtil.getColor(R.color.map_location_circle_color))
                            .strokeWidth(1.0f));
            }

    public void updateLocation(AMapLocation aLocation) {

    }

    public void updateRotate(float angel){
        myLocMarker.setRotateAngle(angel);
    }


    protected void setMyLocMarkerIcon(int resourceId) {
        if (myLocMarker != null) {
            myLocMarker.setIcon(BitmapDescriptorFactory.fromResource(resourceId));
        }
    }


    @Override
    public void onLocated(AMapLocation aLocation) {
        locatedLatLng = new LatLng(aLocation.getLatitude(),aLocation.getLongitude());
        myLocMarker.setPosition(locatedLatLng);
        myLocCircle.setCenter(locatedLatLng);
        myLocCircle.setRadius(aLocation.getAccuracy());
    }

    @Override
    public void onFirstLocated(AMapLocation aMapLocation) {
       // BasestationManager.setCurrentShowCity(aMapLocation.getCity());
        BasestationManager.getInstance().initCity();
    }
}
