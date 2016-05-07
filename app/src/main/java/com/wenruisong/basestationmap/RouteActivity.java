package com.wenruisong.basestationmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.wenruisong.basestationmap.adapter.SearchHistoryAdapter;
import com.wenruisong.basestationmap.database.SearchHistorySqliteHelper;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.map.overlay.DrivingRouteOverlay;
import com.wenruisong.basestationmap.map.overlay.OverlayManager;
import com.wenruisong.basestationmap.map.overlay.TransitRouteOverlay;
import com.wenruisong.basestationmap.map.overlay.WalkingRouteOverlay;
import com.wenruisong.basestationmap.model.RouteHistoryItem;
import com.wenruisong.basestationmap.utils.Constants;
import com.wenruisong.basestationmap.view.LocationSeletorLayout;

import java.util.ArrayList;

public class RouteActivity extends AppCompatActivity implements View.OnClickListener, OnGetRoutePlanResultListener,SearchHistoryAdapter.RemoveItemClickListener {
    private ImageView btn_back;
    private static RouteType routeType;
    private BaiduMap mBaidumap;
    RouteLine route = null;
    OverlayManager routeOverlay = null;
    boolean useDefaultIcon = true;
    public static final int CODE_FIND_LOCATION_ADDRESS = 0x0010;

    public static final int CODE_FIND_LOCATION_START = 0x0020;
    public static final int CODE_FIND_LOCATION_END = 0x0030;
    public static final int CODE_EDIT_LOCATION_ADDRESS = 0x0040;

    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用

    @Override
    public void removeItem(int position) {
        if(routeHistory!=null && routeHistory.size()!=0){
            searchHistorySqliteHelper.delRouteResult(((RouteHistoryItem)routeHistory.get(position)).id);
            routeHistory.remove(position);
            mSearchHistoryAdapter.notifyDataSetChanged();

        }
    }


    public enum RouteType {WALK,BUS,DRIVE}
    private String[] mTitle  = new String[]{ "驾车","公交","步行"};
    private RadioGroup routeRg;
    private LocationSeletorLayout mLocationSeletorLayout;
    private TextView mStartPoint;
    private TextView mEndPoint;
    private LatLng mStartLatLng;
    private LatLng mEndLatLng;
    private FloatingActionButton mFab;
    private RadioGroup mRadioGroup;
    private TextView searchTextView;
    private FrameLayout mExchangeImg;
    private MapView mMapView;
    private String mCityCode;
    private RecyclerView mSearchRecyclerView;
    private SearchHistorySqliteHelper searchHistorySqliteHelper;
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private ArrayList routeHistory;

    public void setEndPoint(String end, boolean isCurrent) {
        if(mEndPoint != null)
            mEndPoint.setText(end);
        mLocationSeletorLayout.updateEndIcon(isCurrent);
    }

    public void setStartPoint(String start, boolean isCurrent) {
        if(mStartPoint != null)
            mStartPoint.setText(start);
        mLocationSeletorLayout.updateStartIcon(isCurrent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        mLocationSeletorLayout = (LocationSeletorLayout) findViewById(R.id.location_layout);
        mExchangeImg = (FrameLayout) findViewById(R.id.route_exchange);
        mMapView = (MapView) findViewById(R.id.route_map);
        mBaidumap = mMapView.getMap();
        mFab = (FloatingActionButton)findViewById(R.id.btnFloatingAction);
        mFab.setOnClickListener(this);
        mStartPoint = (TextView) findViewById(R.id.searchStartPoint);
        mEndPoint = (TextView) findViewById(R.id.searchEndPoint);
        mRadioGroup = (RadioGroup) findViewById(R.id.route_rg);
        mRadioGroup.check(R.id.route_drive);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                getRoute();
            }
        });
        searchTextView = (TextView) findViewById(R.id.btn_goSearch);
        searchTextView.setOnClickListener(this);

        Bundle bundle = getIntent().getBundleExtra(Constants.ROUTE_BUNDLE);
        if(bundle!=null) {
            String startLocation = bundle.getString(Constants.ROUTE_FROM_NAME);
            if (!TextUtils.isEmpty(startLocation)) {
                setStartPoint(startLocation, false);
                mStartLatLng = new LatLng(bundle.getDouble(Constants.ROUTE_FROM_LAT, 0),
                        bundle.getDouble(Constants.ROUTE_FROM_LNG, 0));
            } else {
                BDLocation location =  LocationHelper.getInstance().location;
                mStartLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                setStartPoint(location.getAddrStr(),true);
            }

            String endLocation = bundle.getString(Constants.ROUTE_TARGET_NAME);
            if (!TextUtils.isEmpty(endLocation)) {
                setEndPoint(endLocation, false);
                mEndPoint.setText(endLocation);
                mEndLatLng = new LatLng(bundle.getDouble(Constants.ROUTE_TARGET_LAT, 0),
                        bundle.getDouble(Constants.ROUTE_TARGET_LNG, 0));
            }
        } else {
            BDLocation location =  LocationHelper.getInstance().location;
            mStartLatLng = new LatLng(location.getLatitude(),location.getLongitude());
            setStartPoint(location.getAddrStr(),true);
        }

