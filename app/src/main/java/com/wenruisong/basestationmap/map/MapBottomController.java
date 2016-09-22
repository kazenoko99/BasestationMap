package com.wenruisong.basestationmap.map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.RouteActivity;
import com.wenruisong.basestationmap.adapter.CellInfoPagerAdapter;
import com.wenruisong.basestationmap.basestation.BasestationManager;
import com.wenruisong.basestationmap.basestation.Cell;
import com.wenruisong.basestationmap.basestation.Marker.SelectedBasestationMarker;
import com.wenruisong.basestationmap.basestation.Marker.SelectedCellMarker;
import com.wenruisong.basestationmap.helper.LocationHelper;
import com.wenruisong.basestationmap.navi.NaviActivity;
import com.wenruisong.basestationmap.utils.Constants;
import com.wenruisong.basestationmap.utils.Logs;
import com.wenruisong.basestationmap.utils.ResourcesUtil;
import com.wenruisong.basestationmap.view.BottomCellView;
import com.wenruisong.basestationmap.view.BottomPoiView;
import com.wenruisong.basestationmap.view.WrapContentHeightViewPager;

import java.util.ArrayList;

public class MapBottomController  {
    public WrapContentHeightViewPager mDetailViewPager;
   // private LinearLayout mBottomDetailView;
    private View mBtmSeparatorLine;
    private LinearLayout mBottomLocActionView;
    private TextView mBottomLocNameView;
    private TextView mBottomLocAddrView;
    private TextView mBottomLocDistView;
    private TextView mBottomDetailIcon;
    private TextView map_bottom_basestation;
    private TextView map_bottom_route;
    private TextView map_bottom_navi;
    public BottomCellView mBottomCellView;
    private BottomPoiView mBottomPoiView;

    OnCellInfoPagerListener onCellInfoPagerChanged;
    public LinearLayout mBottomActionView;
    private CellInfoPagerAdapter cellInfoPagerAdapter;
    private Context mContext;
    private AMap aMap;
    private ArrayList<Cell> cells = new ArrayList<>();

    private int mBtmDetailTwoLineHeight;
    private int mBtmDetailOneLineHeight;

    public MapBottomController(Context context) {
        this.mContext = context;
        mBottomCellView = new BottomCellView();
        mBottomPoiView = new BottomPoiView();
    }

    public void setAMap(AMap aMap) {
        this.aMap = aMap;
    }

    public void initBottomWidget(View root) {
        mBtmDetailOneLineHeight = ResourcesUtil.getResources()
                .getDimensionPixelSize(R.dimen.map_bottom_detail_one_line_height);
        mBtmDetailTwoLineHeight = ResourcesUtil.getResources()
                .getDimensionPixelSize(R.dimen.map_bottom_detail_two_line_height);

        mBottomActionView = (LinearLayout)root.findViewById(R.id.map_bottom_actionview);
        map_bottom_basestation = (TextView) root.findViewById(R.id.map_bottom_basestation);
        map_bottom_basestation.setOnClickListener(onBasestaionBtnClickListener);
        map_bottom_route = (TextView) root.findViewById(R.id.map_bottom_route);
        map_bottom_navi = (TextView) root.findViewById(R.id.map_bottom_navi);
        map_bottom_route.setOnClickListener(onRouteBtnClickListener);
        map_bottom_navi.setOnClickListener(onNaviBtnClickListener);
        mBottomCellView.initView(root,mContext);
        mBottomPoiView.initView(root,mContext);
        mBottomLocActionView =(LinearLayout) root.findViewById(R.id.map_bottom_act_layout);
        mBottomLocAddrView = (TextView) root.findViewById(R.id.map_bottom_address);
        mBottomLocDistView = (TextView) root.findViewById(R.id.map_bottom_distance);
        mBottomDetailIcon = (TextView) root.findViewById(R.id.map_bottom_detail);
        mDetailViewPager = (WrapContentHeightViewPager) root.findViewById(R.id.map_bottom_pager);

        mDetailViewPager.setOffscreenPageLimit(3);
        mDetailViewPager.setPageMargin(
                ResourcesUtil.getDimensionPixelSize(R.dimen.map_bottom_page_margin));
        cellInfoPagerAdapter = new CellInfoPagerAdapter(mContext);
        mDetailViewPager.setAdapter(cellInfoPagerAdapter);
        mDetailViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //   onCellInfoPagerChanged.onCellInfoPagerChanged(cells.get(position).aMapLatLng);
            }

