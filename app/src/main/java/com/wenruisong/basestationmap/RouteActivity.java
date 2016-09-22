package com.wenruisong.basestationmap;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.maps.overlay.WalkRouteOverlay;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.wenruisong.basestationmap.adapter.RouteHistoryAdapter;
import com.wenruisong.basestationmap.database.SearchHistorySqliteHelper;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.map.CustomizedMapView;
import com.wenruisong.basestationmap.model.RouteHistoryItem;
import com.wenruisong.basestationmap.utils.AMapUtil;
import com.wenruisong.basestationmap.utils.Constants;
import com.wenruisong.basestationmap.utils.ToastUtil;
import com.wenruisong.basestationmap.view.LocationSeletorLayout;

import java.util.ArrayList;

public class RouteActivity extends AppCompatActivity implements View.OnClickListener, RouteSearch.OnRouteSearchListener,RouteHistoryAdapter.RemoveItemClickListener {
    private ImageView btn_back;
    private static RouteType routeType = RouteType.DRIVE;
    private AMap aMap;
    private RouteSearch mSearch;
    private DriveRouteResult mDriveRouteResult;
    private BusRouteResult mBusRouteResult;
    private WalkRouteResult mWalkRouteResult;
    boolean useDefaultIcon = true;
    public static final int CODE_FIND_LOCATION_ADDRESS = 0x0010;

    public static final int CODE_FIND_LOCATION_START = 0x0020;
    public static final int CODE_FIND_LOCATION_END = 0x0030;
    public static final int CODE_EDIT_LOCATION_ADDRESS = 0x0040;


    @Override
    public void removeItem(int position) {
        if(routeHistory!=null && routeHistory.size()!=0){
            searchHistorySqliteHelper.delRouteResult(((RouteHistoryItem)routeHistory.get(position)).id);
            routeHistory.remove(position);
            mRouteHistoryAdapter.notifyDataSetChanged();

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
//    private TextView mHistoryTextView;
    private FrameLayout mExchangeImg;
    private CustomizedMapView mCustomizedMapView;



    private String mCityCode;
    private boolean isMapShow = false;
    private String mCity;
    private int mSearchMode;
    private RecyclerView mSearchRecyclerView;
    private SearchHistorySqliteHelper searchHistorySqliteHelper;
    private RouteHistoryAdapter mRouteHistoryAdapter;
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
        mCustomizedMapView = (CustomizedMapView) findViewById(R.id.route_map);
        mCustomizedMapView.onCreate(savedInstanceState);
        mCustomizedMapView.setVisibility(View.GONE);
        aMap = mCustomizedMapView.getMap();
        mFab = (FloatingActionButton)findViewById(R.id.btnFloatingAction);
        mFab.setOnClickListener(this);
        mStartPoint = (TextView) findViewById(R.id.searchStartPoint);
        mEndPoint = (TextView) findViewById(R.id.searchEndPoint);
        mRadioGroup = (RadioGroup) findViewById(R.id.route_rg);
        mRadioGroup.check(R.id.route_drive);
//        mHistoryTextView = (TextView) findViewById(R.id.btn_history);
//        mHistoryTextView.setOnClickListener(this);
        mCity = LocationHelper.getInstance().location.getCity();
        Bundle bundle = getIntent().getBundleExtra(Constants.ROUTE_BUNDLE);
        if(bundle!=null) {
            String startLocation = bundle.getString(Constants.ROUTE_FROM_NAME);
            if (!TextUtils.isEmpty(startLocation)) {
                setStartPoint(startLocation, false);
                mStartLatLng = new LatLng(bundle.getDouble(Constants.ROUTE_FROM_LAT, 0),
                        bundle.getDouble(Constants.ROUTE_FROM_LNG, 0));
            } else {
                AMapLocation location =  LocationHelper.getInstance().location;
                mStartLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                setStartPoint(location.getAddress(),true);
            }

            String endLocation = bundle.getString(Constants.ROUTE_TARGET_NAME);
            if (!TextUtils.isEmpty(endLocation)) {
                setEndPoint(endLocation, false);
                mEndPoint.setText(endLocation);
                mEndLatLng = new LatLng(bundle.getDouble(Constants.ROUTE_TARGET_LAT, 0),
                        bundle.getDouble(Constants.ROUTE_TARGET_LNG, 0));
            }
        } else {
            AMapLocation location =  LocationHelper.getInstance().location;
            mStartLatLng = new LatLng(location.getLatitude(),location.getLongitude());
            setStartPoint(location.getAddress(),true);
        }

//        mCityCode = bundle.getString(Constants.LOCATION_CITY);

        mExchangeImg.setOnClickListener(this);
        mStartPoint.setOnClickListener(this);
        mEndPoint.setOnClickListener(this);

        mSearch = new RouteSearch(this);
        mSearch.setRouteSearchListener(this);
//        mStartPoint.setText(startLocation);
//        mEndPoint.setText(endLocation);
        routeRg = (RadioGroup) findViewById(R.id.route_rg);
        routeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.route_bus:
                        routeType = RouteType.BUS;
                        mSearchMode = RouteSearch.BusDefault;
                        break;
                    case R.id.route_drive:
                        routeType = RouteType.DRIVE;
                        mSearchMode = RouteSearch.DrivingDefault;
                        break;
                    case R.id.route_walk:
                        routeType = RouteType.WALK;
                        mSearchMode = RouteSearch.WalkDefault;
                        break;
                }
            }
        });
        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);

        searchHistorySqliteHelper = new SearchHistorySqliteHelper(this);
        routeHistory = searchHistorySqliteHelper.queryRouteResult();
        if(routeHistory == null){
            routeHistory = new ArrayList();
        }
        mSearchRecyclerView = (RecyclerView)findViewById(R.id.search_history);
        mRouteHistoryAdapter = new RouteHistoryAdapter(this,this);
        mRouteHistoryAdapter.setDates(routeHistory);
        mSearchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchRecyclerView.setAdapter(mRouteHistoryAdapter);
    }


    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult() {
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(AMapUtil.convertToLatLonPoint(mStartLatLng) ,AMapUtil.convertToLatLonPoint(mEndLatLng) );

        if (routeType == RouteType.BUS) {// 公交路径规划
            RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo, mSearchMode,
                    mCity, 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
            mSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
        } else if (routeType == RouteType.DRIVE) {// 驾车路径规划
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mSearchMode, null,
                    null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        } else if (routeType == RouteType.WALK) {// 步行路径规划
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, mSearchMode);
            mSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        }
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
                if(!isMapShow) {
                    if (mStartLatLng != null & mEndLatLng != null) {
//                    mSearchRecyclerView.setVisibility(View.GONE);
                        RouteHistoryItem routeHistoryItem = new RouteHistoryItem();
                        routeHistoryItem.mStartName = mStartPoint.getText().toString();
                        routeHistoryItem.mEndName = mEndPoint.getText().toString();
                        routeHistoryItem.time = Long.toString(System.currentTimeMillis());
                        routeHistoryItem.mStartLatLng = mStartLatLng;
                        routeHistoryItem.mEndLatLng = mEndLatLng;
                        searchHistorySqliteHelper.insertRouteResult(routeHistoryItem);
                        routeHistory.add(0, routeHistoryItem);
                        mRouteHistoryAdapter.notifyDataSetChanged();
                        searchRouteResult();
                        isMapShow = true;
                        mFab.setImageResource(R.drawable.mz_ic_tab_back_normal_dark);
//                    mSearchRecyclerView.setVisibility(View.GONE);
//                    mCustomizedMapView.setVisibility(View.VISIBLE);
                    } else if (mStartLatLng == null) {
                        Toast.makeText(RouteActivity.this, "请设置起点位置", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RouteActivity.this, "请设置终点位置", Toast.LENGTH_SHORT).show();
                    }

                } else{
                    showReveal(mFab,false);
                    isMapShow = false;
                }
                break;
            case R.id.btn_back:
                finish();
                break;
