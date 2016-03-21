package com.wenruisong.basestationmap.map;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.adapter.CellInfoPagerAdapter;
import com.wenruisong.basestationmap.basestation.Cell;
import com.wenruisong.basestationmap.basestation.BasestationManager;
import com.wenruisong.basestationmap.basestation.SelectedCellMarker;
import com.wenruisong.basestationmap.utils.Logs;
import com.wenruisong.basestationmap.utils.ResourcesUtil;
import com.wenruisong.basestationmap.view.BottomCellView;
import com.wenruisong.basestationmap.view.WrapContentHeightViewPager;

import java.util.ArrayList;

public class MapBottomController  {
    public WrapContentHeightViewPager mDetailViewPager;
    private LinearLayout mBottomDetailView;
    private View mBtmSeparatorLine;
    private LinearLayout mBottomLocActionView;
    private TextView mBottomLocNameView;
    private TextView mBottomLocAddrView;
    private TextView mBottomLocDistView;
    private TextView mBottomDetailIcon;
    private TextView map_bottom_basestation;
    private TextView map_bottom_route;
    private TextView map_bottom_navi;
    public BottomCellView bottomCellView;
    public View bottomCell;
    OnCellInfoPagerListener onCellInfoPagerChanged;
    public LinearLayout mBottomActionView;
    private CellInfoPagerAdapter cellInfoPagerAdapter;
    private Context mContext;
    private BaiduMap mBaiduMap;
    private ArrayList<Cell> cells = new ArrayList<>();

    private int mBtmDetailTwoLineHeight;
    private int mBtmDetailOneLineHeight;

    public MapBottomController(Context context) {
        this.mContext = context;
        bottomCellView = new BottomCellView();
        bottomCell = bottomCellView.initView(context);
    }

    public void setBaiduMap( BaiduMap baiduMap) {
        mBaiduMap = baiduMap;
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
        mBottomDetailView = (LinearLayout)root.findViewById(R.id.map_bottom_detail_layout);
        mBottomDetailView.setVisibility(View.GONE);
        mBottomDetailView.addView(bottomCell);
        mBottomLocActionView =(LinearLayout) root.findViewById(R.id.map_bottom_act_layout);
        mBottomActionView = (LinearLayout) root.findViewById(R.id.map_bottom_actionview);
        mBottomLocAddrView = (TextView) root.findViewById(R.id.map_bottom_address);
        mBottomLocDistView = (TextView) root.findViewById(R.id.map_bottom_distance);
        mBottomDetailIcon = (TextView) root.findViewById(R.id.map_bottom_detail);
        mDetailViewPager = (WrapContentHeightViewPager) root.findViewById(R.id.map_bottom_pager);

        mDetailViewPager.setOffscreenPageLimit(2);
        mDetailViewPager.setPageMargin(
                ResourcesUtil.getDimensionPixelSize(R.dimen.map_bottom_page_margin));
        cellInfoPagerAdapter = new CellInfoPagerAdapter(mContext);
        mDetailViewPager.setAdapter(cellInfoPagerAdapter);
        mDetailViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //   onCellInfoPagerChanged.onCellInfoPagerChanged(cells.get(position).baiduLatLng);
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
                onCellInfoPagerChanged.onCellInfoPagerChanged(cell.baiduLatLng);
                    SelectedCellMarker.setSelected(mBaiduMap, cell);
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
        SelectedCellMarker.setSelected(mBaiduMap, cell);
        cells= BasestationManager.getCellsFromBS(cell);
        cellInfoPagerAdapter.setDates(cells);
        for (int i=0;i<cells.size();i++ ) {
            if(cells.get(i).cellid == cell.cellid)
            {
                setViewPagerCurrentItem(i, false);
                break;
            }
        }
        mDetailViewPager.setVisibility(View.VISIBLE);
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



    View.OnClickListener onBasestaionBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           if(mBottomDetailView.getVisibility() ==View.GONE )
               mBottomDetailView.setVisibility(View.VISIBLE);
            else
               mBottomDetailView.setVisibility(View.GONE);
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