//        mCityCode = bundle.getString(Constants.LOCATION_CITY);

        mExchangeImg.setOnClickListener(this);
        mStartPoint.setOnClickListener(this);
        mEndPoint.setOnClickListener(this);

        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
//        mStartPoint.setText(startLocation);
//        mEndPoint.setText(endLocation);
        routeRg = (RadioGroup) findViewById(R.id.route_rg);
        routeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.route_bus:
                        routeType = RouteType.BUS;
                        break;
                    case R.id.route_drive:
                        routeType = RouteType.DRIVE;
                        break;
                    case R.id.route_walk:
                        routeType = RouteType.WALK;
                        break;
                }
            }
        });
        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);

        searchHistorySqliteHelper = new SearchHistorySqliteHelper(this);
        routeHistory = searchHistorySqliteHelper.queryRouteResult();
        mSearchRecyclerView = (RecyclerView)findViewById(R.id.search_history);
        mSearchHistoryAdapter = new SearchHistoryAdapter(this,this);
        mSearchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchRecyclerView.setAdapter(mSearchHistoryAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.route_exchange:
                if(TextUtils.isEmpty(mStartPoint.getText()) && TextUtils.isEmpty(mEndPoint.getText())) {
                    return;
                }
                 changeStartAndEnd();

                break;
            case R.id.searchStartPoint: {
                 startActivityToFindLocation(RouteActivity.CODE_FIND_LOCATION_START);
            }
            break;
            case R.id.searchEndPoint: {
                startActivityToFindLocation(RouteActivity.CODE_FIND_LOCATION_END);
            }
            break;
            case R.id.btnFloatingAction :
                if(mStartLatLng!=null&mStartLatLng!=null) {
                    mSearchRecyclerView.setVisibility(View.GONE);
                    getRoute();
                }
                break;
            case R.id.btn_back:
                finish();
                break;

            default:
                break;
        }
    }

    private void startActivityToFindLocation(int requestCode){
        Intent intent = new Intent(RouteActivity.this, SearchActivity.class);
        String action = null;
        switch (requestCode){
            case RouteActivity.CODE_FIND_LOCATION_ADDRESS:
                action = Constants.ACTION_MAP_SEARCH_A_ADDRESS;
                break;
            case RouteActivity.CODE_EDIT_LOCATION_ADDRESS:
                action = Constants.ACTION_MAP_EDIT_A_ADDRESS;
                break;
            default:
                action = Constants.ACTION_MAP_SEARCH_A_ADDRESS;
                break;
        }
        intent.setAction(action);
        intent.putExtra(SearchActivity.TYPE, SearchActivity.PicPlace);
        intent.putExtra(Constants.ACTIVITY_PASS_SEARCH_CITY, mCityCode);
        this.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RouteActivity.CODE_FIND_LOCATION_START:
                    Bundle startBundle = data.getExtras();
                    setStartPoint(startBundle.getString("NAME"), false);
                    mStartLatLng = new LatLng(startBundle.getDouble("LAT", 0), startBundle.getDouble("LNG", 0));

                    break;
                case RouteActivity.CODE_FIND_LOCATION_END:
                    Bundle endBundle = data.getExtras();
                    setEndPoint(endBundle.getString("NAME"), false);
                    mEndLatLng = new LatLng(endBundle.getDouble("LAT", 0), endBundle.getDouble("LNG", 0));
                    break;
            }
       // }
    }

    private void changeStartAndEnd(){
        LatLng temp = mStartLatLng;
        mStartLatLng = mEndLatLng;
        mEndLatLng = temp;

        String tempText = mStartPoint.getText().toString();
        mStartPoint.setText(mEndPoint.getText());
        mEndPoint.setText(tempText);
    }

    private void getRoute(){
        PlanNode stNode = PlanNode.withLocation(mStartLatLng);
        PlanNode enNode = PlanNode.withLocation(mEndLatLng);
        switch(mRadioGroup.getCheckedRadioButtonId()){
            case R.id.route_drive:
                mSearch.drivingSearch((new DrivingRoutePlanOption())
                        .from(stNode).to(enNode));
                break;
            case R.id.route_bus:
                mSearch.transitSearch((new TransitRoutePlanOption())
                        .from(stNode).to(enNode).city(LocationHelper.city));
                break;
            case R.id.route_walk:
                mSearch.walkingSearch((new WalkingRoutePlanOption())
                        .from(stNode).to(enNode));
                break;
        }
    }


    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RouteActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {

            route = result.getRouteLines().get(0);
            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaidumap);
            mBaidumap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RouteActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            route = result.getRouteLines().get(0);
            TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaidumap);
            mBaidumap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RouteActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            route = result.getRouteLines().get(0);
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
            routeOverlay = overlay;
            mBaidumap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }


    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

}