//            case R.id.btn_history:
//                mHistoryTextView.setVisibility(View.GONE);
////                mSearchRecyclerView.setVisibility(View.VISIBLE);
//
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
        intent.putExtra(Constants.TYPE, Constants.RoutePicPlace);
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


    @Override
    public void onResume() {
        Log.i("sys", "mf onResume");
        super.onResume();
        mCustomizedMapView.onResume();
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onPause() {
        Log.i("sys", "mf onPause");
        super.onPause();
        mCustomizedMapView.onPause();
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i("sys", "mf onSaveInstanceState");
        super.onSaveInstanceState(outState);
        mCustomizedMapView.onSaveInstanceState(outState);
    }



//
//    @Override
//    public void onGetWalkingRouteResult(WalkingRouteResult result) {
//        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
//            Toast.makeText(RouteActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
//        }
//        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
//            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
//            // result.getSuggestAddrInfo()
//            return;
//        }
//        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
//
//            route = result.getRouteLines().get(0);
//            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaidumap);
//            mBaidumap.setOnMarkerClickListener(overlay);
//            routeOverlay = overlay;
//            overlay.setData(result.getRouteLines().get(0));
//            overlay.addToMap();
//            overlay.zoomToSpan();
//        }
//    }
//
//    @Override
//    public void onGetTransitRouteResult(TransitRouteResult result) {
//        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
//            Toast.makeText(RouteActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
//        }
//        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
//            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
//            // result.getSuggestAddrInfo()
//            return;
//        }
//        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
//            route = result.getRouteLines().get(0);
//            TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaidumap);
//            mBaidumap.setOnMarkerClickListener(overlay);
//            routeOverlay = overlay;
//            overlay.setData(result.getRouteLines().get(0));
//            overlay.addToMap();
//            overlay.zoomToSpan();
//        }
//    }
//
//    @Override
//    public void onGetDrivingRouteResult(DrivingRouteResult result) {
//        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
//            Toast.makeText(RouteActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
//        }
//        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
//            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
//            // result.getSuggestAddrInfo()
//            return;
//        }
//        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
//            route = result.getRouteLines().get(0);
//            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
//            routeOverlay = overlay;
//            mBaidumap.setOnMarkerClickListener(overlay);
//            overlay.setData(result.getRouteLines().get(0));
//            overlay.addToMap();
//            overlay.zoomToSpan();
//        }
//    }
//
//    // 定制RouteOverly
//    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {
//
//        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
//            super(baiduMap);
//        }
//
//        @Override
//        public BitmapDescriptor getStartMarker() {
//            if (useDefaultIcon) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
//            }
//            return null;
//        }
//
//        @Override
//        public BitmapDescriptor getTerminalMarker() {
//            if (useDefaultIcon) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
//            }
//            return null;
//        }
//    }
//
//    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {
//
//        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
//            super(baiduMap);
//        }
//
//        @Override
//        public BitmapDescriptor getStartMarker() {
//            if (useDefaultIcon) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
//            }
//            return null;
//        }
//
//        @Override
//        public BitmapDescriptor getTerminalMarker() {
//            if (useDefaultIcon) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
//            }
//            return null;
//        }
//    }
//
//    private class MyTransitRouteOverlay extends TransitRouteOverlay {
//
//        public MyTransitRouteOverlay(BaiduMap baiduMap) {
//            super(baiduMap);
//        }
//
//        @Override
//        public BitmapDescriptor getStartMarker() {
//            if (useDefaultIcon) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
//            }
//            return null;
//        }
//
//        @Override
//        public BitmapDescriptor getTerminalMarker() {
//            if (useDefaultIcon) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
//            }
//            return null;
//        }
//    }

    @Override
    public void onBusRouteSearched(BusRouteResult result, int errorCode) {
       // mBottomLayout.setVisibility(View.GONE);
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mBusRouteResult = result;
//                    BusResultListAdapter mBusResultListAdapter = new BusResultListAdapter(mContext, mBusRouteResult);
//                    mBusResultList.setAdapter(mBusResultListAdapter);

                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(this, R.string.no_result);
                }
            } else {
                ToastUtil.show(this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            this, aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos());
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    Toast.makeText(RouteActivity.this,"map loaded",Toast.LENGTH_SHORT).show();
                    showReveal(mFab,true);
//                    mBottomLayout.setVisibility(View.VISIBLE);
//                    int dis = (int) drivePath.getDistance();
//                    int dur = (int) drivePath.getDuration();
//                    String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
//                    mRotueTimeDes.setText(des);
//                    mRouteDetailDes.setVisibility(View.VISIBLE);
//                    int taxiCost = (int) mDriveRouteResult.getTaxiCost();
//                    mRouteDetailDes.setText("打车约"+taxiCost+"元");
//                    mBottomLayout.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(mContext,
//                                    DriveRouteDetailActivity.class);
//                            intent.putExtra("drive_path", drivePath);
//                            intent.putExtra("drive_result",
//                                    mDriveRouteResult);
//                            startActivity(intent);
//                        }
//                    });
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(this, R.string.no_result);
                }

            } else {
                ToastUtil.show(this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }


    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mWalkRouteResult = result;
                    final WalkPath walkPath = mWalkRouteResult.getPaths()
                            .get(0);
                    WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                            this, aMap, walkPath,
                            mWalkRouteResult.getStartPos(),
                            mWalkRouteResult.getTargetPos());
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();
                   // mBottomLayout.setVisibility(View.VISIBLE);
                    int dis = (int) walkPath.getDistance();
                    int dur = (int) walkPath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
