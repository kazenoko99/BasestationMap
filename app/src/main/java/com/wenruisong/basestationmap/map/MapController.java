package com.wenruisong.basestationmap.map;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.model.LatLng;
import com.squareup.otto.Subscribe;
import com.wenruisong.basestationmap.MainActivity;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.SearchActivity;
import com.wenruisong.basestationmap.basestation.Cell;
import com.wenruisong.basestationmap.basestation.BasestationManager;
import com.wenruisong.basestationmap.eventbus.MapToolsEvents;
import com.wenruisong.basestationmap.eventbus.SearchResultEvents;
import com.wenruisong.basestationmap.fragment.MapFragment;
import com.wenruisong.basestationmap.helper.DirectionHelper;
import com.wenruisong.basestationmap.tools.GpsPointMarkerTool;
import com.wenruisong.basestationmap.tools.RulerTool;
import com.wenruisong.basestationmap.view.CompassView;
import com.wenruisong.basestationmap.view.GpsPointDialog;
import com.wenruisong.basestationmap.view.MapModeDialog;

/**
 * Created by wen on 2016/1/16.
 */
public class MapController extends FrameLayout implements MapModeDialog.OnMapModeChangeListner, GpsPointDialog.OnAddGpsPointListner, DirectionHelper.OnDirectionChanged,
        MapBottomController.OnCellInfoPagerListener {
    private final BasestationManager btsManager = BasestationManager.getInstance();
    private final GpsPointMarkerTool gpsPointMarkerTool = GpsPointMarkerTool.getInstance();
    private final RulerTool rulerTool = RulerTool.getInstance();
    private ImageView location_btn;
    private ImageView mapZoomOut;
    private ImageView mapZoomIn;
    private ImageView map_mode_change;
    private LinearLayout map_bottom_detail_layout;
    private LinearLayout map_top_searchview;
    private ImageView drawer_icon;
    private TextView searchHint;
    private ImageView function_btn;
    private CompassView compassView;
    private Context mContext;
    public MapBottomController mapBottomController;
    BaiduMap.OnMarkerClickListener onDefaultModeMarkerClickListener = new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            if (marker.getTitle() != null) {
                if (marker.getZIndex()==2) {
                    Cell cell = BasestationManager.gsmCells.get(Integer.parseInt(marker.getTitle()));
                    mapBottomController.onCellMarkerClick(cell);
                }
                else if (marker.getZIndex()==4) {
                    Cell cell = BasestationManager.lteCells.get(Integer.parseInt(marker.getTitle()));
                    mapBottomController.onCellMarkerClick(cell);
                }
            }
            return false;
        }
    };
    BaiduMap.OnMapClickListener onDefaultModeMapClickListener = new BaiduMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            mapBottomController.mBottomActionView.setVisibility(VISIBLE);
            mapBottomController.mDetailViewPager.setVisibility(GONE);
        }

        @Override
        public boolean onMapPoiClick(MapPoi mapPoi) {
            mapBottomController.mBottomActionView.setVisibility(VISIBLE);
            mapBottomController.mDetailViewPager.setVisibility(GONE);
            return false;
        }
    };
    private MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
    private MapFragment mMap;
    private BaiduMap mBaiduMap;
    BaiduMap.OnMarkerClickListener onRulerModeMarkerClickListener = new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            onRulerModeMapClick(marker.getPosition());
            return false;
        }
    };
    BaiduMap.OnMapClickListener onRulerModeMapClickListener = new BaiduMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            onRulerModeMapClick(latLng);
        }

        @Override
        public boolean onMapPoiClick(MapPoi mapPoi) {
            onRulerModeMapClick(mapPoi.getPosition());
            return false;
        }
    };
    private OnClickListener onDrawerIconClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ((MainActivity) mContext).openOrCloseDrawers();
        }
    };
    private OnClickListener onSearchViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, SearchActivity.class);
            mContext.startActivity(intent);
        }
    };
    private OnClickListener onCancelRulerClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            RulerTool.clearAllPoints();
            defaultMode();
        }
    };
    private OnClickListener onCancelCompassClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            compassView.setVisibility(GONE);
            defaultMode();
        }
    };
    private OnClickListener onZoomInClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mMap.zoomIn();
        }
    };
    private OnClickListener onZoomOutClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mMap.zoomOut();
        }
    };
    private OnClickListener toggleLocateModeClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mMap.toggleLocatingMode(mCurrentMode);
            switch (mCurrentMode) {
                case NORMAL:
                    mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
                    location_btn.setImageResource(R.drawable.map_location_rotate);
                    break;
                case COMPASS:
                    mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                    location_btn.setImageResource(R.drawable.map_location_follow);
                    break;
                default:
                    break;
            }
        }
    };
    private OnClickListener onMapModeClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showMapModeDialog();
        }
    };

    public MapController(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View root = LayoutInflater.from(context).inflate(R.layout.map_controller, this, true);
        MapToolsEvents.getBus().register(this);
        SearchResultEvents.getBus().register(this);
        DirectionHelper.getInstance().setDirectionChangedListener(this);
        mapBottomController = new MapBottomController(mContext);
        mapBottomController.initBottomWidget(root);
        mapBottomController.registerOnCellInfoPagerListener(this);
    }

    @Subscribe
    public void OnClickTools(MapToolsEvents.OnClickTools event) {
        switch (event.mIndex) {
            case 0:
                rulerMode();
                break;
            case 1:
                compassMode();
                break;
            case 2:
                showGpsPointDialog();
                break;
            default:
                defaultMode();
                break;
        }
    }

    @Subscribe
    public void OnCellClick(SearchResultEvents.OnCellClick event) {
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(event.cellLatLng);
        if (event.cellIndex != -1) {
            mapBottomController.onCellMarkerClick(BasestationManager.gsmCells.get(event.cellIndex));
        }
        mBaiduMap.animateMapStatus(u);
    }

    void compassMode() {
        defaultMode();
        compassView.setVisibility(VISIBLE);
        map_bottom_detail_layout.setVisibility(GONE);
        function_btn.setOnClickListener(onCancelCompassClickListener);
        function_btn.setImageResource(R.drawable.mz_btn_list_attachment_delete_normal);
        searchHint.setText("经度：    纬度");
        searchHint.setOnClickListener(null);
        mBaiduMap.removeMarkerClickListener(onDefaultModeMarkerClickListener);
        mBaiduMap.removeMarkerClickListener(onRulerModeMarkerClickListener);
    }

    void rulerMode() {
        defaultMode();
        map_bottom_detail_layout.setVisibility(GONE);
        function_btn.setOnClickListener(onCancelRulerClickListener);
        function_btn.setImageResource(R.drawable.mz_btn_list_attachment_delete_normal);
        searchHint.setText("点击地图不同区域以测量距离");
        searchHint.setOnClickListener(null);
        mBaiduMap.removeMarkerClickListener(onDefaultModeMarkerClickListener);
        mBaiduMap.setOnMapClickListener(onRulerModeMapClickListener);
        mBaiduMap.setOnMarkerClickListener(onRulerModeMarkerClickListener);
    }

    void defaultMode() {
        function_btn.setImageResource(R.drawable.toolbar_tool);
        function_btn.setOnClickListener(onCancelRulerClickListener);
        searchHint.setText("查基站、找地点、搜线路");
        searchHint.setOnClickListener(onSearchViewClickListener);
        mBaiduMap.removeMarkerClickListener(onRulerModeMarkerClickListener);
        mBaiduMap.setOnMapClickListener(onDefaultModeMapClickListener);
        mBaiduMap.setOnMarkerClickListener(onDefaultModeMarkerClickListener);
    }

    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        super.onFinishInflate();
        mapZoomIn = (ImageView) findViewById(R.id.map_zoom_in);
        mapZoomOut = (ImageView) findViewById(R.id.map_zoom_out);
        location_btn = (ImageView) findViewById(R.id.map_locate);
        compassView = (CompassView) findViewById(R.id.compass_view);
        map_mode_change = (ImageView) findViewById(R.id.map_mode);

        map_bottom_detail_layout = (LinearLayout) findViewById(R.id.map_bottom_detail_layout);
        map_top_searchview = (LinearLayout) findViewById(R.id.map_top_searchview);
        searchHint = (TextView) findViewById(R.id.search_hint);
        drawer_icon = (ImageView) findViewById(R.id.drawer_icon);
        function_btn = (ImageView) findViewById(R.id.function_btn);
        location_btn.setOnClickListener(toggleLocateModeClickListener);
        mapZoomIn.setOnClickListener(onZoomInClickListener);
        mapZoomOut.setOnClickListener(onZoomOutClickListener);
        map_mode_change.setOnClickListener(onMapModeClickListener);
        searchHint.setOnClickListener(onSearchViewClickListener);
        drawer_icon.setOnClickListener(onDrawerIconClickListener);
    }

    public void setController(MapFragment map, BaiduMap baiduMap) {
        mMap = map;
        mBaiduMap = baiduMap;
        mapBottomController.setBaiduMap(baiduMap);
        mapBottomController.registerOnCellInfoPagerListener(this);
        defaultMode();
    }

    public void onRulerModeMapClick(LatLng latLng) {
        searchHint.setText(RulerTool.addRulerPoint(mBaiduMap, latLng));
    }

    void showMapModeDialog() {
        MapModeDialog.Builder builder = new MapModeDialog.Builder(mContext);
        builder.setOnMapModeChangeListner(this);
        builder.create().show();
    }

    void showGpsPointDialog() {
        GpsPointDialog.Builder builder = new GpsPointDialog.Builder(mContext);
        builder.setOnMapModeChangeListner(this);
        builder.create().show();
    }


    @Override
    public void setMapMode(int positon) {
        mMap.changeMode(positon);
    }

    @Override
    public void addGpsPoint(String pointName, LatLng pointLatLng) {
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(pointLatLng));
        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(18);
        mBaiduMap.animateMapStatus(u);
        GpsPointMarkerTool.addMarker(mBaiduMap, pointName, pointLatLng);
    }

    @Override
    public void clearAllGpsPoint() {
        GpsPointMarkerTool.clearAllMarkers();
    }

    @Override
    public void onGetNewDirection(float direction) {
        compassView.updateDegree(direction);
    }

    @Override
    public void onCellInfoPagerChanged(LatLng latLng) {
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(latLng, 19);
        mBaiduMap.animateMapStatus(u);
    }
}
