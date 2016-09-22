package com.wenruisong.basestationmap.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.adapter.OfflineCityExpandableListAdapter;
import com.wenruisong.basestationmap.adapter.OfflineCityHeaderListAdapter;
import com.wenruisong.basestationmap.adapter.OfflineCitySearchResultAdapter;
import com.wenruisong.basestationmap.offlinemap.AynscWorkListener;
import com.wenruisong.basestationmap.offlinemap.OfflineMapCityView;
import com.wenruisong.basestationmap.offlinemap.OfflineMapCityViewModel;
import com.wenruisong.basestationmap.offlinemap.OfflineMapCityViewModelImp;
import com.wenruisong.basestationmap.utils.Logs;
import com.wenruisong.basestationmap.utils.ResourcesUtil;

import java.util.List;

public class OfflineMapCityFragment extends BackPressHandledFragment implements OfflineMapCityView {

    private ExpandableListView expandableListView;
    private ListView mSearchResultListView;
    private EditText mSearchView;
    private OfflineCitySearchResultAdapter mSearchAdapter;
    private OfflineCityExpandableListAdapter mCityAdapter;
    private OfflineCityHeaderListAdapter mHotCityAdapter;
    private OfflineMapCityViewModel mViewModel;

    public static BaseFragment getInstance(Bundle bundle) {
        Logs.d("OfflineMapModule", "OfflineMapCityFragment getInstance");

        BaseFragment backPressHandledFragment = new OfflineMapCityFragment();
        backPressHandledFragment.mBundle = bundle;
        return backPressHandledFragment;
    }

    @Override
    public View inflateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = initView(inflater);
        mViewModel = new OfflineMapCityViewModelImp(this, this);
        mViewModel.initData();
        addHotCityList();
        setAdapter();
        setListener();
        updateCityList();

        return root;
    }

    @Override
    public void setToDoWorkAynsc(AynscWorkListener aynscWorkListener, boolean showDialogInner) {
        doWorkAynsc(aynscWorkListener, showDialogInner);
    }

    private View initView(LayoutInflater inflater) {
        View root = inflater.inflate(R.layout.fragment_offline_map_city, null);
        expandableListView = (ExpandableListView) root.findViewById(R.id.list);
        mSearchView = (EditText) root.findViewById(R.id.et_search);
        mSearchResultListView = (ListView) root.findViewById(R.id.lv_search_result);

        mSearchView.setHint(ResourcesUtil.getString(R.string.offline_map_city_search_hint));
        expandableListView.setGroupIndicator(null);

        return root;
    }


    private void setListener() {
        // 监听搜索栏
        mSearchView.addTextChangedListener(mViewModel);

        expandableListView.setOnGroupCollapseListener(mViewModel);
        expandableListView.setOnGroupExpandListener(mViewModel);

        // 设置二级item点击的监听器
//        expandableListView.setOnChildClickListener(mViewModel);


        // 点击搜索框显示光标
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setCursorVisible(true);
                mSearchView.invalidate();
            }
        });

        // 点击列表则隐藏光标并关闭软键盘
        expandableListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSearchView.setCursorVisible(false);

                // 关闭系统软键盘
                View view = getContext().getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager inputmanger = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                return false;
            }
        });

        // 点击列表则隐藏光标并关闭软键盘
        mSearchResultListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSearchView.setCursorVisible(false);

                // 关闭系统软键盘
                View view = getContext().getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager inputmanger = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                return false;
            }
        });
    }

    private void setAdapter() {
        // 为列表绑定数据源
        mCityAdapter = new OfflineCityExpandableListAdapter(getContext()
                , mViewModel.getProvinceList(), mViewModel.getCityMap(), mViewModel.getDownloadBtnListener());
        expandableListView.setAdapter(mCityAdapter);

        mSearchAdapter = new OfflineCitySearchResultAdapter(getContext(), null, mViewModel.getDownloadBtnListener());
        mSearchResultListView.setAdapter(mSearchAdapter);
    }

    private void addHotCityList() {

        List<OfflineMapCity> hotCityList = mViewModel.updateHotCityList();

        mHotCityAdapter = new OfflineCityHeaderListAdapter(getContext(), hotCityList, mViewModel.getDownloadBtnListener());

        View hotCityLayout = LayoutInflater.from(getContext()).inflate(R.layout.header_offline_map_city_list, null);
        ListView hotCityListView = (ListView) hotCityLayout.findViewById(R.id.lv_offline_hot_city_list);
        hotCityListView.setAdapter(mHotCityAdapter);


        expandableListView.addHeaderView(hotCityLayout);

    }

    @Override
    public void updateCityList() {
        mCityAdapter.setCityHashMap(mViewModel.getCityMap());
        mCityAdapter.setProvinceList(mViewModel.getProvinceList());
        mCityAdapter.setGroupOpenArray(mViewModel.getGroupOpenArray());

//        mCityAdapter.setDownloadingCityName(mViewModel.getDownloadingCityName());
//        mCityAdapter.setDownloadingComplete(mViewModel.getDownloadingComplete());
//        mCityAdapter.setUnzipingCityName(mViewModel.getUnzipingCityName());
//        mCityAdapter.setUnzipingComplete(mViewModel.getUnzipingComplete());

        mCityAdapter.notifyDataSetChanged();


        mHotCityAdapter.updateList(mViewModel.updateHotCityList());
    }

    @Override
    public void updateSearchList(List<OfflineMapCity> result) {
        mSearchAdapter.updateResultList(result);
    }

    @Override
    public void setCityListViewVisibility(int visibility) {
        expandableListView.setVisibility(visibility);
    }

    @Override
    public void setSearchListViewVisibility(int visibility) {
        mSearchResultListView.setVisibility(visibility);
    }

    @Override
    public void setSearchEditHint(String text) {
        mSearchView.setHint(text);
    }

    @Override
    public OfflineMapCity getSearchListItemName(int pos) {
        return mSearchAdapter.getItem(pos);
    }



    @Override
    public void onDestroy() {
        Logs.d("OfflineMapModule", "OfflineMapCityFragment onDestroy");

        super.onDestroy();
        mViewModel.onDestory();
    }

}
