package com.wenruisong.basestationmap.offlinemap;

import android.text.TextWatcher;
import android.view.View;
import android.widget.ExpandableListView;

import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapProvince;

import java.util.HashMap;
import java.util.List;

/**
 * Created by yinjianhua on 15-6-12.
 */
public interface OfflineMapCityViewModel extends OfflineMapLoader.OfflineMapDownloadListener,
        TextWatcher,
        ExpandableListView.OnGroupCollapseListener,
        ExpandableListView.OnGroupExpandListener {

    public List<OfflineMapProvince> getProvinceList();

    public HashMap<Object, List<OfflineMapCity>> getCityMap();

    public boolean[] getGroupOpenArray();

    public void initData();

    public void onDestory();

    public String getDownloadingCityName();

    public int getDownloadingComplete();

    public String getUnzipingCityName();

    public int getUnzipingComplete();

    public View.OnClickListener getDownloadBtnListener();

    public List<OfflineMapCity> updateHotCityList();
}
