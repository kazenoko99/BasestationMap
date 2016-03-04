package com.wenruisong.basestationmap.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.wenruisong.basestationmap.MainActivity;
import com.wenruisong.basestationmap.R;
import com.wenruisong.basestationmap.adapter.OfflineDownloadAdapter;
import com.wenruisong.basestationmap.map.OfflineMapManager;
import com.wenruisong.basestationmap.utils.Logs;

import java.util.ArrayList;


public class OfflineMapDownloadFragment extends BackPressHandledFragment implements MKOfflineMapListener {
    private ListView mListView;
    private OfflineDownloadAdapter mAdapter;
    private MKOfflineMap mOffline = null;
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

        setFragShower((MainActivity) getActivity());
        setMapGlobal((MainActivity) getActivity());

        mOffline = OfflineMapManager.getInstance().mOffline;
        mOffline.init(this);
        mAdapter = new OfflineDownloadAdapter(getActivity(), mOffline );
        mListView.setAdapter(mAdapter);

        return root;
    }




    @Override
    public void onDestroy() {
        Logs.d("OfflineMapModule", "OfflineMapDownloadFragment onDestroy");
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onGetOfflineMapState(int type, int state) {
        mAdapter.setCityList(mOffline);
        mAdapter.notifyDataSetChanged();
        //Toast.makeText(getActivity(), "start download" + update.cityName + update.ratio, Toast.LENGTH_SHORT).show();
    }
}
