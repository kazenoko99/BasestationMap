package com.wenruisong.basestationmap.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.adapter.OfflineDownloadAdapter;
import com.wenruisong.basestationmap.offlinemap.AynscWorkListener;
import com.wenruisong.basestationmap.offlinemap.OfflineMapDownloadView;
import com.wenruisong.basestationmap.offlinemap.OfflineMapDownloadViewModel;
import com.wenruisong.basestationmap.offlinemap.OfflineMapDownloadViewModelImp;
import com.wenruisong.basestationmap.utils.Logs;

/**
 * 离线地图下载管理页面
 * <p/>
 * Created by yinjianhua on 15-5-28.
 */
public class OfflineMapDownloadFragment extends BackPressHandledFragment implements OfflineMapDownloadView {
    private ListView mListView;
    private OfflineDownloadAdapter mAdapter;
    private OfflineMapDownloadViewModel mViewModel;
    private View RecommendHeaderView;

    public static BaseFragment getInstance(Bundle bundle) {
        Logs.d("OfflineMapModule", "OfflineMapDownloadFragment getInstance");

        BaseFragment backPressHandledFragment = new OfflineMapDownloadFragment();
        backPressHandledFragment.mBundle = bundle;
        return backPressHandledFragment;
    }

    @Override
    public View inflateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_offline_map_download, null);
        mListView = (ListView) root.findViewById(R.id.lv_download);

        mViewModel = new OfflineMapDownloadViewModelImp(this, this);

        initList(mListView);

        return root;
    }

    private void initList(ListView listView) {
        if (mViewModel.isShouldShowRecommendView()) {
            mAdapter = new OfflineDownloadAdapter(getActivity(), mViewModel.getRecommendCitys(),
                    null, mViewModel.getDownloadCtrlClickListener(), true);
            RecommendHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.header_offline_map_download_recommend, null);
            mListView.addHeaderView(RecommendHeaderView);


        } else {
            mAdapter = new OfflineDownloadAdapter(getActivity(), mViewModel.getAllDownloadCitys(),
                    mViewModel.getCanUpdateCityList(), mViewModel.getDownloadCtrlClickListener(), false);

            mAdapter.setCityDownloadCompleteMap(mViewModel.getCityDownloadCompleteMap());
            mAdapter.setCityUnzipCompleteMap(mViewModel.getCityUnzipCompleteMap());

            listView.setOnItemLongClickListener(mViewModel);
        }


        listView.setAdapter(mAdapter);
    }


    // 数据改变时更新UI
    @Override
    public void updateList() {
        mAdapter.setCityList(mViewModel.getAllDownloadCitys());
//        mAdapter.setDownloadingCityName(mViewMode.getDownloadingCityName());
//        mAdapter.setDownloadingComplete(mViewModel.getDownloadingComplete());
//        mAdapter.setUnzipingCityName(mViewModel.getUnzipingCityName());
//        mAdapter.setUnzipingComplete(mViewModel.getUnzipingComplete());

        mAdapter.setCityDownloadCompleteMap(mViewModel.getCityDownloadCompleteMap());
        mAdapter.setCityUnzipCompleteMap(mViewModel.getCityUnzipCompleteMap());

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void dismissRecommendView() {
        mListView.removeHeaderView(RecommendHeaderView);
        mListView.setOnItemLongClickListener(mViewModel);
        mAdapter.setIsShowRecommendList(false);

        updateList();
    }

    @Override
    public void showDialog(String tip, boolean isCancelable, int delayTime) {

    }

    @Override
    public void hideDialog() {

    }


    @Override
    public void doWorkBackground(AynscWorkListener listener) {
        doWorkAynsc(listener,false);
    }


    @Override
    public void onDestroy() {
        Logs.d("OfflineMapModule", "OfflineMapDownloadFragment onDestroy");

        super.onDestroy();
        mViewModel.onDestory();
    }

}