//                    mRotueTimeDes.setText(des);
//                    mRouteDetailDes.setVisibility(View.GONE);
//                    mBottomLayout.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(mContext,
//                                    WalkRouteDetailActivity.class);
//                            intent.putExtra("walk_path", walkPath);
//                            intent.putExtra("walk_result",
//                                    mWalkRouteResult);
//                            startActivity(intent);
//                        }
//                    });

                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(this, R.string.no_result);
                }

            } else {
                ToastUtil.show(this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showReveal(@NonNull final View anchor , final boolean isShow) {
        final View contentView = mCustomizedMapView;
        contentView.post(new Runnable() {
            @Override
            public void run() {
                final int[] myLocation = new int[2];
                final int[] anchorLocation = new int[2];
                contentView.getLocationOnScreen(myLocation);
                anchor.getLocationOnScreen(anchorLocation);
                final int cx = anchorLocation[0] - myLocation[0] + anchor.getWidth()/2;
                final int cy = anchorLocation[1] - myLocation[1] + anchor.getHeight()/2;

                contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                final int dx = Math.max(cx, contentView.getMeasuredWidth() - cx);
                final int dy = Math.max(cy, contentView.getMeasuredHeight() - cy);
                final float finalRadius = (float) Math.hypot(dx, dy);
                Animator animator;
                if(isShow) {
                    mCustomizedMapView.setVisibility(View.VISIBLE);
                    animator = ViewAnimationUtils.createCircularReveal(contentView, cx, cy, 0f, finalRadius);
                }else{
                    animator = ViewAnimationUtils.createCircularReveal(contentView, cx, cy, finalRadius,0f );
                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mCustomizedMapView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                }
                animator.setDuration(500);

                animator.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCustomizedMapView.onDestroy();
    }

}
