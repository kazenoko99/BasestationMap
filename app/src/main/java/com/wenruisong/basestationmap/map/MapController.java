package com.wenruisong.basestationmap.map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.Poi;
import com.squareup.otto.Subscribe;
import com.wenruisong.basestationmap.MainActivity;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.SearchActivity;
import com.wenruisong.basestationmap.activity.AboutAppActivity;
import com.wenruisong.basestationmap.basestation.BasestationManager;
import com.wenruisong.basestationmap.basestation.Cell;
import com.wenruisong.basestationmap.basestation.Marker.MarkerManager;
import com.wenruisong.basestationmap.eventbus.MapToolsEvents;
import com.wenruisong.basestationmap.eventbus.SearchResultEvents;
import com.wenruisong.basestationmap.helper.DirectionHelper;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.tools.GpsPointMarkerTool;
import com.wenruisong.basestationmap.tools.RulerTool;
import com.wenruisong.basestationmap.utils.Constants;
import com.wenruisong.basestationmap.utils.ImageUtils;
import com.wenruisong.basestationmap.view.CompassView;
import com.wenruisong.basestationmap.view.GpsPointDialog;
import com.wenruisong.basestationmap.view.MapModePopupWindow;
import com.wenruisong.basestationmap.view.RelativePopupWindow;

/**
 * Created by wen on 2016/1/16.
 */