            @Override
            public void onPageSelected(int position) {
                Logs.d("wenruisong", "onPageSelected" + position);
                int cellIndex = position;
                int poiCount = cellInfoPagerAdapter.getDataCount();
                if (poiCount > 1) {
                    cellIndex = position % poiCount;
                }

                Cell cell = cells.get(cellIndex);
                onCellInfoPagerChanged.onCellInfoPagerChanged(cell.aMapLatLng);
                    SelectedCellMarker.setSelected(aMap, cell);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    // 跳到适当的item
                    int item = calculateCurrentItem();
                    if (item != mDetailViewPager.getCurrentItem()) {
                        mDetailViewPager.setCurrentItem(item, false);
                    }
                }
            }
        });
    }

    void onCellMarkerClick(Cell cell)
    {

        mDetailViewPager.setVisibility(View.VISIBLE);
        mBottomActionView.setVisibility(View.GONE);
        SelectedCellMarker.setSelected(aMap, cell);
        cells= BasestationManager.getCellsFromBS(cell);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        float marginLeftRight;
        if(cells.size()>1)
            marginLeftRight = ResourcesUtil.getDimension(R.dimen.mapview_vp_margin_right);
        else
            marginLeftRight = ResourcesUtil.getDimension(R.dimen.mapview_layout_margin_left);
        lp.setMargins((int)marginLeftRight, 0, (int)marginLeftRight, 0);
        mDetailViewPager.setLayoutParams(lp);


        cellInfoPagerAdapter.setDates(cells);
        for (int i=0;i<cells.size();i++ ) {
            if(cells.get(i).cellid == cell.cellid)
            {
                setViewPagerCurrentItem(i, false);
                break;
            }
        }

        cellInfoPagerAdapter.notifyDataSetChanged();

    }

    void onBasestationMarkerClick(Cell cell)
    {
        SelectedBasestationMarker.setSelected(aMap, cell);
        cells= BasestationManager.getCellsFromBS(cell);
        cellInfoPagerAdapter.setDates(cells);
        mDetailViewPager.setVisibility(View.VISIBLE);
       // LinearLayout.LayoutParams lp =(LinearLayout.LayoutParams)mDetailViewPager.getLayoutParams();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        float marginLeftRight;
        if(cells.size()>1)
            marginLeftRight = ResourcesUtil.getDimension(R.dimen.mapview_vp_margin_right);
        else
            marginLeftRight = ResourcesUtil.getDimension(R.dimen.mapview_layout_margin_left);

        lp.setMargins((int)marginLeftRight, 0, (int)marginLeftRight, 0);
        mDetailViewPager.setLayoutParams(lp);
        mBottomActionView.setVisibility(View.GONE);
        cellInfoPagerAdapter.notifyDataSetChanged();

    }

    private int calculateCurrentItem() {
        int poiCount = cellInfoPagerAdapter.getDataCount();
        int item = mDetailViewPager.getCurrentItem();
        int poiIndex = item % poiCount;
        if (poiIndex < (poiCount / 2)) {
            // 显示第二屏item
            item = poiIndex + poiCount;
        } else {
            // 显示第一屏item
            item = poiIndex;
        }
        return item;
    }

    private void setViewPagerCurrentItem(int poiIndex, boolean smoothScroll) {
        int item = poiIndex;
        int poiCount = cellInfoPagerAdapter.getDataCount();
        if (poiCount > 1) {
            int current = mDetailViewPager.getCurrentItem();
            if (current == poiIndex) {
                item = calculateCurrentItem();
            } else {
                // 计算比较近的item
                int nextIndex = poiIndex + poiCount;
                if (Math.abs(current - nextIndex) < Math.abs(current - poiIndex)) {
                    item = nextIndex;
                }
            }
        }
        mDetailViewPager.setCurrentItem(item, smoothScroll);
    }

    View.OnClickListener onRouteBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mContext, RouteActivity.class);
            mContext.startActivity(intent);
        }
    };


    View.OnClickListener onNaviBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mContext, NaviActivity.class);
            Bundle bundle = new Bundle();

            bundle.putDouble(Constants.START_LAT,LocationHelper.location.getLatitude());
            bundle.putDouble(Constants.START_LNG,LocationHelper.location.getLongitude());

            bundle.putDouble(Constants.END_LAT,30.3212);
            bundle.putDouble(Constants.END_LNG,116.4312);
            intent.putExtra(Constants.NAVI_BUNDLE,bundle);
            mContext.startActivity(intent);
        }
    };

    public void showPoiInfo(Poi poi){
        if(mBottomPoiView.getVisibility() ==View.GONE) {
            mBottomPoiView.setVisibility(View.VISIBLE);
            mBottomCellView.setVisibility(View.GONE);
        }
        mBottomPoiView.setDate(poi);
    }

    public void restore(){
        mBottomActionView.setVisibility(View.VISIBLE);
        mDetailViewPager.setVisibility(View.GONE);
        mBottomPoiView.setVisibility(View.GONE);
        mBottomCellView.setVisibility(View.GONE);
    }



    View.OnClickListener onBasestaionBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mBottomPoiView.getVisibility()==View.VISIBLE)
            mBottomPoiView.setVisibility(View.GONE);
            else
            mBottomCellView.setVisibility(View.VISIBLE);
        }
    };


    public void registerOnCellInfoPagerListener(OnCellInfoPagerListener onCellInfoPagerChanged)
    {
        this.onCellInfoPagerChanged = onCellInfoPagerChanged;
    }

    public interface OnCellInfoPagerListener {
        void onCellInfoPagerChanged(LatLng latLng);
    }
}