public class MapController implements MapModePopupWindow.OnMapModeChangeListner, GpsPointDialog.OnAddGpsPointListner, DirectionHelper.OnDirectionChanged,
        MapBottomController.OnCellInfoPagerListener , AMap.OnMyLocationChangeListener {
    private final BasestationManager btsManager = BasestationManager.getInstance();
    private final GpsPointMarkerTool gpsPointMarkerTool = GpsPointMarkerTool.getInstance();
    private final RulerTool rulerTool = RulerTool.getInstance();
    private ImageView location_btn;
    private ImageView mapZoomOut;
    private ImageView mapZoomIn;
    private ImageView mapModeChange;
    private LinearLayout map_bottom_detail_layout;
    private LinearLayout map_top_searchview;
    private ImageView drawer_icon;
    private TextView searchHint;
    private ImageView function_btn;
    private CompassView compassView;
    private Context mContext;
    public MapBottomController mapBottomController;

    private boolean isNeedStore = true;



    AMap.OnMarkerClickListener onDefaultModeMarkerClickListener = new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            Cell cell;
            if (marker.getObject()
                    != null) {
                //use zindex of maker to distingish net type
                switch ((int)marker.getZIndex()){
                    case 2:
                        cell = BasestationManager.getGsmCellbyIndex((int)(marker.getObject()));
                        mapBottomController.onCellMarkerClick(cell);
                        break;
                    case 4:
                         cell = BasestationManager.getLteCellbyIndex((int)(marker.getObject()));
                        mapBottomController.onCellMarkerClick(cell);
                        break;
                    case 5:
                        cell = BasestationManager.getGsmCellbyIndex((int)(marker.getObject()));
                        mapBottomController.onBasestationMarkerClick(cell);
                        break;
                    case 6:
                        cell = BasestationManager.getLteCellbyIndex((int)(marker.getObject()));
                        mapBottomController.onBasestationMarkerClick(cell);
                        break;
                }


            }
            return false;
        }
    };
    AMap.OnMapClickListener onDefaultModeMapClickListener = new AMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            isNeedStore =true;
            mapStateHandler.sendEmptyMessageDelayed(0,100);
        }
    };

    AMap.OnPOIClickListener onDefaultModePoiClickListener = new AMap.OnPOIClickListener() {

        @Override
        public void onPOIClick(Poi poi) {
            isNeedStore =false;
            if( mapBottomController.mBottomActionView.getVisibility()==View.GONE) {
                mapBottomController.mBottomActionView.setVisibility(View.VISIBLE);
                mapBottomController.mDetailViewPager.setVisibility(View.GONE);
            }
            mapBottomController.showPoiInfo(poi);
        }
    };
    
    private MapManager mMapManager;
    private AMap aMap;

    AMap.OnMarkerClickListener onRulerModeMarkerClickListener = new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            onRulerModeMapClick(marker.getPosition());
            return false;
        }
    };
    AMap.OnMapClickListener onRulerModeMapClickListener = new AMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            onRulerModeMapClick(latLng);
        }
    };

    AMap.OnPOIClickListener onRulerModePoiClickListener = new AMap.OnPOIClickListener() {

        @Override
        public void onPOIClick(Poi poi) {
            onRulerModeMapClick(poi.getCoordinate());
        }
    };


    private View.OnClickListener onDrawerIconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((MainActivity) mContext).openOrCloseDrawers();
        }
    };
    private View.OnClickListener onSearchViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, SearchActivity.class);
            mContext.startActivity(intent);
        }
    };
    private View.OnClickListener onCancelRulerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RulerTool.clearAllPoints();
            defaultMode();
        }
    };

    private View.OnClickListener onFunctionBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mContext.startActivity(new Intent(mContext, AboutAppActivity.class));
        }
    };

    private View.OnClickListener onCancelCompassClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            compassView.setVisibility(View.GONE);
            defaultMode();
        }
    };
    private View.OnClickListener onZoomInClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mMapManager.zoomIn();
        }
    };
    private View.OnClickListener onZoomOutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mMapManager.zoomOut();
        }
    };
    private View.OnClickListener toggleLocateModeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mMapManager.changeLocationMode(location_btn);
        }
    };
    private View.OnClickListener onMapModeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showMapModeDialog(v);
        }
    };

    public MapController(Context context) {
        mContext = context;
       }

    public void initWidget(View root){
        mapBottomController = new MapBottomController(mContext);
        mapBottomController.initBottomWidget(root);
        mapBottomController.registerOnCellInfoPagerListener(this);
        mapZoomIn = (ImageView) root.findViewById(R.id.map_zoom_in);
        mapZoomOut = (ImageView) root.findViewById(R.id.map_zoom_out);
        location_btn = (ImageView) root.findViewById(R.id.map_locate);
        compassView = (CompassView) root.findViewById(R.id.compass_view);
        mapModeChange = (ImageView) root.findViewById(R.id.map_mode);

        map_bottom_detail_layout = (LinearLayout) root.findViewById(R.id.map_bottom_detail_layout);
        map_top_searchview = (LinearLayout) root.findViewById(R.id.map_top_searchview);
        searchHint = (TextView) root.findViewById(R.id.search_hint);
        drawer_icon = (ImageView) root.findViewById(R.id.drawer_icon);
        function_btn = (ImageView) root.findViewById(R.id.function_btn);
        location_btn.setOnClickListener(toggleLocateModeClickListener);
        mapZoomIn.setOnClickListener(onZoomInClickListener);
        mapZoomOut.setOnClickListener(onZoomOutClickListener);
        mapModeChange.setOnClickListener(onMapModeClickListener);
        searchHint.setOnClickListener(onSearchViewClickListener);
        drawer_icon.setOnClickListener(onDrawerIconClickListener);

        MapToolsEvents.getBus().register(this);
        SearchResultEvents.getBus().register(this);
        DirectionHelper.getInstance().setDirectionChangedListener(this);

        MapModePopupWindow.setOnMapModeChangeListner(this);
    }

    @Subscribe
    public void OnClickTools(MapToolsEvents.OnClickTools event) {
        switch (event.mIndex) {
            case Constants.TOOL_RULER:
                rulerMode();
                break;
            case Constants.TOOL_COMPASS:
                compassMode();
                break;
            case Constants.TOOL_GPS_POINT:
                showGpsPointDialog();
                break;
            default:
                defaultMode();
                break;
        }
    }

    @Subscribe
    public void OnCellClick(SearchResultEvents.OnCellClick event) {
        switch (event.netTypeFlag) {
            case Constants.GSM:
                MarkerManager.getInstance().setMarkerType(MarkerManager.MarkerType.GSM);
                if (event.cellIndex != -1) {
                    mapBottomController.onCellMarkerClick(BasestationManager.getGsmCellbyIndex(event.cellIndex));
                }
                break;

            case Constants.LTE:
                MarkerManager.getInstance().setMarkerType(MarkerManager.MarkerType.LTE);
                if (event.cellIndex != -1) {
                    mapBottomController.onCellMarkerClick(BasestationManager.getLteCellbyIndex(event.cellIndex));
                }
                break;
        }

        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(event.cellLatLng,18));
    }

    @Subscribe
    public void OnPoiClick(SearchResultEvents.OnPoiClick event) {
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(event.position, 19));
    }

    void compassMode() {
        defaultMode();
        compassView.setVisibility(View.VISIBLE);
        function_btn.setOnClickListener(onCancelCompassClickListener);
        function_btn.setImageResource(R.drawable.mz_btn_list_attachment_delete_normal);
        searchHint.setText("经度：    纬度");
        searchHint.setOnClickListener(null);
    }

    void rulerMode() {
        defaultMode();
        function_btn.setOnClickListener(onCancelRulerClickListener);
        function_btn.setImageResource(R.drawable.mz_btn_list_attachment_delete_normal);
        searchHint.setText("点击地图不同区域以测量距离");
        searchHint.setOnClickListener(null);
        aMap.setOnMapClickListener(onRulerModeMapClickListener);
        aMap.setOnMarkerClickListener(onRulerModeMarkerClickListener);
        aMap.setOnPOIClickListener(onRulerModePoiClickListener);
    }

    void defaultMode() {
        function_btn.setImageResource(R.drawable.toolbar_tool);
        function_btn.setOnClickListener(onFunctionBtnClickListener);
        searchHint.setText("查基站、找地点、搜线路");
        searchHint.setOnClickListener(onSearchViewClickListener);
        aMap.setOnMapClickListener(onDefaultModeMapClickListener);
        aMap.setOnMarkerClickListener(onDefaultModeMarkerClickListener);
        aMap.setOnPOIClickListener(onDefaultModePoiClickListener);
    }

 

    public void setController(AMap AMap) {
        mMapManager = MapManager.getInstance();
        aMap = AMap;
        LocationHelper.getInstance().setAmap(aMap);
        mapBottomController.setAMap(aMap);
        mapBottomController.registerOnCellInfoPagerListener(this);
        aMap.setOnMyLocationChangeListener(this);
        defaultMode();

        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setScaleControlsEnabled(false);
        uiSettings.setCompassEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setAllGesturesEnabled(true);
    }

    public void onRulerModeMapClick(LatLng latLng) {
        searchHint.setText(RulerTool.addRulerPoint(aMap, latLng));
    }

    void showMapModeDialog(View view) {
//        MapModeDialog.Builder builder = new MapModeDialog.Builder(mContext);
//        builder.setLocation(mapModeChange.getTop(),mapModeChange.getRight());
//        builder.setOnMapModeChangeListner(this);
//        builder.create().show();

      new MapModePopupWindow(mapModeChange.getContext()).showOnAnchor(mapModeChange, RelativePopupWindow.VerticalPosition.BELOW, RelativePopupWindow.HorizontalPosition.ALIGN_RIGHT);

    }

    void showGpsPointDialog() {
        GpsPointDialog.Builder builder = new GpsPointDialog.Builder(mContext);
        builder.setOnMapModeChangeListner(this);
        builder.create().show();
    }

    private Handler mapStateHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(isNeedStore)
                mapBottomController.restore();
        }
    };

    @Override
    public void setMapMode(int positon) {
        mMapManager.changeMode(positon);
    }

    @Override
    public void showMapText(boolean flag) {
        aMap.showMapText(flag);
    }

    @Override
    public void showTraffic(boolean flag) {
        aMap.setTrafficEnabled(flag);
    }

    @Override
    public void screenShoot() {
         aMap.getMapScreenShot(new AMap.OnMapScreenShotListener(){

             @Override
             public void onMapScreenShot(Bitmap bitmap) {

             }

             @Override
             public void onMapScreenShot(Bitmap bitmap, int arg1) {
                 ImageUtils.screenShot(bitmap,arg1);
             }
         });
    }

    @Override
    public void addGpsPoint(String pointName, LatLng pointLatLng) {
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pointLatLng, 18));
        GpsPointMarkerTool.addMarker(aMap, pointName, pointLatLng);
    }

    @Override
    public void clearAllGpsPoint() {
        GpsPointMarkerTool.clearAllMarkers();
    }

    @Override
    public void onGetNewDirection(float direction) {
        compassView.updateDegree(direction);
        mMapManager.updateRotate(direction);
    }

    @Override
    public void onCellInfoPagerChanged(LatLng latLng) {
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
    }


    @Override
    public void onMyLocationChange(Location location) {
    }
}
